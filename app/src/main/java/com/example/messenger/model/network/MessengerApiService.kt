package com.example.messenger.model.network

import com.example.messenger.model.entity.Message
import com.example.messenger.model.entity.MessageForSerialization
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

    @POST("users")
    suspend fun updateUser(@Body user: User, @Query("update") id: Long): StatusResponse

    @GET("users")
    suspend fun getUserByTag(@Query("tag") tag: String): Response<User>

    @GET("users")
    suspend fun getUserById(@Query("id") id: Long): Response<User>

    @POST("messages")
    suspend fun saveMessage(@Body message: MessageForSerialization): StatusResponse

    @GET("messages")
    suspend fun getMessages(
        @Query("id") id: Long,
        @Query("del") deleteMessages: Boolean = false
    ): List<Message>
}