package com.k_office.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.k_office.data.model.UserModel
import kotlin.text.orEmpty

class CurrentUserStorageImpl(private val sharedPreferences: SharedPreferences): CurrentUserStorage {

    private val sharedPrefEdit = sharedPreferences.edit()
    private val gson: Gson = Gson()

    override fun insertUser(currentUser: UserModel) {
        val userModel = gson.toJson(currentUser)
        sharedPrefEdit.putString(CURRENT_USER_MODEL_KEY, userModel).apply()
    }

    override fun clear() {
        sharedPrefEdit.clear().apply()
    }

    override fun getUser(): UserModel? {
        return gson.currentUser()
    }

    override fun getUserId(): String {
        return gson.currentUser()?.id.orEmpty()
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

    override fun getBalance(): Float {
        return gson.currentUser()?.sum ?: 0f
    }

    override fun getCode(): String {
        return gson.currentUser()?.code.orEmpty()
    }

    override fun isLoggedIn(): Boolean {
        return gson.currentUser() != null
    }

    private fun Gson.currentUser(): UserModel? {
        val userModel = sharedPreferences.getString(CURRENT_USER_MODEL_KEY, "")
        if (userModel.isNullOrBlank()) {
            return null
        } else {
            return fromJson(userModel, UserModel::class.java)
        }
    }

    companion object {
        private const val CURRENT_USER_MODEL_KEY = "CURRENT_USER_MODEL_KEY"
    }
}