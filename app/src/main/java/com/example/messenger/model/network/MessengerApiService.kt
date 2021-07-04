package com.example.messenger.model.network

import com.example.messenger.model.entity.User
import com.example.messenger.model.network.status.StatusResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface MessengerApiService {
    @POST("users")
    suspend fun saveUser(@Body user: User): StatusResponse
}