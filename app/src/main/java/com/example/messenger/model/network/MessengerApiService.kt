package com.example.messenger.model.network

import com.example.messenger.model.entity.User
import com.example.messenger.model.network.status.StatusResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MessengerApiService {
    @POST("users")
    suspend fun saveUser(@Body user: User): StatusResponse

    @GET("users")
    suspend fun getUserByTag(@Query("tag") tag: String): Response<User>
}