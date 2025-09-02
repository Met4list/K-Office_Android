package com.k_office.domain.use_case

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.k_office.domain.R
import com.k_office.domain.base.BaseUseCase
import com.k_office.domain.base.DataState
import com.k_office.domain.base.toUIText
import com.k_office.domain.model.AdsBanner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import java.io.InputStreamReader

class GetAdsBannersUseCase : BaseUseCase<Context, Flow<DataState<List<AdsBanner>>>> {
    override suspend fun invoke(context: Context): Flow<DataState<List<AdsBanner>>> = flow {
        try {
            emit(DataState.Loading)
            emit(
                DataState.Success(
                    context.resources.openRawResource(R.raw.ads_banners).use { inputStream ->
                        InputStreamReader(inputStream).use { reader ->
                            val listType = object : TypeToken<List<AdsBanner>>() {}.type
                            Gson().fromJson(reader, listType)
                        }
                    })
            )
            emit(DataState.Default)
        } catch (e: IOException) {
            emit(DataState.Failure(e.toUIText()))
        } catch (e: Exception) {
            emit(DataState.Failure(e.toUIText()))
        }
    }
}