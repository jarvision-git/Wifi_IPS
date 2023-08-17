package com.example.samrtt

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.net.wifi.rtt.WifiRttManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.samrtt.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var wifiManager : WifiManager
    lateinit var binding:ActivityMainBinding
    var sb = StringBuilder()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

                }
                permissions.getOrDefault(android.Manifest.permission.CHANGE_WIFI_STATE, false) -> {

                }
                else -> {

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
                android.Manifest.permission.NEARBY_WIFI_DEVICES,
                android.Manifest.permission.CHANGE_WIFI_STATE
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

//                        val req: RangingRequest = RangingRequest.Builder().run {
//                            addAccessPoint(ScanResult ap1)
//                            addAccessPoint(ap2ScanResult)
//                            build()
//                        }


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



        val wifiScanReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                if (success) {
                    scanSuccess()
                } else {
                    scanFailure()
                }
            }
        }

        wifiManager = this.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        this.registerReceiver(wifiScanReceiver, intentFilter)

        val success =  wifiManager.startScan()
        if (!success) {
            // scan failure handling
            scanFailure()
        }




    }
    @SuppressLint("MissingPermission")
    private fun scanSuccess() {
        var a:Int=0
        var b:Int=0
        val results = wifiManager.scanResults
        Log.v("checkpoint status","Reached")
        if (results.size!=0 ) {

//            var str=results[0].wifiSsid!!.toString()
            for(i in 0..results.size-1){
               if (results[i].BSSID==("d4:6e:0e:c9:46:0c")) {
                   a=results[i].level
                    sb.append(
                        results[i].wifiSsid.toString()+results[i].level.toString()
                    )
               }
                if( results[i].BSSID==("b4:a7:c6:87:af:b6") ){
                    b=results[i].level
                    sb.appendLine(
                        results[i].wifiSsid.toString()+results[i].level.toString()
                    )
                }
            }
            if (a==0||b==0){
                binding.tvRoom.text="Scan Again"

            }
            if (a<-30 && b<-60){
                binding.tvRoom.text="Room 1"
            }
            else if (a<-20 && a>-55 && b<-30 && b>-60){
                binding.tvRoom.text=" Room 2"
            }
            else if  (a<-55 && b>-60){
                binding.tvRoom.text=" Room 3"
            }
            else{
                binding.tvRoom.text=" Scan Again"

            }
            binding.tvWifi.text=sb
        }
    }

    @SuppressLint("MissingPermission")
    private fun scanFailure() {

        val results = wifiManager.scanResults
        Log.i("Scan result :","prev")

    }
}