package com.example.messenger.data.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object MessengerApi {
    private const val BASE_URL = "http://3.142.181.116:8000/api/"
    //private const val BASE_URL = "http://localhost:8000/api"

    private lateinit var retrofit: Retrofit

    val retrofitService: MessengerApiService by lazy {
        configureRetrofit()
        retrofit.create(MessengerApiService::class.java)
    }

    private fun configureRetrofit() {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        val gson = GsonBuilder().create()
        retrofit = Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BASE_URL)
            .build()
    }
}