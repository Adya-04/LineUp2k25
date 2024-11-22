package com.example.lineup2025.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lineup2025.auth.model.AvatarRequest
import com.example.lineup2025.auth.model.AvatarResponse
import com.example.lineup2025.auth.repository.AvatarSelectRepository
import com.example.lineup2025.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AvatarSelectViewModel @Inject constructor(private val repository: AvatarSelectRepository) : ViewModel() {

    val storeAvatarResponseLiveData: LiveData<NetworkResult<AvatarResponse>>
        get() = repository.storeAvatarResponseLiveData

    fun storeAvatar(token: String, avatarRequest: AvatarRequest) {
        viewModelScope.launch {
            repository.storeAvatar(token, avatarRequest)
        }
    }
}