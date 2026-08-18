package com.k_office.domain.use_case

import android.content.Context
import com.google.gson.Gson
import com.k_office.domain.R
import com.k_office.domain.base.BaseUseCase
import com.k_office.domain.base.DataState
import com.k_office.domain.base.toUIText
import com.k_office.domain.model.Shop
import com.k_office.domain.model.ShopValuesModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class GetShopsInfoUseCase : BaseUseCase<Context, Flow<DataState<List<Shop>>>> {

    override suspend fun invoke(context: Context): Flow<DataState<List<Shop>>> = flow {
        try {
            emit(DataState.Loading)
            val inputStream = context.resources.openRawResource(R.raw.shop_values)
            val jsonString = BufferedReader(InputStreamReader(inputStream)).use { it.readText() }

            // Координати беремо лише з JSON — системний Geocoder тут не потрібен
            val shopData = Gson().fromJson(jsonString, ShopValuesModel::class.java)
            emit(DataState.Success(shopData.shops))
            emit(DataState.Default)
        } catch (e: IOException) {
            emit(DataState.Failure(e.toUIText()))
        } catch (e: Exception) {
            emit(DataState.Failure(e.toUIText()))
        } finally {
            emit(DataState.Default)
        }
    }
}
