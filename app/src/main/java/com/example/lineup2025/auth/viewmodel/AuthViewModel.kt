package com.example.lineup2025.auth.viewmodel

import android.text.TextUtils
import android.util.Patterns
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

    val loginResponseLiveData: LiveData<NetworkResult<LoginResponseBody>>
        get() = repository.loginResponseLiveData

    val signUpResponseLiveData: LiveData<NetworkResult<SignUpResponseBody>>
        get() = repository.signUpResponseLivedata

    fun login(loginRequestBody: LoginRequestBody) {
        viewModelScope.launch {
            repository.login(loginRequestBody)
        }
    }

    fun signUpUser(signUpRequestBody: SignUpRequestBody) {
        viewModelScope.launch {
            repository.signUp(signUpRequestBody)
        }
    }

    fun validateSignupCredentials(name: String, email: String, zealid: String, password: String,confirmPassword: String): Pair<Boolean,String> {
        var result = Pair(true,"")
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(zealid) || TextUtils.isEmpty(password)){
            result = Pair(false,"Please fill all the fields")
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            result = Pair(false,"Please enter a valid email")
        }
        else if(zealid.length < 6){
            result = Pair(false,"Zeal Id must have 6 characters")
        }
        else if(password != confirmPassword){
            result = Pair(false, "Passwords do not match")
        }
        return result
    }

    fun validateLoginCredentials(zealid: String, password: String): Pair<Boolean,String>{
        var result = Pair(true,"")
        if(TextUtils.isEmpty(zealid) || TextUtils.isEmpty(password)){
            result = Pair(false,"Please enter your ZealId and Password")
        }
        return result
    }
}