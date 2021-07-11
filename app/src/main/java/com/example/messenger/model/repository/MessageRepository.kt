package com.example.messenger.model.repository

import com.example.messenger.model.db.dao.MessageDao
import com.example.messenger.model.entity.Message
import kotlinx.coroutines.flow.Flow

class MessageRepository(private val messageDao: MessageDao) {
    fun getLastMessages(): Flow<List<Message>> {
        return messageDao.getLastMessages()
    }
}