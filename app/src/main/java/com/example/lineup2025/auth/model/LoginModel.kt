package com.example.lineup2025.auth.model

data class LoginRequestBody(
    val password: String,
    val zealId: String
)

data class LoginResponseBody (
    val name :String,
    val protocol: String,
    val code: String,
    val token :String,
    val message: String,
    val url: String,
    val scannedCodes:ArrayList<String>
)