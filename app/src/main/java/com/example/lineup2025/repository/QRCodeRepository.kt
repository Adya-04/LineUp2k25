package com.example.lineup2025.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.lineup2025.api.MainAPI
import com.example.lineup2025.model.QRCodeResponse
import com.example.lineup2025.utils.NetworkResult
import org.json.JSONObject
import java.net.SocketTimeoutException
import javax.inject.Inject

class QRCodeRepository @Inject constructor(private val mainAPI: MainAPI) {
    private val _qrResponseLivedata = MutableLiveData<NetworkResult<QRCodeResponse>>()
    val qrResponseLiveData: LiveData<NetworkResult<QRCodeResponse>>
        get() = _qrResponseLivedata

    suspend fun getQRCode(){
        _qrResponseLivedata.postValue(NetworkResult.Loading())
        try {
            val response = mainAPI.getQRCode()
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    _qrResponseLivedata.postValue(NetworkResult.Success(responseBody))
                } else {
                    _qrResponseLivedata.postValue(NetworkResult.Error("Response body is null"))
                }
            } else if (response.errorBody() != null) {
                val errObj = JSONObject(response.errorBody()!!.charStream().readText())
                _qrResponseLivedata.postValue(NetworkResult.Error(errObj.getString("message")))
            } else {
                _qrResponseLivedata.postValue(NetworkResult.Error("Something went wrong"))
            }
        }
        catch (e: SocketTimeoutException){
            _qrResponseLivedata.postValue(NetworkResult.Error("Please try again!"))
        } catch (e: Exception){
            _qrResponseLivedata.postValue(NetworkResult.Error("Unexpected error occurred"))
        }

    }
}