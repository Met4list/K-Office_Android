package com.k_office.presentation.screen.verify_otp.args

import com.k_office.presentation.base.utils.Args
import kotlinx.parcelize.Parcelize

@Parcelize
data class VerifyOtpArgs(
    val phoneNumber: String,
    val type: String
): Args
