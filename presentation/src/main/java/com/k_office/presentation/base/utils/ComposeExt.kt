package com.k_office.presentation.base.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority

/**
 * Data class для хранения состояния местоположения.
 * @param location Текущее местоположение пользователя (null, если недоступно или загружается).
 * @param isLoading True, если местоположение активно загружается или ожидаются разрешения.
 * @param requestPermissions Функция для явного повторного запроса разрешений.
 */
data class LocationState(
    val location: Location?,
    val isLoading: Boolean,
    val requestPermissions: () -> Unit
)

/**
 * Composable-хук для получения и управления состоянием местоположения пользователя.
 * Запрашивает разрешения и запускает/останавливает обновления местоположения в соответствии с жизненным циклом Composable.
 * @return LocationState, содержащий местоположение, состояние загрузки и функцию для запроса разрешений.
 */
@Composable
fun rememberLocationState(): LocationState {
    val context = LocalContext.current
    var userLocation by remember { mutableStateOf<Location?>(null) }
    var isLoading by remember { mutableStateOf(true) } // Изначально загружается

    // Флаг, чтобы не запрашивать разрешения снова и снова, если пользователь уже отказал
    var permissionRequested by remember { mutableStateOf(false) }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    userLocation = location
                    isLoading = false // Местоположение получено
                    Log.d("rememberLocationState", "Location updated: ${location.latitude}, ${location.longitude}")
                } ?: run {
                    Log.w("rememberLocationState", "Location result was null.")
                    isLoading = true // Если null, продолжаем загрузку
                }
            }
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.any { it.value } // True, если хотя бы одно разрешение дано
        if (granted) {
            startLocationUpdatesInternal(context, fusedLocationClient, locationCallback) { isUpdating ->
                isLoading = isUpdating
            }
        } else {
            Toast.makeText(context, "Location permission denied. Cannot get current location.", Toast.LENGTH_SHORT).show()
            userLocation = null
            isLoading = false // Разрешение отклонено, загрузка завершена
        }
    }

    // Функция для запроса разрешений, которую можно вызвать из UI
    val requestPermissionsExplicitly = remember {
        {
            if (!permissionRequested) { // Запрашиваем только один раз
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
                permissionRequested = true // Помечаем, что запрос был
            } else {
                // Если разрешения уже запрашивались, но не даны, можно показать объяснение
                // Или просто попробовать снова запросить, но тогда убрать if (!permissionRequested)
                // Для простоты, пока без дополнительного объяснения
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    // Эффект для запуска/остановки обновлений местоположения в соответствии с жизненным циклом Composable
    DisposableEffect(fusedLocationClient, locationCallback) {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFineLocationPermission || hasCoarseLocationPermission) {
            // Разрешения уже есть, запускаем обновления
            startLocationUpdatesInternal(context, fusedLocationClient, locationCallback) { isUpdating ->
                isLoading = isUpdating
            }
        } else if (!permissionRequested) { // Запрашиваем разрешение, если еще не запрашивалось
            requestPermissionsExplicitly() // Инициируем запрос разрешений
        } else {
            // Если разрешение уже запрашивалось и не было дано, то isLoading уже false
            isLoading = false
        }

        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            Log.d("rememberLocationState", "Location updates removed on dispose.")
        }
    }

    return LocationState(userLocation, isLoading, requestPermissionsExplicitly)
}

// Внутренняя функция для запуска запросов местоположения
@SuppressLint("MissingPermission") // Разрешение проверяется перед вызовом
private fun startLocationUpdatesInternal(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    locationCallback: LocationCallback,
    onStatusUpdate: (Boolean) -> Unit // Callback для обновления isLoading
) {
    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L)
        .setMinUpdateIntervalMillis(5000L)
        .build()

    try {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, context.mainLooper)
        onStatusUpdate(true) // Показываем загрузку, пока не придет первое местоположение
        Log.d("startLocationUpdatesInternal", "Location updates requested.")
    } catch (e: SecurityException) {
        Toast.makeText(context, "Location permission not truly granted for updates: ${e.message}", Toast.LENGTH_LONG).show()
        Log.e("startLocationUpdatesInternal", "SecurityException requesting location updates: ${e.message}")
        onStatusUpdate(false) // Ошибка, загрузка завершена
    }
}