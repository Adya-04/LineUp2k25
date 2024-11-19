package com.example.lineup2025.api

import com.example.lineup2025.auth.model.LoginRequestBody
import com.example.lineup2025.auth.model.LoginResponseBody
import com.example.lineup2025.auth.model.SignUpRequestBody
import com.example.lineup2025.auth.model.SignUpResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface APIService {
    @POST("user/signup")
    suspend fun signUp(@Body signUp: SignUpRequestBody): Response<SignUpResponseBody>

    @POST("user/login")
    suspend fun login(@Body login: LoginRequestBody): Response<LoginResponseBody>
}