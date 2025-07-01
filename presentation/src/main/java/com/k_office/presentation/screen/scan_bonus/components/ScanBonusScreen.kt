package com.k_office.presentation.screen.scan_bonus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.k_office.presentation.R
import com.k_office.presentation.base.utils.QRCodeHelper
import com.k_office.presentation.screen.home.HomeViewModel

@Composable
internal fun ScanBonusScreen(viewModel: HomeViewModel) {

    val currentUser = viewModel.currentUser.collectAsState().value

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Replace with real barcode
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.White, shape = RectangleShape)
                ) {
                    AsyncImage(
                        modifier = Modifier.fillMaxSize(),
                        model = QRCodeHelper.generateQRCode(currentUser?.code),
                        contentDescription = "Generated QRCode"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    stringResource(R.string.name_or_scan_the_barcode),
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Text(
                    text = currentUser?.code.orEmpty(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.blue_primary)
                )
            }
        }
    }
}