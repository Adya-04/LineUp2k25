package com.example.lineup2025.auth.model

data class generateOtpRequest(
    val email: String
)

data class generateOtpResponse(
    val message: String
)

data class verifyOtpRequest(
    val email: String,
    val otp: String
)

data class verifyOtpResponse(
    val message: String
)