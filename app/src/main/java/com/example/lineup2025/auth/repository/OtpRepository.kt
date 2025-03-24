package com.example.lineup2025.auth.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.lineup2025.api.UserAPI
import com.example.lineup2025.auth.model.generateOtpRequest
import com.example.lineup2025.auth.model.generateOtpResponse
import com.example.lineup2025.auth.model.verifyOtpRequest
import com.example.lineup2025.auth.model.verifyOtpResponse
import com.example.lineup2025.utils.NetworkResult
import org.json.JSONObject
import java.net.SocketTimeoutException
import javax.inject.Inject

class OtpRepository @Inject constructor(private val userAPI: UserAPI) {

    private val _generateOtpResponseLiveData = MutableLiveData<NetworkResult<generateOtpResponse>>()
    val generateOtpResponseLiveData: LiveData<NetworkResult<generateOtpResponse>>
        get() = _generateOtpResponseLiveData

    private val _verifyOtpResponseLiveData = MutableLiveData<NetworkResult<verifyOtpResponse>>()
    val verifyOtpResponseLiveData: LiveData<NetworkResult<verifyOtpResponse>>
        get() = _verifyOtpResponseLiveData

    suspend fun generateOtp(generateOtpRequest: generateOtpRequest) {
        _generateOtpResponseLiveData.postValue(NetworkResult.Loading())
        try {
            val response = userAPI.generateOtp(generateOtpRequest)
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    _generateOtpResponseLiveData.postValue(NetworkResult.Success(responseBody))
                } else {
                    _generateOtpResponseLiveData.postValue(NetworkResult.Error("Response body is null"))
                }
            } else if (response.errorBody() != null) {
                val errObj = JSONObject(response.errorBody()!!.charStream().readText())
                _generateOtpResponseLiveData.postValue(NetworkResult.Error(errObj.getString("message")))
            } else {
                _generateOtpResponseLiveData.postValue(NetworkResult.Error("Something went wrong"))
            }
        } catch (e: SocketTimeoutException) {
            _generateOtpResponseLiveData.postValue(NetworkResult.Error("Please try again!"))
        } catch (e: Exception) {
            _generateOtpResponseLiveData.postValue(NetworkResult.Error("Unexpected Error occured"))
            Log.d("Otp Repo", e.message.toString())
        }
    }

    suspend fun verifyOtp(verifyOtpRequest: verifyOtpRequest) {
        _verifyOtpResponseLiveData.postValue(NetworkResult.Loading())
        try {
            val response = userAPI.verifyOtp(verifyOtpRequest)
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    _verifyOtpResponseLiveData.postValue(NetworkResult.Success(responseBody))
                } else {
                    _verifyOtpResponseLiveData.postValue(NetworkResult.Error("Response body is null"))
                }
            } else if (response.errorBody() != null) {
                val errObj = JSONObject(response.errorBody()!!.charStream().readText())
                _verifyOtpResponseLiveData.postValue(NetworkResult.Error(errObj.getString("message")))
            } else {
                _verifyOtpResponseLiveData.postValue(NetworkResult.Error("Something went wrong"))
            }
        } catch (e: SocketTimeoutException) {
            _verifyOtpResponseLiveData.postValue(NetworkResult.Error("Please try again!"))
        } catch (e: Exception) {
            _verifyOtpResponseLiveData.postValue(NetworkResult.Error("Unexpected Error Occured"))
        }
    }

}