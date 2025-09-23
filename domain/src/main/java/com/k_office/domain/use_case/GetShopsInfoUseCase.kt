package com.k_office.domain.use_case

import android.content.Context
import android.location.Geocoder
import android.os.Build
import com.google.gson.Gson
import com.k_office.domain.R
import com.k_office.domain.base.BaseUseCase
import com.k_office.domain.base.DataState
import com.k_office.domain.base.toUIText
import com.k_office.domain.model.LatLng
import com.k_office.domain.model.Shop
import com.k_office.domain.model.ShopValuesModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Locale

class GetShopsInfoUseCase : BaseUseCase<Context, Flow<DataState<List<Shop>>>> {

    override suspend fun invoke(context: Context): Flow<DataState<List<Shop>>> = flow {
        try {
            emit(DataState.Loading)
            val inputStream = context.resources.openRawResource(R.raw.shop_values)
            val jsonString = BufferedReader(InputStreamReader(inputStream)).use { it.readText() }

            val shopData = Gson().fromJson(jsonString, ShopValuesModel::class.java)

            emit(DataState.Success(shopData.shops.map { shop ->
                shop.copy(latLng = getLatLngFromAddress(context, shop.fullAddress))
            }))
            emit(DataState.Default)
        } catch (e: IOException) {
            emit(DataState.Failure(e.toUIText()))
        } catch (e: Exception) {
            emit(DataState.Failure(e.toUIText()))
        } finally {
            emit(DataState.Default)
        }
    }

    private suspend fun getLatLngFromAddress(context: Context, address: String): LatLng {
        return withContext(Dispatchers.IO) {
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

