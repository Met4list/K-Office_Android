package com.k_office.domain.base

interface Mapper<R, M> {

    fun mapTo(response: R): M

    fun mapFrom(model: M): R
}