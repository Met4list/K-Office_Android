package com.k_office.presentation.screen.auth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.k_office.presentation.R
import com.k_office.presentation.base.compose.LoadingDialog
import com.k_office.presentation.base.utils.openBrowserPage
import com.k_office.presentation.screen.auth.LoginViewModel

@Composable
internal fun LoginScreen(viewModel: LoginViewModel, onClick: () -> Unit) {
    val context = LocalContext.current

    var phoneNumber by remember { mutableStateOf(TextFieldValue("+380")) }

    val loading by viewModel.loading.collectAsState()

    val annotatedText = buildAnnotatedString {
        append(stringResource(R.string.by_continuing_you_agree))

        pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
        withStyle(style = SpanStyle(color = colorResource(R.color.blue_primary), fontWeight = FontWeight.Medium)) {
            append(stringResource(R.string.login_privacy_policy))
        }
        pop()


        append(" " + stringResource(R.string.and) + " ")

        pushStringAnnotation(tag = "OFFER", annotation = "offer")
        withStyle(style = SpanStyle(color = colorResource(R.color.blue_primary), fontWeight = FontWeight.Medium)) {
            append(stringResource(R.string.offer_agreement))
        }
        pop()
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

            // Phone icon
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Phone Icon",
                tint = colorResource(R.color.blue_primary),
                modifier = Modifier
                    .size(72.dp)
                    .background(Color(0xFFF5F5F5), shape = CircleShape)
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.auth),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Phone number input
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { newValue ->
                    val input = newValue.text

                    // Enforce prefix and max length
                    if (input.length >= 4 && input.startsWith("+380")) {
                        if (input.length <= 13) {
                            // Keep cursor at the end after update
                            phoneNumber = newValue.copy(
                                text = input,
                                selection = TextRange(input.length)
                            )
                        }
                    } else if (input == "+380") {
                        phoneNumber = newValue.copy(
                            text = "+380",
                            selection = TextRange("+380".length)
                        )
                    }
                },
                label = { Text(stringResource(R.string.phone_number)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = colorResource(R.color.blue_primary))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Legal links
            ClickableText(
                text = annotatedText,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                ),
                modifier = Modifier.padding(horizontal = 8.dp),
                onClick = { offset ->
                    annotatedText.getStringAnnotations(tag = "PRIVACY", start = offset, end = offset)
                        .firstOrNull()?.let {
                            context.openBrowserPage(PRIVACY_POLICY_LINK)
                            return@ClickableText
                        }

                    annotatedText.getStringAnnotations(tag = "OFFER", start = offset, end = offset)
                        .firstOrNull()?.let {
                            context.openBrowserPage(OFFER_AGREEMENT_LINK)
                        }
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Anonymous login
            TextButton(onClick = onClick) {
                Text(
                    text = stringResource(R.string.register_now),
                    color = colorResource(R.color.blue_light)
                )
            }

            // Login button
            Button(
                onClick = { viewModel.login(phoneNumber.text) },
                enabled = phoneNumber.text.isNotBlank() || phoneNumber.text.length >= 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.DarkGray,
                    containerColor = colorResource(R.color.blue_primary)
                )
            ) {
                Text(stringResource(R.string.login))
            }
        }
    }
}

private const val PRIVACY_POLICY_LINK = "https://k-office.vn.ua/politika-konfidencijnosti"
private const val OFFER_AGREEMENT_LINK = "https://k-office.vn.ua/publichnij-dogovir-oferta"