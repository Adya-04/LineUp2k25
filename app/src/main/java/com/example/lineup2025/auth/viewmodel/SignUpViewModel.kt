package com.example.lineup2025.auth.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lineup2025.auth.model.SignUpRequestBody
import com.example.lineup2025.auth.model.SignUpResponseBody
import com.example.lineup2025.auth.repository.SignUpRepository
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

class SignUpViewModelFactory(private val repository: SignUpRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}