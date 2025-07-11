package com.k_office.presentation.base.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal inline fun ConfirmationDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    crossinline onConfirm: () -> Unit,
    crossinline onCancel: () -> Unit
) {
    val textMessage: (@Composable () -> Unit)? = {
        if (message.isNotEmpty()) @Composable {
            Text(text = message)
        } else null
    }

    AlertDialog(
        onDismissRequest = {
            onCancel()
        },
        title = {
            Text(text = title)
        },
        text = textMessage,
        confirmButton = {
            TextButton(
                onClick = { onConfirm.invoke() },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onCancel.invoke() }
            ) {
                Text(dismissText)
            }
        },
        modifier = Modifier.padding(16.dp)
    )
}
