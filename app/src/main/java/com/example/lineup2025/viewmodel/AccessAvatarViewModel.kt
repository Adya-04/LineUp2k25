package com.example.lineup2025.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lineup2025.model.AccessAvatar
import com.example.lineup2025.model.QRCodeResponse
import com.example.lineup2025.repository.AccessAvatarRepository
import com.example.lineup2025.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccessAvatarViewModel @Inject constructor(private val repository: AccessAvatarRepository): ViewModel() {
    val accessAvatarResponseLiveData: LiveData<NetworkResult<AccessAvatar>>
        get() = repository.accessAvatarResponseLiveData

    fun accessAvatar(){
        viewModelScope.launch {
            repository.accessAvatar()
        }
    }
}