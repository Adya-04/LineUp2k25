package com.example.lineup2025.auth.login.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lineup2025.auth.login.model.LoginRequestBody
import com.example.lineup2025.auth.login.model.LoginResponseBody
import com.example.lineup2025.auth.login.repository.LoginRepository
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
                Log.e("LoginViewModel", "Sign up failed: ${e.message}")
            }

        }
    }
}