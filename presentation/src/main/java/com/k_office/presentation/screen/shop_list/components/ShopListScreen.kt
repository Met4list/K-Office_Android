package com.k_office.presentation.screen.shop_list.components

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.k_office.domain.model.Shop
import com.k_office.presentation.R
import com.k_office.presentation.base.utils.openGoogleMapsRoute
import com.k_office.presentation.screen.shop_list.ShopListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ShopListScreen(viewModel: ShopListViewModel) {

    val context = LocalContext.current
    val shops by viewModel.shopsInfo.collectAsStateWithLifecycle()

    val showModalBottomSheet = viewModel.showModalBottomSheet
    val selectedShopForInfoModal = viewModel.selectedShopForModal

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    LaunchedEffect(showModalBottomSheet) {
        if (showModalBottomSheet) {
            scope.launch { sheetState.show() }
        } else {
            scope.launch { sheetState.hide() }
        }
    }

    LaunchedEffect(sheetState.isVisible) {
        if (!sheetState.isVisible && showModalBottomSheet) {
            viewModel.hideShopInfoModal()
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Список одразу з JSON, без очікування GPS і розрахунку дистанції
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(shops) { shop ->
                ShopItem(
                    shop = shop,
                    isSelected = false,
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
                        context.openGoogleMapsRoute(
                            shop.latLng.latitude,
                            shop.latLng.longitude,
                            shop.name
                        )
                    }
                    viewModel.hideShopInfoModal()
                }
            )
        }
    }
}


@Composable
internal fun ShopItem(
    shop: Shop,
    isSelected: Boolean,
    onMapClick: (Shop) -> Unit,
    onDetailsClick: (Shop) -> Unit,
) {
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
                shop.locationDetails.let {
                    if (it.isNotEmpty()) {
                        Text(
                            text = it,
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxHeight()
            ) {
                IconButton(
                    onClick = { onMapClick(shop) },
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

                IconButton(
                    onClick = { onDetailsClick(shop) },
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
