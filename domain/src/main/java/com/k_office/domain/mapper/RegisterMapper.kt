package com.k_office.domain.mapper

import com.k_office.data.request.RegisterUserRequest
import com.k_office.domain.base.Mapper
import com.k_office.domain.model.RegistrationModel

object RegisterMapper : Mapper<RegistrationModel, RegisterUserRequest> {
    override fun mapTo(response: RegistrationModel): RegisterUserRequest = with(response) {
        RegisterUserRequest(
            telephone, name
        )
    }
}