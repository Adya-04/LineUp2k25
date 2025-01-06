package com.example.lineup2025.services

import android.location.Location
import androidx.lifecycle.ViewModel
import com.example.lineup2025.utils.Constants.SOCKET_URL
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocationUpdatesViewModel @Inject constructor(private val repository: LocationUpdatesRepository): ViewModel() {

    fun initializeSocket(){
        repository.initializeSocket()
    }

    fun sendLocation(location: Location){
        repository.sendLocation(location)
    }

    fun disconnectSocket(){
        repository.disconnectSocket()
    }

}