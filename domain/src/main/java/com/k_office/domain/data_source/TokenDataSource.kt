package com.k_office.domain.data_source

import android.content.SharedPreferences
import com.google.gson.Gson
import com.k_office.domain.model.TokensModel
import timber.log.Timber

interface TokenDataSource {

    suspend fun insertTokens(tokens: TokensModel)

    suspend fun receiveTokens(): TokensModel?

    suspend fun clear()

    class Base(private val sharedPreferences: SharedPreferences, private val gson: Gson) :
        TokenDataSource {

        private val sharedPrefsEdit = sharedPreferences.edit()

        override suspend fun insertTokens(tokens: TokensModel) {
            val parsedTokens = gson.toJson(tokens)
            sharedPrefsEdit.putString(TOKENS_MODEL_KEY, parsedTokens)
        }

        override suspend fun receiveTokens(): TokensModel? {
            return gson.tokensModel()
        }

        override suspend fun clear() {
            sharedPrefsEdit.clear().apply()
        }

        private fun Gson.tokensModel(): TokensModel? {
            val tokensModel = sharedPreferences.getString(TOKENS_MODEL_KEY, "")
            if (tokensModel.isNullOrBlank()) {
                return null
            } else {
                val model = fromJson(tokensModel, TokensModel::class.java)
                Timber.d("Received tokens from :${this::class.java.name}", model.accessToken, model.refreshToken)
                return model
            }
        }

        companion object {
            private const val TOKENS_MODEL_KEY = "TOKENS_MODEL_KEY"
        }
    }
}