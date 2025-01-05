package com.example.lineup2025.api

import com.example.lineup2025.auth.model.AvatarRequest
import com.example.lineup2025.auth.model.AvatarResponse
import com.example.lineup2025.model.AccessAvatar
import com.example.lineup2025.model.QRCodeResponse
import com.example.lineup2025.model.QRScannerRequest
import com.example.lineup2025.model.QRScannerResponse
import com.example.lineup2025.model.RouteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MainAPI {
    @POST("user/store-avatar")
    suspend fun storeAvatar(@Body avatar: AvatarRequest): Response<AvatarResponse>

    @GET("user/generate-qr")
    suspend fun getQRCode(): Response<QRCodeResponse>

    @POST("user/scan-qrcode")
    suspend fun scanQRCode(@Body code: QRScannerRequest): Response<QRScannerResponse>

    @GET("user/get-avatar")
    suspend fun accessAvatar():Response<AccessAvatar>

    @GET("user/refresh-location")
    suspend fun getRoute():Response<RouteResponse>

}