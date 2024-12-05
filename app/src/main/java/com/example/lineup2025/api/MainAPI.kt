package com.example.lineup2025.api

import com.example.lineup2025.auth.model.AvatarRequest
import com.example.lineup2025.auth.model.AvatarResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MainAPI {
    @POST("user/store-avatar")
    suspend fun storeAvatar(@Body avatar: AvatarRequest): Response<AvatarResponse>

}