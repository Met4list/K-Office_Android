package com.k_office.data.storage

import com.k_office.data.model.UserModel

interface CurrentUserStorage {

    fun insertUser(currentUser: UserModel)

    fun clear()

    fun getUser(): UserModel?

    fun getUserId(): String

    fun getBonusCard(): String

    fun username(): String

    fun phoneNumber(): String

    fun getBalance(): Float

    fun getCode(): String

}