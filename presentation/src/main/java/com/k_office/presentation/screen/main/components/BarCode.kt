package com.k_office.presentation.screen.main.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.k_office.domain.model.CurrentUserInfoModel
import com.k_office.presentation.R
import com.k_office.presentation.base.utils.QRCodeHelper

@Composable
internal inline fun BarCode(
    currentUser: CurrentUserInfoModel?,
    crossinline onClick: () -> Unit
) {

    var barcodeWidth by remember {
        mutableStateOf(1)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged {
                if (it.width > barcodeWidth) barcodeWidth = it.width
            }.clickable { onClick.invoke() }) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = QRCodeHelper.generateQRCode(
                    content = currentUser?.code,
                    width = barcodeWidth,
                    height = 250
                ),
                contentDescription = "Generated QRCode"
            )

            Spacer(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = stringResource(R.string.scan_in_shop),
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = Gray
            )
        }
    }
}