package com.example.lineup2025

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.lineup2025.databinding.ActivityBottomNavigationBinding
import com.example.lineup2025.services.DirectionService
import com.example.lineup2025.services.ForeGroundLocationUpdates
import com.example.lineup2025.services.LocationUpdates
import com.example.lineup2025.ui.LeaderBoardFragment
import com.example.lineup2025.ui.QRCodeFragment
import com.example.lineup2025.ui.QRScannerFragment
import com.example.lineup2025.ui.RouteFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomNavigationActivity : AppCompatActivity() {
    lateinit var binding: ActivityBottomNavigationBinding

    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private val LOCATION_PERMISSION_REQUEST_CODE = 101
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 102

    private val directionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action == DirectionService.ACTION_DIRECTION_UPDATE) {
                    val direction = it.getStringExtra("DIRECTION")
                    if (direction != null) {
                        updateDirectionUI(direction)
                    }
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val serviceIntent = Intent(this, DirectionService::class.java)
        startService(serviceIntent)

        replaceFragment(QRCodeFragment())
        // Handle BottomNavigationView item clicks
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.QR_code -> replaceFragment(QRCodeFragment())
                R.id.Scanner -> replaceFragment(QRScannerFragment())
                R.id.route -> replaceFragment(RouteFragment())
                R.id.Leaderboard -> replaceFragment(LeaderBoardFragment())
            }
            true
        }

        requestRequiredPermissions()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                AlertDialog.Builder(this@BottomNavigationActivity)
                    .setTitle("Exit")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("Yes") { dialog, which ->
                        val intent = Intent(Intent.ACTION_MAIN)
                        intent.addCategory(Intent.CATEGORY_HOME)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                    .setNegativeButton("No") { dialog, which ->
                        dialog.dismiss() // Close the dialog
                    }
                    .show()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestRequiredPermissions() {
        // First, check if any permissions are missing
        val permissionsToRequest = mutableListOf<String>()
        if (!checkCameraPermission()) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }
        if (!checkLocationPermission()) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if(!checkNotificationPermission()){
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                CAMERA_PERMISSION_REQUEST_CODE // Use the same request code
            )
        }
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            for (i in permissions.indices) {
                val permission = permissions[i]
                val grantResult = grantResults[i]

                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                        // User denied with "Don't ask again"
                        showSettingsDialog(permission)
                    } else {
                        // Permission denied but not permanently
                        Toast.makeText(
                            this,
                            "$permission is required for this feature.",
                            Toast.LENGTH_SHORT
                        ).show()
                        showSettingsDialog(permission)
                    }
                }
            }
        }
    }

    private fun showSettingsDialog(permission: String) {
        AlertDialog.Builder(this)
            .setTitle("$permission Required")
            .setMessage("This permission is required for the app to function properly. Please grant it in settings.")
            .setPositiveButton("Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTrasaction = fragmentManager.beginTransaction()
        fragmentTrasaction.replace(R.id.frame_layout, fragment)
        fragmentTrasaction.commit()

        if (fragment is RouteFragment) {
            binding.direction.visibility = View.VISIBLE
        } else {
            binding.direction.visibility = View.GONE
        }
    }

    private fun startForeground() {
        val serviceIntent = Intent(this, ForeGroundLocationUpdates::class.java)
        startService(serviceIntent)
        val serviceIntent3 = Intent(this, DirectionService::class.java)
        startService(serviceIntent3)
    }

    private fun stopForeground() {
        val serviceIntent = Intent(this, ForeGroundLocationUpdates::class.java)
        stopService(serviceIntent)
    }

    private fun startBackground() {
        val serviceIntent = Intent(this, LocationUpdates::class.java)
        startService(serviceIntent)
    }

    private fun stopBackground() {
        val serviceIntent = Intent(this, LocationUpdates::class.java)
        stopService(serviceIntent)
    }
    private fun updateDirectionUI(direction: String) {
        binding.direction.text = direction
    }

    override fun onResume() {
        super.onResume()
        Log.d("Bottom Activity", "Location 1")
        startForeground()
        Log.d("Bottom Activity", "Location 2")
        stopBackground()
        Log.d("Bottom Activity", "Location 3")

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(
                directionReceiver,
                IntentFilter(DirectionService.ACTION_DIRECTION_UPDATE)
            )
    }

    override fun onPause() {
        super.onPause()
        startBackground()
        stopForeground()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground()
        stopBackground()
        val serviceIntent = Intent(this, DirectionService::class.java)
        stopService(serviceIntent)
    }

}