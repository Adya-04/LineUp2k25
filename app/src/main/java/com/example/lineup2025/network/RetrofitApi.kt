package com.example.lineup2025.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitApi {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://lineup-backend.onrender.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val apiInterface: APIService by lazy {
        retrofit.create(APIService::class.java)
    }
}