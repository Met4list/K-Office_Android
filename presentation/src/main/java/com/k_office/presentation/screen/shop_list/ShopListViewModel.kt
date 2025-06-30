package com.k_office.presentation.screen.shop_list

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.k_office.domain.model.Shop
import com.k_office.domain.use_case.GetShopsInfoUseCase
import com.k_office.presentation.base.view_model.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopListViewModel @Inject constructor(
    private val getShopsInfoUseCase: GetShopsInfoUseCase
) : BaseViewModel() {

    private val _shopsInfo = MutableStateFlow<List<Shop>>(emptyList())
    val shopsInfo = _shopsInfo.asStateFlow()

    var selectedShopForModal by mutableStateOf<Shop?>(null)

    var showModalBottomSheet by mutableStateOf(false)

    var cameraPosition by mutableStateOf(
        CameraPosition.fromLatLngZoom(
            LatLng(49.2331, 28.4682),
            12f
        )
    )
        private set

    fun onShopMapClick(shop: Shop) {
        selectedShopForModal = shop
        showModalBottomSheet = true
        Log.d("ShopListViewModel", "Shop ${shop.name} selected for map modal.")
    }

    fun onShopDetailsClick(shop: Shop) {
        selectedShopForModal = shop
        showModalBottomSheet = true // Или другой флаг, если детали в отдельном модале
        Log.d("ShopListViewModel", "Shop ${shop.name} details clicked, opening modal.")
    }

    fun hideShopInfoModal() {
        showModalBottomSheet = false
        selectedShopForModal = null
        Log.d("ShopListViewModel", "Shop info modal hidden.")
    }

    fun updateCameraPosition(position: CameraPosition) {
        cameraPosition = position
    }

    fun loadShops(context: Context) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val shops = getShopsInfoUseCase.invoke(context)
            _shopsInfo.emit(shops)
            Log.d("ShopListViewModel", "Shops loaded: ${shops.size}")
        }
    }
}