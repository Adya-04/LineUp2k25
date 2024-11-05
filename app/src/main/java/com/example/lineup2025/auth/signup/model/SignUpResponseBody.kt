package com.example.lineup2025.auth.signup.model

data class SignUpResponseBody (
    val protocol: String,
    val code: Int,
    val url: String,
    val message: String,
    val token: String,
    val scannedCodes:ArrayList<String>
)