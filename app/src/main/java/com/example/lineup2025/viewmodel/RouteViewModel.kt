package com.example.lineup2025.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lineup2025.model.RouteResponse
import com.example.lineup2025.repository.RouteRepository
import com.example.lineup2025.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteViewModel @Inject constructor(private val repository: RouteRepository) : ViewModel() {
    val getRouteResponseLiveData : LiveData<NetworkResult<RouteResponse>>
        get() = repository.getRouteResponseLiveData

    fun getRoute(){
        viewModelScope.launch {
            repository.getRoute()
        }
    }

}