package com.example.lineup2025.auth.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.lineup2025.api.MainAPI
import com.example.lineup2025.auth.model.AvatarRequest
import com.example.lineup2025.auth.model.AvatarResponse
import com.example.lineup2025.utils.NetworkResult
import org.json.JSONObject
import javax.inject.Inject

class AvatarSelectRepository @Inject constructor(private val mainApi: MainAPI) {
    private val _storeAvatarResponseLiveData = MutableLiveData<NetworkResult<AvatarResponse>>()
    val storeAvatarResponseLiveData: LiveData<NetworkResult<AvatarResponse>>
        get() = _storeAvatarResponseLiveData

    suspend fun storeAvatar(avatarRequest: AvatarRequest){
        _storeAvatarResponseLiveData.postValue(NetworkResult.Loading())
        val response = mainApi.storeAvatar(avatarRequest)
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