package com.example.messenger.model.repository

import com.example.messenger.model.db.dao.MessageDao
import com.example.messenger.model.entity.Message
import com.example.messenger.model.entity.toMessageForSerialization
import com.example.messenger.model.network.MessengerApi
import com.example.messenger.model.network.status.Status
import com.example.messenger.model.network.status.StatusResponse
import io.reactivex.rxjava3.core.Observable

class MessageRepository(private val messageDao: MessageDao) {

    private val retrofitService = MessengerApi.retrofitService

    suspend fun pullMessagesOut(id: Long, deleteFromServer: Boolean): List<Message> {
        return retrofitService.getMessages(id, deleteFromServer)
    }

    fun getMessagesWithCompanion(companionId: Long): Observable<List<Message>> {
        return messageDao.getMessagesWithCompanion(companionId)
    }

    suspend fun save(message: Message) {
        val messageForSerialization = message.toMessageForSerialization()
        val statusResponse: StatusResponse = retrofitService.saveMessage(messageForSerialization)
        when (statusResponse.status) {
            Status.SUCCESS -> {
                message.id = statusResponse.id
                message.time = statusResponse.time
                messageDao.save(message)
            }
            Status.FAIL -> {
                throw FailStatusException()
            }
            else -> {
                throw Exception()
            }
        }
    }

    suspend fun saveLocally(message: Message) {
        messageDao.save(message)
    }
}