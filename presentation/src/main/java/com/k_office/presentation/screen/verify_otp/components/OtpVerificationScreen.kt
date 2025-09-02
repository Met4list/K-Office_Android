package com.k_office.presentation.screen.verify_otp.components

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.k_office.presentation.R
import com.k_office.presentation.base.compose.LoadingDialog
import com.k_office.presentation.base.utils.formatPhoneNumber
import com.k_office.presentation.screen.verify_otp.OtpVerificationViewModel
import kotlinx.coroutines.delay
import timber.log.Timber

@SuppressLint("TimberArgCount")
@Composable
internal fun OtpVerificationScreen(
    viewModel: OtpVerificationViewModel,
    phoneNumber: String,
    onVerificationComplete: (String) -> Unit,
    onRetryClick: () -> Unit,
) {
    val context = LocalContext.current
    val otpValue by viewModel.otpState.collectAsState()
    var remainingSeconds by remember { mutableStateOf(60) }
    var isTimerRunning by remember { mutableStateOf(true) }

    val loading by viewModel.loading.collectAsState()
    val retryOtp by viewModel.retryOtp.collectAsState(false)

    val smsPermissionGranted by viewModel.smsPermissionGranted.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        viewModel.onSMSPermissionsResult(permissions)
    }

    val consentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val message = result.data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
            Timber.d("OtpScreen", "Consent result OK. Message length: ${message?.length}")
            message?.let { viewModel.onSMSReceived(it) }
        } else {
            Timber.w("OtpScreen", "Consent result not OK: ${result.resultCode}")
        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkSMSPermissions(context)
        if (!smsPermissionGranted) {
            val smsPermission =
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS)
            val readPermission =
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS)

            if (smsPermission != android.content.pm.PackageManager.PERMISSION_GRANTED ||
                readPermission != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.READ_SMS
                    )
                )
            }
        }
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
            Timber.d("OtpScreen", "Retry requested, restarting SMS User Consent")
            context.startSmsRetriever()
        }
    }

    LaunchedEffect(Unit) {
        Timber.d("OtpScreen", "Starting SMS User Consent (no SMS permission required)")
        context.startSmsRetriever()
    }

    DisposableEffect(key1 = Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
                    val extras = intent.extras
                    val status = extras?.get(SmsRetriever.EXTRA_STATUS) as? Status
                    when (status?.statusCode) {
                        CommonStatusCodes.SUCCESS -> {
                            Timber.d("OtpScreen", "SMS_RETRIEVED_ACTION: SUCCESS")
                            val consentIntent = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                            if (consentIntent != null) {
                                Timber.d("OtpScreen", "Launching consent intent")
                                consentLauncher.launch(consentIntent)
                            } else {
                                Timber.w("OtpScreen", "Consent intent is null")
                            }
                        }
                        CommonStatusCodes.TIMEOUT -> {
                            Timber.w("OtpScreen", "SMS_RETRIEVED_ACTION: TIMEOUT - restarting user consent")
                            context?.startSmsRetriever()
                        }
                        else -> {
                            Timber.w("OtpScreen", "SMS_RETRIEVED_ACTION: Unknown status ${status?.statusCode}")
                        }
                    }
                }
            }
        }

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.registerReceiver(receiver, intentFilter, Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(receiver, intentFilter)
        }

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    if (loading) {
        LoadingDialog()
    } else {
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

            OtpInputField(otpValue = otpValue ?: "") { newValue ->
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
                enabled = otpValue?.length == 4,
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
}

@Composable
private fun OtpInputField(
    otpValue: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = otpValue,
        onValueChange = { newValue ->
            if (newValue.all { it.isDigit() } && newValue.length <= 4) {
                onValueChange(newValue)
            }
        },
        modifier = Modifier.fillMaxWidth(),
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

private fun Context.startSmsRetriever() {
    try {
        val client = SmsRetriever.getClient(this)
        client.startSmsUserConsent(null)
            .addOnSuccessListener { Timber.d("OtpViewModel", "SMS Retriever started successfully") }
            .addOnFailureListener { e -> Timber.e("OtpViewModel", "Failed to start SMS Retriever", e) }
    } catch (e: Exception) {
        Timber.e("OtpViewModel", "Exception starting SMS Retriever: ${e.message}", e)
    }
}