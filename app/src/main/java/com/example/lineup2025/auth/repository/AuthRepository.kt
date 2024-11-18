package com.example.lineup2025.auth.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.lineup2025.auth.model.LoginRequestBody
import com.example.lineup2025.auth.model.LoginResponseBody
import com.example.lineup2025.auth.model.SignUpRequestBody
import com.example.lineup2025.auth.model.SignUpResponseBody
import com.example.lineup2025.network.APIService
import com.example.lineup2025.utils.NetworkResult
import org.json.JSONObject

class AuthRepository(private val apiService: APIService) {

    private val _loginResponseLivedata = MutableLiveData<NetworkResult<LoginResponseBody>>()
    val loginResponseLiveData: LiveData<NetworkResult<LoginResponseBody>>
        get() = _loginResponseLivedata

    private val _signUpResponseLivedata = MutableLiveData<NetworkResult<SignUpResponseBody>>()
    val signUpResponseLivedata: LiveData<NetworkResult<SignUpResponseBody>>
        get() = _signUpResponseLivedata

    suspend fun login(loginRequestBody: LoginRequestBody) {
        _loginResponseLivedata.postValue(NetworkResult.Loading())
        val response = apiService.login(loginRequestBody)
        if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                _loginResponseLivedata.postValue(NetworkResult.Success(responseBody))
            } else {
                _loginResponseLivedata.postValue(NetworkResult.Error("Response body is null"))
            }
        } else if (response.errorBody() != null) {
            val errObj = JSONObject(response.errorBody()!!.charStream().readText())
            _loginResponseLivedata.postValue(NetworkResult.Error(errObj.getString("message")))
        } else {
            _loginResponseLivedata.postValue(NetworkResult.Error("Something went wrong"))
        }
    }


    suspend fun signUp(signUpRequestBody: SignUpRequestBody) {
        _signUpResponseLivedata.postValue(NetworkResult.Loading())
        val response = apiService.signUp(signUpRequestBody)
        if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                _signUpResponseLivedata.postValue(NetworkResult.Success(responseBody))
            } else {
                _signUpResponseLivedata.postValue(NetworkResult.Error("Response body is null"))
            }
        }
        else if (response.errorBody() != null) {
            val errObj = JSONObject(response.errorBody()!!.charStream().readText())
            _signUpResponseLivedata.postValue(NetworkResult.Error(errObj.getString("message")))
        } else {
            _signUpResponseLivedata.postValue(NetworkResult.Error("Something went wrong"))
        }
    }
}