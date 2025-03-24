package com.example.lineup2025.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lineup2025.auth.model.generateOtpRequest
import com.example.lineup2025.auth.model.generateOtpResponse
import com.example.lineup2025.auth.model.verifyOtpRequest
import com.example.lineup2025.auth.model.verifyOtpResponse
import com.example.lineup2025.auth.repository.OtpRepository
import com.example.lineup2025.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(private val otpRepository: OtpRepository) : ViewModel(){

    val generateOtpResponseLiveData : LiveData<NetworkResult<generateOtpResponse>>
        get() = otpRepository.generateOtpResponseLiveData

    val verifyOtpResponseLivedata: LiveData<NetworkResult<verifyOtpResponse>>
        get() = otpRepository.verifyOtpResponseLiveData

    fun generateOtp(generateOtpRequest: generateOtpRequest){
        viewModelScope.launch {
            otpRepository.generateOtp(generateOtpRequest)
        }
    }
    fun verifyOtp(verifyOtpRequest: verifyOtpRequest){
        viewModelScope.launch {
            otpRepository.verifyOtp(verifyOtpRequest)
        }
    }

    //validateCredentials
    fun validateOtp(typedOtp: String): Pair<Boolean,String>{
        var result = Pair(true,"")
        if (typedOtp.isNotEmpty()) {
            if (typedOtp.length != 6) {
                result = Pair(false,"Please enter correct OTP")
            }
        } else {
            result = Pair(false,"Please Enter OTP")
        }
        return result
    }
}