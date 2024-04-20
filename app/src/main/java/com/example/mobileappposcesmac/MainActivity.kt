package com.example.mobileappposcesmac

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

private const val LOCATION_REQUEST_CODE = 100
private val locationPermissionsList = arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)

private const val PERMISSION_ACCEPTED_MESSAGE = "Permission Accepted"
private const val PERMISSION_REJECTED_MESSAGE = "Permission Rejected"

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        initializeMapContext()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.first() != PERMISSION_GRANTED) {
            shortToastText(PERMISSION_REJECTED_MESSAGE)
            return
        }

        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                shortToastText(PERMISSION_ACCEPTED_MESSAGE)
                retrieveUserLocation()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isMyLocationButtonEnabled = true

        checkLocationPermission()
    }

    private fun initializeMapContext() =
        (supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment)?.getMapAsync(this)

    private fun checkLocationPermission() =
        if (hasLocationPermissions()) {
            retrieveUserLocation()
        } else {
            requestLocationPermissions()
        }

    private fun hasLocationPermissions() =
        verifyPermission(ACCESS_FINE_LOCATION) && verifyPermission(ACCESS_COARSE_LOCATION)

    @SuppressLint("MissingPermission")
    private fun retrieveUserLocation() {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            it?.run {
                map.moveCamera(CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude)))
            }
        }
    }

    private fun requestLocationPermissions() =
        requestPermission(locationPermissionsList, LOCATION_REQUEST_CODE)

    private fun verifyPermission(permissionToVerify: String) =
        ActivityCompat.checkSelfPermission(this, permissionToVerify) == PERMISSION_GRANTED

    private fun requestPermission(permissions: Array<String>, permissionCode: Int) =
        ActivityCompat.requestPermissions(this, permissions, permissionCode)

    private fun shortToastText(text: String) =
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}