package com.example.messenger.model.repository

import com.example.messenger.model.db.dao.UserDao
import com.example.messenger.model.entity.User
import com.example.messenger.model.entity.changeBase64ToPath
import com.example.messenger.model.network.MessengerApi
import com.example.messenger.model.network.status.Status
import com.example.messenger.model.network.status.StatusResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.Response
import java.net.HttpURLConnection

class UserRepository(private val userDao: UserDao) {

    private val retrofitService = MessengerApi.retrofitService

    suspend fun saveGlobally(user: User) {
        val statusResponse: StatusResponse = retrofitService.saveUser(user)
        processStatusResponse(statusResponse, user)
    }

    suspend fun updateGlobally(user: User) {
        val statusResponse: StatusResponse = retrofitService.updateUser(user, user.id)
        processStatusResponse(statusResponse, user)
    }

    private suspend fun processStatusResponse(statusResponse: StatusResponse, user: User) {
        when (statusResponse.status) {
            Status.SUCCESS -> {
                user.id = statusResponse.id
                user.changeBase64ToPath()
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

    suspend fun isUserInDatabase(id: Long): Boolean {
        return userDao.isUserInDatabase(id) == 1
    }

    suspend fun getByTag(tag: String): User {
        val response = retrofitService.getUserByTag(tag)
        if (response.code() == HttpURLConnection.HTTP_OK) {
            return response.body() ?: throw UserNotFoundException()
        }
        throw Exception("Not ok http status")
    }

    suspend fun getById(id: Long): User {
        val response = retrofitService.getUserById(id)
        return validateResponse(response)
    }

    fun getMyUser(): Observable<User> {
        return userDao.getMyUser()
    }

    private fun validateResponse(response: Response<User>): User {
        if (response.code() == HttpURLConnection.HTTP_OK) {
            return response.body() ?: throw UserNotFoundException()
        }
        throw Exception("Not ok http status")
    }

    suspend fun getMyId(): Long = userDao.getMyId()

    fun getUser(id: Long): Observable<User> {
        return userDao.getUser(id)
    }
}