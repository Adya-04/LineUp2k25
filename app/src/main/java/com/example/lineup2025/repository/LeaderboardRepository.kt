package com.example.lineup2025.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.lineup2025.api.MainAPI
import com.example.lineup2025.model.LeaderboardModel
import com.example.lineup2025.utils.NetworkResult
import org.json.JSONObject
import java.net.SocketTimeoutException
import javax.inject.Inject

class LeaderboardRepository @Inject constructor(private val mainAPI: MainAPI) {
    private val _leaderboardResponseLiveData = MutableLiveData<NetworkResult<LeaderboardModel>>()
    val leaderboardResponseLiveData: LiveData<NetworkResult<LeaderboardModel>>
        get() = _leaderboardResponseLiveData

    suspend fun getPlayers() {
        _leaderboardResponseLiveData.postValue(NetworkResult.Loading())
        try {
            val response = mainAPI.getPlayers()
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    _leaderboardResponseLiveData.postValue(NetworkResult.Success(responseBody))
                } else {
                    _leaderboardResponseLiveData.postValue(NetworkResult.Error("Response Body is null"))
                }
            } else if (response.errorBody() != null) {
                val errObj = JSONObject(response.errorBody()!!.charStream().readText())
                _leaderboardResponseLiveData.postValue(NetworkResult.Error(errObj.getString("message")))
            } else {
                _leaderboardResponseLiveData.postValue(NetworkResult.Error("Something went wrong"))
            }
        } catch (e: SocketTimeoutException) {
            _leaderboardResponseLiveData.postValue(NetworkResult.Error("Please Try Again"))
        } catch (e: Exception) {
            _leaderboardResponseLiveData.postValue(NetworkResult.Error("Unexpected error occurred"))
        }
    }
}