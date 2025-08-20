package com.k_office.presentation.base.utils

fun String.formatPhoneNumber(): String {
    val rawNumber = this
    val digits = rawNumber.removePrefix("+38")

    if (digits.length != 9) {
        return rawNumber
    }

    val operatorCode = digits.substring(0, 3)
    val part1 = digits.substring(3, 6)
    val part2 = digits.substring(6, 8)
    val part3 = digits.substring(8, 10)

    return "+38 ($operatorCode) $part1 $part2 $part3"
}