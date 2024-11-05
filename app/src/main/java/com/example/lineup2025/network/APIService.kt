package com.example.lineup2025.network

import com.example.lineup2025.auth.login.model.LoginRequestBody
import com.example.lineup2025.auth.login.model.LoginResponseBody
import com.example.lineup2025.auth.signup.model.SignUpRequestBody
import com.example.lineup2025.auth.signup.model.SignUpResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface APIService {
    @POST("user/signup")
    suspend fun signUp(@Body signUp: SignUpRequestBody): Response<SignUpResponseBody>

    @POST("user/login")
    suspend fun login(@Body login: LoginRequestBody): Response<LoginResponseBody>
}