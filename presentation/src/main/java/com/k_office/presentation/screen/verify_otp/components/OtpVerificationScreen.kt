package com.k_office.presentation.screen.verify_otp.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.k_office.presentation.R
import com.k_office.presentation.base.compose.LoadingDialog
import com.k_office.presentation.base.utils.formatPhoneNumber
import com.k_office.presentation.screen.verify_otp.OtpVerificationViewModel
import kotlinx.coroutines.delay

@Composable
internal fun OtpVerificationScreen(
    viewModel: OtpVerificationViewModel,
    phoneNumber: String,
    onVerificationComplete: (String) -> Unit,
    onRetryClick: () -> Unit,
) {
    val otpValue by viewModel.otpState.collectAsStateWithLifecycle()
    val otpFilledFromSms by viewModel.otpFilledFromSms.collectAsStateWithLifecycle()
    var remainingSeconds by remember { mutableStateOf(60) }
    var isTimerRunning by remember { mutableStateOf(true) }
    var autoSubmitted by remember { mutableStateOf(false) }

    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val retryOtp by viewModel.retryOtp.collectAsStateWithLifecycle(false)
    val focusRequester = remember { FocusRequester() }

    // Автопродовження лише якщо код реально підставили з SMS
    LaunchedEffect(otpValue, otpFilledFromSms) {
        val code = otpValue.orEmpty()
        if (otpFilledFromSms && code.length == 4 && !autoSubmitted) {
            autoSubmitted = true
            onVerificationComplete(code)
        }
    }

    LaunchedEffect(Unit) {
        // Клавіатура як запасний шлях, якщо SMS не перехопиться
        focusRequester.requestFocus()
    }

    LaunchedEffect(key1 = isTimerRunning) {
        if (isTimerRunning) {
            while (remainingSeconds > 0) {
                delay(1000L)
                remainingSeconds--
            }
            isTimerRunning = false
        }
    }

    LaunchedEffect(retryOtp) {
        if (retryOtp) {
            remainingSeconds = 60
            isTimerRunning = true
            autoSubmitted = false
        }
    }

    if (loading) {
        LoadingDialog()
    }
    Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = stringResource(R.string.otp_verification_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(
                    R.string.otp_verification_description,
                    phoneNumber.formatPhoneNumber()
                ),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(40.dp))

            OtpInputField(
                otpValue = otpValue ?: "",
                readOnly = otpFilledFromSms,
                focusRequester = focusRequester,
            ) { newValue ->
                if (newValue.length <= 4) {
                    viewModel.onOtpReceived(newValue)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.did_not_receive_code),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                if (remainingSeconds > 0) {
                    Text(
                        text = " (${remainingSeconds}s)",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = colorResource(R.color.blue_light)
                    )
                } else {
                    TextButton(onClick = onRetryClick) {
                        Text(
                            text = stringResource(R.string.retry),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorResource(R.color.blue_primary),
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onVerificationComplete(otpValue ?: "") },
                // Сіра й недоступна лише після перехоплення SMS; інакше клієнт тисне сам
                enabled = !otpFilledFromSms && !loading && otpValue?.length == 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.LightGray,
                    containerColor = colorResource(R.color.blue_primary)
                )
            ) {
                Text(stringResource(R.string.verify_otp))
            }
        }
}

@Composable
private fun OtpInputField(
    otpValue: String,
    readOnly: Boolean = false,
    focusRequester: FocusRequester,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = otpValue,
        onValueChange = { newValue ->
            if (newValue.all { it.isDigit() } && newValue.length <= 4) {
                onValueChange(newValue)
            }
        },
        readOnly = readOnly,
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        label = { Text(stringResource(R.string.enter_otp)) },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorResource(R.color.blue_primary),
            unfocusedBorderColor = Color.Gray
        )
    )
}
