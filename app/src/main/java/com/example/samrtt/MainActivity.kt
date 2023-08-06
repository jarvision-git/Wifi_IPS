package com.example.samrtt

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.rtt.WifiRttManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                }
                permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                } else -> {
                // No location access granted.
            }
            }
        }

        val WifiPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(android.Manifest.permission.NEARBY_WIFI_DEVICES, false) -> {
                    // Only approximate location access granted.
                } else -> {
                // No location access granted.
            }
            }
        }



        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            Log.i("loc perm :","granted")
        }
        else{
            Log.e("loc perm :","not granted")

            locationPermissionRequest.launch(arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
            ))
        }
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.NEARBY_WIFI_DEVICES) ==
            PackageManager.PERMISSION_GRANTED) {
            Log.i("wifi perm :","granted")
        }
        else{
            Log.e("wifi perm :","not granted")

            WifiPermissionRequest.launch(arrayOf(
                android.Manifest.permission.NEARBY_WIFI_DEVICES
            ))


        }


        if(this.packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_RTT)) {
            Log.v("package status : ","Present")

            val filter = IntentFilter(WifiRttManager.ACTION_WIFI_RTT_STATE_CHANGED)
            val myReceiver = object: BroadcastReceiver() {

                override fun onReceive(context: Context, intent: Intent) {
                    val wifiRttManager = baseContext.getSystemService(WIFI_RTT_RANGING_SERVICE) as WifiRttManager
                    if (wifiRttManager.isAvailable) {
                        Log.v("WifiManager","Available")

                    } else {
                        Log.v("WifiManager","Not available")

                    }
                }
            }
            this.registerReceiver(myReceiver, filter)
        }
        else{
            Log.v("package status : ","Absent")

        }
    }
}