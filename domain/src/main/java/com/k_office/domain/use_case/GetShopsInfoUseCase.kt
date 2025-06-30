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

            // Тепер ми перетворюємо кожен Shop, щоб геокодувати адресу
            shopData.shops.map { shop ->
                // Створюємо нову копію Shop з оновленими координатами
                shop.copy(latLng = getLatLngFromAddress(context, shop.fullAddress))
            }
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList() // Повертаємо порожній список або обробляємо помилку
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList() // Обробляємо помилки парсингу JSON або геокодування
        }
    }

//    override suspend fun invoke(context: Context): List<Shop> = withContext(Dispatchers.IO) {
//        try {
//            val jsonString = context.resources.openRawResource(R.raw.shop_values)
//                .bufferedReader()
//                .use { it.readText() }
//
//            val shopData = Gson().fromJson(jsonString, ShopValuesModel::class.java)
//            shopData.shops
//        } catch (e: IOException) {
//            e.printStackTrace()
//            emptyList() // Return an empty list or handle the error appropriately
//        } catch (e: Exception) {
//            e.printStackTrace()
//            emptyList() // Handle JSON parsing errors
//        }
//    }

    private suspend fun getLatLngFromAddress(context: Context, address: String): LatLng {
        return return withContext(Dispatchers.IO) {
            val geocoder = Geocoder(context, Locale("uk", "UA")) // Вказуємо українську локаль
            try {
                val addresses = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Для API 33+ (Android 13) використовуємо цей шлях.
                    // Синхронний метод все ще доступний, хоч і може бути застарілим для новіших API.
                    // Виконання в Dispatchers.IO гарантує, що він не блокуватиме UI потік.
                    geocoder.getFromLocationName(address, 1)
                } else {
                    // Для старих версій Android (< API 33) використовуємо синхронний (блокуючий) метод.
                    // @Suppress("DEPRECATION") приховує попередження про застарілий метод.
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocationName(address, 1)
                }

                if (!addresses.isNullOrEmpty()) {
                    val location = addresses[0]
                    LatLng(location.latitude, location.longitude)
                } else {
                    // Якщо адреса не знайдена, повертаємо дефолтні або нульові координати.
                    // Розгляньте можливість повернення null або іншого спеціального значення
                    // для кращої обробки помилок у вашому додатку.
                    LatLng(0.0, 0.0)
                }
            } catch (e: IOException) {
                // Обробка помилок мережі або сервісу геокодування.
                // Наприклад, відсутність підключення до Інтернету або недоступність сервісів Google Play.
                e.printStackTrace()
                LatLng(0.0, 0.0)
            } catch (e: Exception) {
                // Обробка інших потенційних помилок геокодування.
                e.printStackTrace()
                LatLng(0.0, 0.0)
            }
        }
    }
}

