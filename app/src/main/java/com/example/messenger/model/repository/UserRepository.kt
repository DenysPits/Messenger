package com.example.messenger.model.repository

import com.example.messenger.model.db.dao.UserDao
import com.example.messenger.model.entity.ChatPreview
import com.example.messenger.model.entity.User
import com.example.messenger.model.network.MessengerApi
import com.example.messenger.model.network.status.Status
import com.example.messenger.model.network.status.StatusResponse
import kotlinx.coroutines.flow.Flow
import java.net.HttpURLConnection

class UserRepository(private val userDao: UserDao) {

    private val retrofitService = MessengerApi.retrofitService

    suspend fun saveGlobally(user: User) {
        val statusResponse: StatusResponse = retrofitService.saveUser(user)
        when (statusResponse.status) {
            Status.SUCCESS -> {
                user.id = statusResponse.id
                saveLocally(user)
            }
            Status.FAIL -> {
                throw FailStatusException()
            }
            Status.TAG_IS_TAKEN -> {
                throw TagIsTakenException()
            }
        }
    }

    suspend fun saveLocally(user: User) {
        userDao.save(user)
    }

    suspend fun isTableEmpty(): Boolean {
        return userDao.countRows() == 0
    }

    suspend fun getByTag(tag: String): User {
        val response = retrofitService.getUserByTag(tag)
        if (response.code() == HttpURLConnection.HTTP_OK) {
            return response.body() ?: throw UserNotFoundException()
        }
        throw Exception("Not ok http status")
    }

    fun getChatPreviews(): Flow<List<ChatPreview>> {
        return userDao.getChatPreviews()
    }
}