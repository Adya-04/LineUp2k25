package com.example.lineup2025.auth.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lineup2025.auth.model.LoginRequestBody
import com.example.lineup2025.auth.model.LoginResponseBody
import com.example.lineup2025.auth.repository.LoginRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class LoginViewModel(private val repository: LoginRepository) : ViewModel() {

    private val _loginResponse = MutableLiveData<Response<LoginResponseBody>>()
    val loginResponse: LiveData<Response<LoginResponseBody>>
        get() = _loginResponse

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun login(loginRequestBody: LoginRequestBody) {
        _loading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = repository.login(loginRequestBody)
                withContext(Dispatchers.Main) {
                    _loginResponse.value = response
                    _loading.value = false
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Login failed: ${e.message}")
            }

        }
    }
}

class LoginViewModelFactory(private val repository: LoginRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}