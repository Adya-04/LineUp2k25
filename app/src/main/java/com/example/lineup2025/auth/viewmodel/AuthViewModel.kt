package com.example.lineup2025.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lineup2025.auth.model.LoginRequestBody
import com.example.lineup2025.auth.model.LoginResponseBody
import com.example.lineup2025.auth.model.SignUpRequestBody
import com.example.lineup2025.auth.model.SignUpResponseBody
import com.example.lineup2025.auth.repository.AuthRepository
import com.example.lineup2025.utils.NetworkResult
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

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

class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}