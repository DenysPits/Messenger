package com.example.messenger.data.network

import com.example.messenger.data.entity.User
import com.example.messenger.data.network.status.StatusResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface MessengerApiService {
    @POST("users")
    suspend fun saveUser(@Body user: User): StatusResponse
}