package com.k_office.domain.data_source

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.k_office.domain.model.CurrentUserInfoModel

interface CurrentUserInfoDataSource {

    fun insertUser(currentUser: CurrentUserInfoModel)

    fun clear()

    fun getUser(): CurrentUserInfoModel?

    fun getBonusCard(): String

    fun username(): String

    fun phoneNumber(): String

    fun getBalance(): Double

    fun getConde(): String

    class Base(context: Context) : CurrentUserInfoDataSource {

        private val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("user_config", Context.MODE_PRIVATE)
        private val sharedPrefEdit = sharedPreferences.edit()
        private val gson: Gson = Gson()

        override fun insertUser(currentUser: CurrentUserInfoModel) {
            val userModel = gson.toJson(currentUser)
            sharedPrefEdit.putString(CURRENT_USER_MODEL_KEY, userModel).apply()
        }

        override fun clear() {
            sharedPrefEdit.clear().apply()
        }

        override fun getUser(): CurrentUserInfoModel? {
            return gson.currentUser()
        }

        override fun getBonusCard(): String {
            return gson.currentUser()?.bonusCard.orEmpty()
        }

        override fun username(): String {
            return gson.currentUser()?.name.orEmpty()
        }

        override fun phoneNumber(): String {
            return gson.currentUser()?.telephone.orEmpty()
        }

        override fun getBalance(): Double {
            return gson.currentUser()?.sum ?: Double.NaN
        }

        override fun getConde(): String {
            return gson.currentUser()?.code.orEmpty()
        }

        private fun Gson.currentUser(): CurrentUserInfoModel? {
            val userModel = sharedPreferences.getString(CURRENT_USER_MODEL_KEY, "")
            if (userModel.isNullOrBlank()) {
                return null
            } else {
                return fromJson<CurrentUserInfoModel>(userModel, CurrentUserInfoModel::class.java)
            }
        }

        companion object {
            private const val CURRENT_USER_MODEL_KEY = "CURRENT_USER_MODEL_KEY"
        }
    }
}