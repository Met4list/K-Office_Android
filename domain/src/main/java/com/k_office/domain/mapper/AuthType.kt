package com.k_office.domain.mapper

enum class AuthType {
    REGISTER,
    LOGIN;

    companion object {
        fun findByType(parsedType: String): AuthType? {
            for (type in values()) {
                if (type.name == parsedType) {
                    return type
                }
            }
            return null
        }
    }
}