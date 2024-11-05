package com.example.lineup2025.auth.signup.repository

import com.example.lineup2025.network.APIService
import com.example.lineup2025.auth.signup.model.SignUpRequestBody
import com.example.lineup2025.auth.signup.model.SignUpResponseBody
import retrofit2.Response

class SignUpRepository(private val apiService: APIService) {
    suspend fun signUp(signUpRequestBody: SignUpRequestBody): Response<SignUpResponseBody> {
        return apiService.signUp(signUpRequestBody)
    }
}