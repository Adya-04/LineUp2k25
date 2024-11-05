package com.example.lineup2025.auth.login.repository

import com.example.lineup2025.auth.login.model.LoginRequestBody
import com.example.lineup2025.auth.login.model.LoginResponseBody
import com.example.lineup2025.network.APIService
import retrofit2.Response

class LoginRepository(private val apiService: APIService) {
    suspend fun login(loginRequestBody: LoginRequestBody): Response<LoginResponseBody>{
        return apiService.login(loginRequestBody)
    }
}