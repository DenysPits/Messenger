package com.example.messenger.model.repository

import com.example.messenger.model.db.dao.UserDao
import com.example.messenger.model.entity.User
import com.example.messenger.model.network.MessengerApi
import com.example.messenger.model.network.status.Status

class UserRepository(private val userDao: UserDao) {
    suspend fun save(user: User) {
        val statusResponse = MessengerApi.retrofitService.saveUser(user)
        when (statusResponse.status) {
            Status.SUCCESS -> {
                user.id = statusResponse.id
                userDao.save(user)
            }
            Status.FAIL -> {
                throw FailStatusException()
            }
            Status.TAG_IS_TAKEN -> {
                throw TagIsTakenException()
            }
        }
    }

    suspend fun isTableEmpty(): Boolean {
        return userDao.countRows() == 0
    }
}