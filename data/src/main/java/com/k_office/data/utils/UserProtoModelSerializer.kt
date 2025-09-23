package com.k_office.data.utils

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.k_office.data.storage.UserProtoModelOuterClass
import java.io.InputStream
import java.io.OutputStream

object UserProtoModelSerializer: Serializer<UserProtoModelOuterClass.UserProtoModel> {
    override val defaultValue: UserProtoModelOuterClass.UserProtoModel
        get() = UserProtoModelOuterClass.UserProtoModel.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): UserProtoModelOuterClass.UserProtoModel {
        try {
            return UserProtoModelOuterClass.UserProtoModel.parseFrom(input)
        } catch (t: Throwable) {
            throw CorruptionException("Cannot read proto.", t)
        }
    }

    override suspend fun writeTo(
        t: UserProtoModelOuterClass.UserProtoModel,
        output: OutputStream,
    ) = t.writeTo(output)
}