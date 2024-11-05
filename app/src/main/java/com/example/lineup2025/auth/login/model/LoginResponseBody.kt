package com.example.lineup2025.auth.login.model

data class LoginResponseBody (
    val name :String,
    val protocol: String,
    val code: String,
    val token :String,
    val message: String,
    val url: String,
    val scannedCodes:ArrayList<String>
)