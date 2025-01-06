package com.example.lineup2025.model

data class QRScannerRequest(
    val code: String
)

data class QRScannerResponse(
    val message: String,
    val scannedCodes: ArrayList<String>
)