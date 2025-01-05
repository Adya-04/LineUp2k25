package com.example.lineup2025.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.lineup2025.api.MainAPI
import com.example.lineup2025.model.RouteResponse
import com.example.lineup2025.utils.NetworkResult
import org.json.JSONObject
import java.net.SocketTimeoutException
import javax.inject.Inject

class RouteRepository @Inject constructor(private val mainAPI: MainAPI) {
    private val _getRouteResponseLiveData = MutableLiveData<NetworkResult<RouteResponse>>()
    val getRouteResponseLiveData: LiveData<NetworkResult<RouteResponse>>
        get() = _getRouteResponseLiveData

    suspend fun getRoute() {
        _getRouteResponseLiveData.postValue(NetworkResult.Loading())
        try {
            val response = mainAPI.getRoute()
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    _getRouteResponseLiveData.postValue(NetworkResult.Success(responseBody))
                } else {
                    _getRouteResponseLiveData.postValue(NetworkResult.Error("Response body is null"))
                }
            } else if (response.errorBody() != null) {
//            val errObj = JSONObject(response.errorBody()!!.charStream().readText())
//            _getRouteResponseLiveData.postValue(NetworkResult.Error(errObj.getString("message")))
                //no message here in response body
            } else {
                _getRouteResponseLiveData.postValue(NetworkResult.Error("Something went wrong"))
            }
        }
        catch (e: SocketTimeoutException){
            _getRouteResponseLiveData.postValue(NetworkResult.Error("Please try again!"))
        } catch (e: Exception){
            _getRouteResponseLiveData.postValue(NetworkResult.Error("Unexpected error occurred"))
        }
    }

}