package com.k_office.data.storage

import com.k_office.data.model.UserModel
import kotlinx.coroutines.flow.Flow

interface CurrentUserStorage {

    suspend fun insertUser(currentUser: UserModel)

    suspend fun clear()

    fun getUser(): Flow<UserModel?>

    fun getUserId(): Flow<String>

    fun getBonusCard(): Flow<String>

    fun getUsername(): Flow<String>

    fun phoneNumber(): Flow<String>

    fun getBalance(): Flow<Float>

    fun getCode(): Flow<String>

    fun isLoggedIn(): Flow<Boolean>
}