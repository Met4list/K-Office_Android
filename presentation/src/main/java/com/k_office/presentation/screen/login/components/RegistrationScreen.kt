package com.k_office.presentation.screen.login.components

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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.k_office.presentation.R
import com.k_office.presentation.base.compose.LoadingDialog
import com.k_office.presentation.screen.login.RegistrationViewModel

@Composable
internal fun RegistrationScreen(viewModel: RegistrationViewModel, onClick: () -> Unit) {
    // State variables for new fields (make sure they're declared in your Composable or ViewModel)
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    var phoneNumber by remember { mutableStateOf(TextFieldValue("+380")) }

    val loading by viewModel.loading.collectAsState()

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

            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = "Phone Icon",
                tint = colorResource(id = R.color.blue_primary),
                modifier = Modifier
                    .size(72.dp)
                    .background(Color(0xFFF5F5F5), shape = CircleShape)
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Enter your details",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Name input
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = colorResource(R.color.blue_primary))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Address input
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = colorResource(R.color.blue_primary))
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                label = { Text("Phone Number") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = colorResource(R.color.blue_primary))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Legal links
            Text(
                text = buildAnnotatedString {
                    append("By continuing, you agree to the ")
                    withStyle(style = SpanStyle(color = colorResource(id = R.color.blue_primary))) {
                        append("Privacy Policy")
                    }
                    append(" and the ")
                    withStyle(style = SpanStyle(color = colorResource(R.color.blue_light))) {
                        append("Offer Agreement")
                    }
                },
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Anonymous login
            TextButton(onClick = { onClick.invoke() }) {
                Text(
                    text = "Anonymous login",
                    color = colorResource(R.color.blue_light)
                )
            }

            // Continue button
            Button(
                onClick = { viewModel.registrationBonus(phoneNumber.text, name, address) },
                enabled = name.isNotBlank() && address.isNotBlank() && phoneNumber.text.isNotBlank(), // Enable based on validation
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
                Text("Continue")
            }
        }
    }
}