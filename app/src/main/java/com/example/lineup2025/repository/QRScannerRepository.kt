package com.example.lineup2025.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.lineup2025.api.MainAPI
import com.example.lineup2025.model.QRScannerRequest
import com.example.lineup2025.model.QRScannerResponse
import com.example.lineup2025.utils.NetworkResult
import org.json.JSONObject
import java.net.SocketTimeoutException
import javax.inject.Inject

class QRScannerRepository @Inject constructor(private val mainAPI: MainAPI) {
    private val _qrScannerResponseLivedata = MutableLiveData<NetworkResult<QRScannerResponse>>()
    val qrScannerResponseLiveData: LiveData<NetworkResult<QRScannerResponse>>
        get() = _qrScannerResponseLivedata

    suspend fun sendQRCode(code:QRScannerRequest){
        _qrScannerResponseLivedata.postValue(NetworkResult.Loading())
        try {
            val response = mainAPI.scanQRCode(code)
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    _qrScannerResponseLivedata.postValue(NetworkResult.Success(responseBody))
                } else {
                    _qrScannerResponseLivedata.postValue(NetworkResult.Error("Response body is null"))
                }
            } else if (response.errorBody() != null) {
                val errObj = JSONObject(response.errorBody()!!.charStream().readText())
                _qrScannerResponseLivedata.postValue(NetworkResult.Error(errObj.getString("message")))
            } else {
                _qrScannerResponseLivedata.postValue(NetworkResult.Error("Something went wrong"))
            }
        }
        catch (e: SocketTimeoutException){
            _qrScannerResponseLivedata.postValue(NetworkResult.Error("Please try again!"))
        } catch (e: Exception){
            _qrScannerResponseLivedata.postValue(NetworkResult.Error("Unexpected error occurred"))
        }


    }
}