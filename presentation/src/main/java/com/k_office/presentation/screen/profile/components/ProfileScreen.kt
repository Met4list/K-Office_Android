package com.k_office.presentation.screen.profile.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.k_office.presentation.R
import com.k_office.presentation.base.compose.LoadingDialog
import com.k_office.presentation.base.utils.isValidPhoneNumber
import com.k_office.presentation.screen.profile.ProfileViewModel

@Composable
internal fun ProfileScreen(viewModel: ProfileViewModel) {

    val context = LocalContext.current

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val successfullyMessage by viewModel.successfullyMessage.collectAsStateWithLifecycle()

    var phoneNumber by remember { mutableStateOf(TextFieldValue("+380")) }
    var name by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        phoneNumber = phoneNumber.copy(
            text = currentUser?.telephone.toString(),
            selection = TextRange(currentUser?.telephone?.length ?: 0)
        )

        name = currentUser?.name.toString()
    }

    LaunchedEffect(successfullyMessage) {
        if (!successfullyMessage.isNullOrBlank()) {
            Toast.makeText(context, successfullyMessage, Toast.LENGTH_SHORT).show()
        }
    }

    val loading by viewModel.loading.collectAsStateWithLifecycle()

    if (loading) {
        LoadingDialog()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {

                Text(
                    modifier = Modifier.padding(vertical = 12.dp),
                    text = stringResource(R.string.profile_title),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Прізвище та Ім'я") },
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
                    label = { Text("Номер телефону") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = colorResource(R.color.blue_primary))
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Continue button
            Button(
                onClick = { viewModel.updateUser(phoneNumber.text, name) },
                enabled = name != currentUser?.name || (phoneNumber.text != currentUser?.telephone && phoneNumber.text.isValidPhoneNumber()),
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
                Text(text = stringResource(id = R.string.key_continue))
            }
        }
    }
}