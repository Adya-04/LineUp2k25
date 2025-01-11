package com.example.lineup2025.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lineup2025.model.LeaderboardModel
import com.example.lineup2025.repository.LeaderboardRepository
import com.example.lineup2025.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(private val leaderboardRepository: LeaderboardRepository): ViewModel() {
    val leaderboardLiveData : LiveData<NetworkResult<LeaderboardModel>>
        get() = leaderboardRepository.leaderboardResponseLiveData

    fun getPlayers(){
        viewModelScope.launch {
            leaderboardRepository.getPlayers()
        }
    }
}