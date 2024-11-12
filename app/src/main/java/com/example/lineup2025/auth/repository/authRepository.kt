package com.example.lineup2025.auth.repository

import com.example.lineup2025.auth.model.LoginRequestBody
import com.example.lineup2025.auth.model.LoginResponseBody
import com.example.lineup2025.auth.model.SignUpRequestBody
import com.example.lineup2025.auth.model.SignUpResponseBody
import com.example.lineup2025.network.APIService
import retrofit2.Response

class LoginRepository(private val apiService: APIService) {
    suspend fun login(loginRequestBody: LoginRequestBody): Response<LoginResponseBody>{
        return apiService.login(loginRequestBody)
    }
}

class SignUpRepository(private val apiService: APIService) {
    suspend fun signUp(signUpRequestBody: SignUpRequestBody): Response<SignUpResponseBody> {
        return apiService.signUp(signUpRequestBody)
    }
}