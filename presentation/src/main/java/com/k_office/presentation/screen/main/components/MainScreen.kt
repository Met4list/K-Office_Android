package com.k_office.presentation.screen.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.k_office.presentation.R
import com.k_office.presentation.base.utils.FragmentUtil
import com.k_office.presentation.screen.dialogs.BonusCardDialog
import com.k_office.presentation.screen.home.HomeViewModel
import com.k_office.presentation.screen.shop_list.ShopListFragment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal inline fun MainScreen(viewModel: HomeViewModel, fragmentManager: FragmentManager) {

    val context = LocalContext.current

    val banners = viewModel.banners.collectAsStateWithLifecycle(listOf())
    val currentUser = viewModel.currentUser.collectAsState()

    var showBonusCard by remember {
        mutableStateOf(false)
    }

    val loading by viewModel.loading.collectAsState()

    val refreshState = rememberSwipeRefreshState(loading)

    LaunchedEffect(Unit) {
        viewModel.loadBanners(context)
    }

    if (showBonusCard) {
        BonusCardDialog(currentUser.value) {
            showBonusCard = false
        }
    }

    SwipeRefresh(state = refreshState, onRefresh = {
        viewModel.updateUserInfo()
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            HeaderGreeting(
                name = currentUser.value?.name.orEmpty(),
                balance = "${currentUser.value?.sum ?: 0} бонусів"
            )
            Spacer(modifier = Modifier.height(6.dp))

            StoreLocation(title = "Адреси магазинів") {
                FragmentUtil.setFragmentIfAbsent(ShopListFragment(), fragmentManager, R.id.nav_container)
            }

            Spacer(modifier = Modifier.height(8.dp))

            BarCode(currentUser.value) {
                showBonusCard = true
            }

            Spacer(modifier = Modifier.height(8.dp))
            AdsBanners(banners = banners.value)
        }
    }
}

@Composable
internal inline fun HeaderGreeting(name: String, balance: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9F9F9), shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = stringResource(R.string.greetings_title),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = name,
                color = colorResource(R.color.blue_primary),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = stringResource(R.string.balance), color = Color.Gray)
            Text(
                text = balance,
                color = colorResource(R.color.blue_primary),
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

@Composable
internal inline fun StoreLocation(title: String, crossinline onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF3F3F3), shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
            .clickable(onClick = { onClick.invoke() }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.Icon(
            Icons.Default.LocationOn,
            contentDescription = null,
            tint = Color.Gray
        )
        Text(modifier = Modifier.padding(start = 8.dp), text = title, fontWeight = FontWeight.Bold)
    }
}