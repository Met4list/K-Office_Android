package com.k_office.presentation.screen.main.components

import android.widget.Toast
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
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.k_office.presentation.R
import com.k_office.presentation.base.utils.FragmentUtil
import com.k_office.presentation.base.utils.openBrowserPage
import com.k_office.presentation.screen.dialogs.BonusCardDialog
import com.k_office.presentation.screen.home.HomeViewModel
import com.k_office.presentation.screen.shop_list.ShopListFragment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal inline fun MainScreen(viewModel: HomeViewModel, fragmentManager: FragmentManager) {

    val context = LocalContext.current

    val banners by viewModel.banners.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val successfullyMessage by viewModel.successfullyUpdated.collectAsState()

    var showBonusCard by remember {
        mutableStateOf(false)
    }

    val loading by viewModel.loading.collectAsStateWithLifecycle()

    val refreshState = rememberSwipeRefreshState(loading)

    LaunchedEffect(Unit) {
        viewModel.loadBanners(context)
    }

    if (showBonusCard) {
        BonusCardDialog(currentUser) {
            showBonusCard = false
        }
    }

    LaunchedEffect(successfullyMessage) {
        if (successfullyMessage != null) {
            Toast.makeText(context, successfullyMessage?.getString(context), Toast.LENGTH_SHORT).show()
        }
    }

    SwipeRefresh(
        state = refreshState,
        onRefresh = {
            viewModel.updateUserInfo()
        },
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = trigger,
                contentColor = colorResource(R.color.blue_primary)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            HeaderGreeting(
                name = currentUser?.name.orEmpty(),
                balance = "${currentUser?.sum} бонусів"
            )
            Spacer(modifier = Modifier.height(8.dp))

            StoreLocation(title = "Адреси магазинів") {
                FragmentUtil.setFragmentIfAbsent(ShopListFragment(), fragmentManager, R.id.nav_container)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OrderOnWebsite {
                context.openBrowserPage(ORDER_ON_WEBSITE_LINK)
            }

            Spacer(modifier = Modifier.height(8.dp))

            BarCode(currentUser) {
                showBonusCard = true
            }

            Spacer(modifier = Modifier.height(8.dp))
            AdsBanners(banners = banners)
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
        Text(
            text = buildAnnotatedString {
                withStyle(style = MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                    append(stringResource(R.string.greetings_title))
                }
                append("\n")
                withStyle(
                    style = MaterialTheme.typography.bodyLarge.toSpanStyle().copy(
                        color = colorResource(R.color.blue_primary)
                    )
                ) {
                    append(name)
                }
            },
            maxLines = 2
        )

        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Gray)) {
                    append(stringResource(R.string.balance))
                }
                append("\n")
                withStyle(
                    style = SpanStyle(
                        color = colorResource(R.color.blue_primary),
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(balance)
                }
            },
            textAlign = TextAlign.End,
            maxLines = 2
        )
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

@Composable
internal inline fun OrderOnWebsite(crossinline onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF3F3F3), shape = RoundedCornerShape(12.dp))
            .padding(16.dp)
            .clickable(onClick = { onClick.invoke() }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.Icon(
            Icons.Default.ShoppingCart,
            contentDescription = null,
            tint = Color.Gray
        )
        Text(modifier = Modifier.padding(start = 8.dp), text = stringResource(R.string.order_on_website), fontWeight = FontWeight.Bold)
    }
}

private const val ORDER_ON_WEBSITE_LINK = "http://K-office.in.ua"