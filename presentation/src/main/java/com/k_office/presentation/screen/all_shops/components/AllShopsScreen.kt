package com.k_office.presentation.screen.all_shops.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.k_office.presentation.screen.shop_list.ShopListViewModel

@Composable
internal inline fun AllShopsScreen(viewModel: ShopListViewModel) {

    val context = LocalContext.current
    val shopList = viewModel.shopsInfo.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadShops(context)
    }

    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                zoomControlsEnabled = true,
                myLocationButtonEnabled = true
            )
        )
    }

    val properties by remember {
        mutableStateOf(
            MapProperties(
                isMyLocationEnabled = true
            )
        )
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = rememberCameraPositionState {
            position = viewModel.cameraPosition
        },
        properties = properties,
        uiSettings = uiSettings
    ) {
        shopList.value.forEach { shop ->
            Marker(
                state = MarkerState(position = LatLng(shop.latLng.latitude, shop.latLng.longitude)),
                title = shop.name,
                snippet = shop.fullAddress
            )
        }
    }
}