package com.example.lineup2025.services

import android.location.Location
import android.util.Log
import com.example.lineup2025.utils.Constants.SOCKET_URL
import com.example.lineup2025.utils.TokenManager
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONException
import org.json.JSONObject
import java.net.URISyntaxException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationUpdatesRepository @Inject constructor() {
    @Inject
    lateinit var tokenManager: TokenManager
    private lateinit var socket: Socket

    fun initializeSocket(){
        try{
            socket = IO.socket(SOCKET_URL)
            socket.connect()
            Log.d("Location Updates", "sent request")
        }
        catch (e: URISyntaxException){
            e.printStackTrace()
        }
    }

    fun sendLocation(location : Location){
        val data = JSONObject()
        val token = tokenManager.getToken()
        try{
            data.put("latitude", location.latitude)
            data.put("longitude", location.longitude)
            data.put("token", token)
            Log.d("Location Updates", "send location")
            socket.emit("locationChange", data)
            Log.d("SendLocation",data.toString())
        }
        catch (e: JSONException){
            e.printStackTrace()
        }
    }

    fun disconnectSocket(){
        if(::socket.isInitialized){
            socket.disconnect()
        }
    }
}