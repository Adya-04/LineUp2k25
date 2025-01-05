package com.example.lineup2025.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.lineup2025.api.MainAPI
import com.example.lineup2025.model.AccessAvatar
import com.example.lineup2025.model.QRCodeResponse
import com.example.lineup2025.utils.NetworkResult
import org.json.JSONObject
import java.net.SocketTimeoutException
import javax.inject.Inject

class AccessAvatarRepository @Inject constructor(private val mainAPI: MainAPI) {
    private val _accessAvatarResponseLivedata = MutableLiveData<NetworkResult<AccessAvatar>>()
    val accessAvatarResponseLiveData: LiveData<NetworkResult<AccessAvatar>>
        get() = _accessAvatarResponseLivedata

    suspend fun accessAvatar(){
        _accessAvatarResponseLivedata.postValue(NetworkResult.Loading())
        try {
            val response = mainAPI.accessAvatar()
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    _accessAvatarResponseLivedata.postValue(NetworkResult.Success(responseBody))
                } else {
                    _accessAvatarResponseLivedata.postValue(NetworkResult.Error("Response body is null"))
                }
            } else if (response.errorBody() != null) {
                val errObj = JSONObject(response.errorBody()!!.charStream().readText())
                _accessAvatarResponseLivedata.postValue(NetworkResult.Error(errObj.getString("message")))
            } else {
                _accessAvatarResponseLivedata.postValue(NetworkResult.Error("Something went wrong"))
            }
        }
        catch (e: SocketTimeoutException){
            _accessAvatarResponseLivedata.postValue(NetworkResult.Error("Please try again!"))
        } catch (e: Exception){
            _accessAvatarResponseLivedata.postValue(NetworkResult.Error("Unexpected error occurred"))
        }
    }
}