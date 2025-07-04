package com.k_office.domain.use_case

import android.content.Context
import android.location.Geocoder
import android.os.Build
import com.google.gson.Gson
import com.k_office.domain.R
import com.k_office.domain.base.BaseUseCase
import com.k_office.domain.model.LatLng
import com.k_office.domain.model.Shop
import com.k_office.domain.model.ShopValuesModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Locale

class GetShopsInfoUseCase : BaseUseCase<Context, List<Shop>> {

    override suspend fun invoke(context: Context): List<Shop> = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.resources.openRawResource(R.raw.shop_values)
            val jsonString = BufferedReader(InputStreamReader(inputStream)).use { it.readText() }

            val shopData = Gson().fromJson(jsonString, ShopValuesModel::class.java)

            shopData.shops.map { shop ->
                shop.copy(latLng = getLatLngFromAddress(context, shop.fullAddress))
            }
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private suspend fun getLatLngFromAddress(context: Context, address: String): LatLng {
        return return withContext(Dispatchers.IO) {
            val geocoder = Geocoder(context, Locale("uk", "UA"))
            try {
                val addresses = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocationName(address, 1)
                } else {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocationName(address, 1)
                }

                if (!addresses.isNullOrEmpty()) {
                    val location = addresses[0]
                    LatLng(location.latitude, location.longitude)
                } else {
                    LatLng(0.0, 0.0)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                LatLng(0.0, 0.0)
            } catch (e: Exception) {
                e.printStackTrace()
                LatLng(0.0, 0.0)
            }
        }
    }
}

