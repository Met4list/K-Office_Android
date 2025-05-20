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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.k_office.presentation.screen.auth.LoginViewModel

@Composable
internal fun LoginScreen(viewModel: LoginViewModel, onClick: () -> Unit) {
    var phoneNumber by remember { mutableStateOf("") }

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
            tint = Color.Magenta,
            modifier = Modifier
                .size(72.dp)
                .background(Color(0xFFF5F5F5), shape = CircleShape)
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Phone Login",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Phone number input
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Legal links
        Text(
            text = buildAnnotatedString {
                append("By continuing, you agree to the ")
                withStyle(style = SpanStyle(color = Color.Magenta)) {
                    append("Privacy Policy")
                }
                append(" and the ")
                withStyle(style = SpanStyle(color = Color.Magenta)) {
                    append("Offer Agreement")
                }
            },
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Anonymous login
        TextButton(onClick = onClick) {
            Text(
                text = "Register now",
                color = Color.Magenta
            )
        }

        // Login button
        Button(
            onClick = { viewModel.login(phoneNumber) },
            enabled = phoneNumber.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                disabledContainerColor = Color.LightGray,
                disabledContentColor = Color.DarkGray
            )
        ) {
            Text("Login")
        }
    }
}