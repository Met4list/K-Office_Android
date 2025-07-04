package com.k_office.domain.use_case

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.k_office.domain.R
import com.k_office.domain.base.BaseUseCase
import com.k_office.domain.model.AdsBanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStreamReader

class GetAdsBannersUseCase: BaseUseCase<Context, List<AdsBanner>> {
    override suspend fun invoke(context: Context): List<AdsBanner> = withContext(Dispatchers.IO) {
        try {
            context.resources.openRawResource(R.raw.ads_banners).use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    val listType = object : TypeToken<List<AdsBanner>>() {}.type
                    Gson().fromJson(reader, listType)
                }
            } ?: emptyList()
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}