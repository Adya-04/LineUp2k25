package com.example.lineup2025.auth.model

data class SignUpRequestBody (
    val email: String,
    val password: String,
    val name: String,
    val zealId: String
)