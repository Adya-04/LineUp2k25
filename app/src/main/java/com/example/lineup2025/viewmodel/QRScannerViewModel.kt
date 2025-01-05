package com.example.lineup2025.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lineup2025.model.QRScannerRequest
import com.example.lineup2025.model.QRScannerResponse
import com.example.lineup2025.repository.QRScannerRepository
import com.example.lineup2025.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QRScannerViewModel @Inject constructor(private val repository: QRScannerRepository): ViewModel(){
    val qrScannerResponseLiveData: LiveData<NetworkResult<QRScannerResponse>>
        get() = repository.qrScannerResponseLiveData

    fun sendQR(qrScannerRequest: QRScannerRequest){
        viewModelScope.launch {
            repository.sendQRCode(qrScannerRequest)
        }
    }
}