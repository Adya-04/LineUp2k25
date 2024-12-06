package com.example.lineup2025.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lineup2025.model.QRCodeResponse
import com.example.lineup2025.repository.QRCodeRepository
import com.example.lineup2025.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QRCodeViewModel @Inject constructor(private val repository: QRCodeRepository): ViewModel() {
    val qrcodeResponseLiveData: LiveData<NetworkResult<QRCodeResponse>>
        get() = repository.qrResponseLiveData

    fun getQRCode(){
        viewModelScope.launch {
            repository.getQRCode()
        }
    }
}