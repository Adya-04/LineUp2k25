package com.example.lineup2025.api

import com.example.lineup2025.auth.model.LoginRequestBody
import com.example.lineup2025.auth.model.LoginResponseBody
import com.example.lineup2025.auth.model.SignUpRequestBody
import com.example.lineup2025.auth.model.SignUpResponseBody
import com.example.lineup2025.auth.model.generateOtpRequest
import com.example.lineup2025.auth.model.generateOtpResponse
import com.example.lineup2025.auth.model.verifyOtpRequest
import com.example.lineup2025.auth.model.verifyOtpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserAPI {
    @POST("user/signup")
    suspend fun signUp(@Body signUp: SignUpRequestBody): Response<SignUpResponseBody>

    @POST("user/login")
    suspend fun login(@Body login: LoginRequestBody): Response<LoginResponseBody>

    @POST("api/otp/generate-otp")
    suspend fun generateOtp(@Body generateOtp : generateOtpRequest): Response<generateOtpResponse>

    @POST("api/otp/verify-otp")
    suspend fun verifyOtp(@Body verifyOtp: verifyOtpRequest): Response<verifyOtpResponse>
}