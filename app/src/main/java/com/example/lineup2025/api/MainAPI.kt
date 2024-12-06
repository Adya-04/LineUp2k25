package com.example.lineup2025.api

import com.example.lineup2025.auth.model.AvatarRequest
import com.example.lineup2025.auth.model.AvatarResponse
import com.example.lineup2025.model.QRCodeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MainAPI {
    @POST("user/store-avatar")
    suspend fun storeAvatar(@Body avatar: AvatarRequest): Response<AvatarResponse>

    @GET("user/generate-qr")
    suspend fun getQRCode(): Response<QRCodeResponse>

}