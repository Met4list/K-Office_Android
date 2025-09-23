package com.k_office.data.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.k_office.data.model.UserModel
import com.k_office.data.utils.UserProtoModelSerializer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.userDataStore: DataStore<UserProtoModelOuterClass.UserProtoModel> by dataStore(
    fileName = "user_model",
    serializer = UserProtoModelSerializer
)

class CurrentUserStorageImpl(private val dataStore: DataStore<UserProtoModelOuterClass.UserProtoModel>) :
    CurrentUserStorage {

    override suspend fun insertUser(currentUser: UserModel) {
        dataStore.updateData {
            it.toBuilder()
                .setId(currentUser.id)
                .setName(currentUser.name)
                .setTelephone(currentUser.telephone)
                .setBonusCard(currentUser.bonusCard)
                .setSum(currentUser.sum ?: 0f)
                .setCode(currentUser.code)
                .setCreatedAt(currentUser.createdAt)
                .build()
        }
    }

    override fun getUser(): Flow<UserModel?> {
        return dataStore.data.map { userProto ->
            if (userProto == UserProtoModelOuterClass.UserProtoModel.getDefaultInstance()) {
                null
            } else {
                UserModel(
                    id = userProto.id,
                    name = userProto.name,
                    telephone = userProto.telephone,
                    bonusCard = userProto.bonusCard,
                    sum = userProto.sum,
                    code = userProto.code,
                    address = null,
                    createdAt = ""
                )
            }
        }
    }

    override fun getUserId(): Flow<String> {
        return dataStore.data.map { it.id.orEmpty() }
    }

    override fun getBonusCard(): Flow<String> {
        return dataStore.data.map { it.bonusCard.orEmpty() }
    }

    override fun getUsername(): Flow<String> {
        return dataStore.data.map { it.name.orEmpty() }
    }

    override fun phoneNumber(): Flow<String> {
        return dataStore.data.map { it.telephone.orEmpty() }
    }

    override fun getBalance(): Flow<Float> {
        return dataStore.data.map { it.sum }
    }

    override fun getCode(): Flow<String> {
        return dataStore.data.map { it.code.orEmpty() }
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return dataStore.data.map { it != UserProtoModelOuterClass.UserProtoModel.getDefaultInstance() }
    }

    override suspend fun clear() {
        dataStore.updateData {
            UserProtoModelOuterClass.UserProtoModel.getDefaultInstance()
        }
    }
}