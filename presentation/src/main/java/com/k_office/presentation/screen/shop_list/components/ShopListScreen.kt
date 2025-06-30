package com.k_office.presentation.screen.shop_list.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.k_office.domain.model.Shop
import com.k_office.presentation.R
import com.k_office.presentation.base.utils.openGoogleMapsRoute
import com.k_office.presentation.screen.shop_list.ShopListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ShopListScreen(viewModel: ShopListViewModel) { // ViewModel инжектируется здесь

    val context = LocalContext.current
    val shops by viewModel.shopsInfo.collectAsStateWithLifecycle() // Список магазинов (без дистанции пока)

    // --- Состояния, управляемые Composable для местоположения ---
    var userLocation by remember { mutableStateOf<Location?>(null) }
    var isLoadingLocation by remember { mutableStateOf(true) } // Индикатор загрузки именно местоположения
    var hasLocationPermissionBeenAsked by remember { mutableStateOf(false) } // Флаг, чтобы избежать повторного запроса без причины

    // Состояние для списка магазинов с вычисленными дистанциями
    val shopsWithCalculatedDistances =
        remember(shops, userLocation) { // Пересчитываем, когда shops ИЛИ userLocation меняется
            if (userLocation == null) {
                shops.map { it.copy(distance = "Calculating...") } // Если нет местоположения, дистанция "Calculating..."
            } else {
                shops.map { shop ->
                    val storeLocation = Location("").apply {
                        latitude = shop.latLng.latitude
                        longitude = shop.latLng.longitude
                    }
                    val distanceInMeters = userLocation!!.distanceTo(storeLocation)
                    val distanceInKm = distanceInMeters / 1000.0

                    val formattedDistance = if (distanceInKm >= 1.0) {
                        "%.1f км".format(distanceInKm)
                    } else {
                        "%.0f м".format(distanceInMeters)
                    }
                    shop.copy(distance = formattedDistance)
                }
            }
        }

    // Запускатель для запроса разрешений
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.any { it.value }
        if (granted) {
            startLocationUpdates(
                context,
                userLocation,
                isLoadingLocation
            ) { loc, loading -> // Обновленная лямбда для startLocationUpdates
                userLocation = loc
                isLoadingLocation = loading
            }
        } else {
            Toast.makeText(
                context,
                "Location permission denied. Cannot calculate distances.",
                Toast.LENGTH_SHORT
            ).show()
            isLoadingLocation = false
            userLocation = null
        }
        hasLocationPermissionBeenAsked = true
    }

    // FusedLocationProviderClient и LocationCallback управляются DisposableEffect
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    userLocation = location
                    isLoadingLocation = false
                } ?: run {
                    // Если onLocationResult null, но разрешения есть, продолжаем загрузку
                    isLoadingLocation = true
                }
            }
        }
    }

    // Эффект для запроса разрешений и запуска/остановки обновлений местоположения
    DisposableEffect(Unit) {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasLocationPermissionBeenAsked) { // Только если разрешение еще не запрашивалось
            if (hasFineLocationPermission || hasCoarseLocationPermission) {
                startLocationUpdates(context, userLocation, isLoadingLocation) { loc, loading ->
                    userLocation = loc
                    isLoadingLocation = loading
                }
            } else {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }

        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    // Обсервация состояний BottomSheet из ViewModel
    val showModalBottomSheet = viewModel.showModalBottomSheet
    val selectedShopForInfoModal = viewModel.selectedShopForModal

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Синхронизация видимости BottomSheet с состоянием ViewModel
    LaunchedEffect(showModalBottomSheet) {
        if (showModalBottomSheet) {
            scope.launch { sheetState.show() }
        } else {
            scope.launch { sheetState.hide() }
        }
    }
    // Отслеживаем скрытие BottomSheet через UI (смахивание)
    LaunchedEffect(sheetState.isVisible) {
        if (!sheetState.isVisible && showModalBottomSheet) {
            viewModel.hideShopInfoModal()
        }
    }
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (isLoadingLocation) { // Используем isLoadingLocation для индикатора загрузки местоположения
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Loading your location and calculating distances...")
            }
        } else if (userLocation == null && !shops.any { it.distance != "Calculating..." && it.distance != "N/A" }) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = {
                    requestPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }) {
                    Text("Retry Location")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Could not get your location. Distances might be inaccurate.")
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(shopsWithCalculatedDistances) { shop -> // Используем shopsWithCalculatedDistances
                    ShopItem(
                        shop = shop,
                        isSelected = false, // Если у вас есть логика selectedShopId, то используйте ее здесь
                        onMapClick = { clickedShop ->
                            viewModel.onShopMapClick(clickedShop)
                        },
                        onDetailsClick = { clickedShop ->
                            viewModel.onShopDetailsClick(clickedShop)
                        }
                    )
                }
            }
        }

    }

    if (showModalBottomSheet && selectedShopForInfoModal != null) {
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.hideShopInfoModal()
            },
            sheetState = sheetState,
            containerColor = Color.Transparent,
            scrimColor = Color.Black.copy(alpha = 0.6f),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            tonalElevation = 0.dp
        ) {
            ShopInfoDialog(
                shop = selectedShopForInfoModal!!,
                onDismiss = {
                    viewModel.hideShopInfoModal()
                },
                onRouteClick = {
                    selectedShopForInfoModal?.let { shop ->
                        context.openGoogleMapsRoute(shop.latLng.latitude, shop.latLng.longitude, shop.name)
                    }
                    viewModel.hideShopInfoModal()
                }
            )
        }
    }
}

