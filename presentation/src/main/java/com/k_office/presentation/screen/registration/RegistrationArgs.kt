package com.k_office.presentation.screen.registration

import com.k_office.presentation.base.utils.Args
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegistrationArgs(
    val phoneNumber: String
): Args
