package com.example.lineup2025.auth.signup.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lineup2025.auth.signup.model.SignUpRequestBody
import com.example.lineup2025.auth.signup.model.SignUpResponseBody
import com.example.lineup2025.auth.signup.repository.SignUpRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class SignUpViewModel(private val repository: SignUpRepository) : ViewModel() {

    private val _signUpResponse = MutableLiveData<Response<SignUpResponseBody>>()
    val signUpResponse: LiveData<Response<SignUpResponseBody>>
        get() = _signUpResponse

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun signUp(email: String, password: String, name: String, zealId: String) {
        val requestBody = SignUpRequestBody(email, password, name, zealId)
        _loading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = repository.signUp(requestBody)
                _signUpResponse.postValue(response)
            } catch (e: Exception) {
                Log.e("SignUpViewModel", "Sign up failed: ${e.message}")
            }
            finally {
                _loading.postValue(false) // Set loading to false once signup completes
            }
        }
    }
}