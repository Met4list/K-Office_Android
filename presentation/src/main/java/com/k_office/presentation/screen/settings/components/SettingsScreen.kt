package com.k_office.presentation.screen.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.k_office.presentation.base.utils.openNotificationSettings

@Composable
internal inline fun SettingsScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        SettingsOptions()
    }
}

@Composable
private inline fun SettingsOptions(modifier: Modifier = Modifier) {

    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 8.dp) // Add a little padding at the very top of the content
    ) {
        item {
            // Wrap multiple settings items in a Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp), // Padding for the card itself
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, Color.LightGray),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                // Content inside the card
                SettingsOption(
                    title = "App theme",
                    description = "App theme settings",
                    onClick = { /* Handle click for App theme */ }
                )
                // Internal divider only within the card
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                SettingsOption(
                    title = "Sound of notifications",
                    description = "Customize the sound of notifications",
                    onClick = context::openNotificationSettings
                )
            }
        }
    }
}

@Composable
private inline fun SettingsOption(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    crossinline onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = { onClick.invoke() }) // Make the entire item clickable
            .padding(horizontal = 16.dp, vertical = 12.dp) // Padding for content
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}