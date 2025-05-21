package com.k_office.presentation.screen.choose_shop.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.k_office.presentation.screen.choose_shop.ChooseShopViewModel

@Composable
internal fun ChooseShopScreen(viewModel: ChooseShopViewModel) {
    val singapore = LatLng(49.2307466, 28.3990018)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 11f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = singapore),
            title = "KOffice Shop",
            snippet = "This is your shop"
        )
    }
}