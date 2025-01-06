package com.example.lineup2025.services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.lineup2025.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LocationUpdates :  Service(){

    @Inject
    lateinit var locationViewModel: LocationUpdatesRepository


    private lateinit var locationManager: LocationManager

    private val locationListener = LocationListener{ location ->
        Log.d("Location Updates", "sent request")
        locationViewModel.sendLocation(location)

    }

    @RequiresApi(Build.VERSION_CODES.ECLAIR)
    override fun onCreate() {
        super.onCreate()

      locationViewModel.initializeSocket()

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        requestLocationUpdates()

        val notifications = createNotification(this)
        startForeground(1,notifications)
    }

    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        // Check if GPS_PROVIDER is available
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                8000,
                0f,
                locationListener
            )
        } else {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                8000,
                0f,
                locationListener
            )
        }
    }

    private fun createNotification(context: Context): Notification {
        val channelId = "location_update_channel"
        val notificationManager = getSystemService(NotificationManager::class.java)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Location Updates",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Lineup is Active")
            .setContentText("Searching for your friends...")
            .setSmallIcon(R.drawable.small_avatar)
        // Replace with your icon

        return notificationBuilder.build()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        locationViewModel.disconnectSocket()
        locationManager.removeUpdates(locationListener)
    }

}

@AndroidEntryPoint
class ForeGroundLocationUpdates : Service(){

    @Inject
    lateinit var locationViewModel: LocationUpdatesRepository


    private lateinit var locationManager: LocationManager

    private val locationListener = LocationListener{ location ->
        locationViewModel.sendLocation(location)
    }

    @RequiresApi(Build.VERSION_CODES.ECLAIR)
    override fun onCreate() {
        super.onCreate()

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationViewModel.initializeSocket()

        requestLocationUpdates()
    }

    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        // Check if GPS_PROVIDER is available
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                8000,
                0f,
                locationListener
            )
        } else {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                8000,
                0f,
                locationListener
            )
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        locationViewModel.disconnectSocket()
        locationManager.removeUpdates(locationListener)
    }

}