package com.k_office.presentation.base.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IdRes
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import timber.log.Timber


data class LocationState(
    val location: Location?,
    val isLoading: Boolean,
    val requestPermissions: () -> Unit
)


@SuppressLint("TimberArgCount")
@Composable
fun rememberLocationState(): LocationState {
    val context = LocalContext.current
    var userLocation by remember { mutableStateOf<Location?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    var permissionRequested by remember { mutableStateOf(false) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationCallback = remember {
        object : LocationCallback() {
            @SuppressLint("TimberArgCount")
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    userLocation = location
                    isLoading = false
                    Timber.d(
                        message = "rememberLocationState",
                        args = arrayOf("Location updated: ${location.latitude}, ${location.longitude}")
                    )
                } ?: run {
                    Timber.w("rememberLocationState", "Location result was null.")
                    isLoading = true
                }
            }
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.any { it.value }
        if (granted) {
            startLocationUpdatesInternal(
                context,
                fusedLocationClient,
                locationCallback
            ) { isUpdating ->
                isLoading = isUpdating
            }
        } else {
            Toast.makeText(
                context,
                "Location permission denied. Cannot get current location.",
                Toast.LENGTH_SHORT
            ).show()
            userLocation = null
            isLoading = false
        }
    }

    val requestPermissionsExplicitly = remember {
        {
            if (!permissionRequested) {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
                permissionRequested = true
            } else {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    DisposableEffect(fusedLocationClient, locationCallback) {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFineLocationPermission || hasCoarseLocationPermission) {
            startLocationUpdatesInternal(
                context,
                fusedLocationClient,
                locationCallback
            ) { isUpdating ->
                isLoading = isUpdating
            }
        } else if (!permissionRequested) {
            requestPermissionsExplicitly()
        } else {
            isLoading = false
        }

        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            Timber.d("rememberLocationState", "Location updates removed on dispose.")
        }
    }

    return LocationState(userLocation, isLoading, requestPermissionsExplicitly)
}

@SuppressLint("MissingPermission", "TimberArgCount")
private fun startLocationUpdatesInternal(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    locationCallback: LocationCallback,
    onStatusUpdate: (Boolean) -> Unit
) {
    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L)
        .setMinUpdateIntervalMillis(5000L)
        .build()

    try {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            context.mainLooper
        )
        onStatusUpdate(true)
        Timber.d("startLocationUpdatesInternal", "Location updates requested.")
    } catch (e: SecurityException) {
        Toast.makeText(
            context,
            "Location permission not truly granted for updates: ${e.message}",
            Toast.LENGTH_LONG
        ).show()
        Timber.e(
            "startLocationUpdatesInternal",
            "SecurityException requesting location updates: ${e.message}"
        )
        onStatusUpdate(false)
    }
}

@Composable
fun Context.WithFragmentNavigator(
    @IdRes containerId: Int,
    content: @Composable (navigateTo: (Fragment) -> Unit) -> Unit
) {
    val activity = this.findActivity()

    if (activity == null) {
        return
    }

    val fragmentManager = (activity as? FragmentActivity)?.supportFragmentManager
        ?: throw IllegalStateException("Context's Activity must be a FragmentActivity to use FragmentManager.")

    val navigateTo: (Fragment) -> Unit = { fragment ->
        FragmentUtil.setFragmentIfAbsent(fragment, fragmentManager, containerId)
    }

    content(navigateTo)
}