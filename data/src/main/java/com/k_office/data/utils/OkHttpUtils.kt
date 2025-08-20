package com.k_office.data.utils

import okhttp3.OkHttpClient

internal fun OkHttpClient.Builder.addDefaultInterceptor(headers: Map<String, String> = mapOf()): OkHttpClient.Builder {
    return addInterceptor { chain ->
        val builder = chain
            .request()
            .newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")

        if (headers.isNotEmpty()) {
            headers.forEach { (key, value) ->
                builder.addHeader(key, value)
            }
        }

        chain.proceed(builder.build())
    }
}