package com.example.lineup2025.auth.model

data class SignUpRequestBody (
    val email: String,
    val password: String,
    val name: String,
    val zealId: String
)

data class SignUpResponseBody (
    val protocol: String,
    val code: Int,
    val url: String,
    val message: String,
    val token: String,
    val scannedCodes:ArrayList<String>
)