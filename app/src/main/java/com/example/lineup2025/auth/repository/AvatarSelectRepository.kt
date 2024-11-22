package com.example.lineup2025.auth.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.lineup2025.api.APIService
import com.example.lineup2025.auth.model.AvatarRequest
import com.example.lineup2025.auth.model.AvatarResponse
import com.example.lineup2025.utils.NetworkResult
import org.json.JSONObject
import javax.inject.Inject

class AvatarSelectRepository @Inject constructor(private val apiService: APIService) {
    private val _storeAvatarResponseLiveData = MutableLiveData<NetworkResult<AvatarResponse>>()
    val storeAvatarResponseLiveData: LiveData<NetworkResult<AvatarResponse>>
        get() = _storeAvatarResponseLiveData

    suspend fun storeAvatar(token: String, avatarRequest: AvatarRequest){
        _storeAvatarResponseLiveData.postValue(NetworkResult.Loading())
        val response = apiService.storeAvatar(token,avatarRequest)
        if(response.isSuccessful){
            val responseBody = response.body()
            if(responseBody != null){
                _storeAvatarResponseLiveData.postValue(NetworkResult.Success(responseBody))
            }
            else{
                _storeAvatarResponseLiveData.postValue(NetworkResult.Error("Response body is null"))
            }
        }
        else if(response.errorBody()!= null){
            val errObj = JSONObject(response.errorBody()!!.charStream().readText())
            _storeAvatarResponseLiveData.postValue(NetworkResult.Error(errObj.getString("message")))
        }
        else {
            _storeAvatarResponseLiveData.postValue(NetworkResult.Error("Something went wrong"))
        }
    }
}