// ---- ShopItem остается без изменений, как вы его предоставили ----
// Убедитесь, что ShopItem принимает (Shop) -> Unit для onMapClick/onDetailsClick,
// а не () -> Unit, чтобы передать Shop обратно.
@Composable
internal fun ShopItem(
    shop: Shop,
    isSelected: Boolean,
    onMapClick: (Shop) -> Unit, // Принимаем Shop
    onDetailsClick: (Shop) -> Unit // Принимаем Shop
) {
    // Цвет рамки зависит от isSelected
    val borderColor = if (isSelected) Color(0xFFC70039) else Color.Transparent
    val borderWidth = if (isSelected) 1.dp else 0.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(borderWidth, borderColor, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Левая часть - круглая иконка
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0F0F0))
                    .border(1.dp, Color(0xFFE0E0E0), CircleShape)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_app_logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Центральная часть - текст (Название, Адрес, Дистанция, Детали локации)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = shop.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = shop.fullAddress,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = shop.distance.toString(), // distance уже строка
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                shop.locationDetails.let {
                    if (it.isNotEmpty()) { // Проверка на непустую строку
                        Text(
                            text = it,
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Правая часть - Две новые кнопки по вертикали
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxHeight()
            ) {
                // Кнопка: На карту
                IconButton(
                    onClick = { onMapClick(shop) }, // Передаем shop обратно
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Show on map",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.padding(vertical = 18.dp))

                // Кнопка: Детали магазина
                IconButton(
                    onClick = { onDetailsClick(shop) }, // Передаем shop обратно
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Shop details",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}


@SuppressLint("MissingPermission") // Разрешение будет запрошено ранее
private fun startLocationUpdates(
    context: Context,
    userLocation: Location?,
    isLoadingLocation: Boolean,
    onLocationUpdate: (Location?, Boolean) -> Unit
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                onLocationUpdate(location, false) // Местоположение получено, загрузка завершена
            } ?: run {
                onLocationUpdate(null, true) // Местоположение null, продолжаем загрузку
            }
        }
    }

    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L)
        .setMinUpdateIntervalMillis(5000L)
        .build()

    val hasFineLocationPermission = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (hasFineLocationPermission || hasCoarseLocationPermission) {
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, context.mainLooper)
            // Если userLocation еще не установлен, показываем загрузку
            if (userLocation == null) {
                onLocationUpdate(null, true)
            }
        } catch (e: SecurityException) {
            Toast.makeText(context, "Location permission not truly granted for updates: ${e.message}", Toast.LENGTH_LONG).show()
            onLocationUpdate(null, false) // Ошибка, загрузка завершена, местоположение не получено
        }
    } else {
        onLocationUpdate(null, false) // Нет разрешений, загрузка завершена, местоположение не получено
    }
}