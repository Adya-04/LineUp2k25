package com.example.lineup2025.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lineup2025.auth.model.LoginRequestBody
import com.example.lineup2025.auth.model.LoginResponseBody
import com.example.lineup2025.auth.model.SignUpRequestBody
import com.example.lineup2025.auth.model.SignUpResponseBody
import com.example.lineup2025.auth.repository.AuthRepository
import com.example.lineup2025.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {

    val loginResponseLiveData :LiveData<NetworkResult<LoginResponseBody>>
        get() = repository.loginResponseLiveData

    val signUpResponseLiveData: LiveData<NetworkResult<SignUpResponseBody>>
        get() = repository.signUpResponseLivedata

    fun login(loginRequestBody: LoginRequestBody) {
        viewModelScope.launch {
            repository.login(loginRequestBody)
        }
    }
    fun signUpUser(signUpRequestBody: SignUpRequestBody){
        viewModelScope.launch {
            repository.signUp(signUpRequestBody)
        }
    }
}