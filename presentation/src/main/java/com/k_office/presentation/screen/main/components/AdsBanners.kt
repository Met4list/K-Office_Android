package com.k_office.presentation.screen.main.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.k_office.domain.model.AdsBanner
import com.k_office.presentation.R
import kotlinx.coroutines.delay
import timber.log.Timber

@Composable
internal inline fun AdsBanners(banners: List<AdsBanner>) {

    val context = LocalContext.current

    val pagerState = rememberPagerState(pageCount = { banners.size })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(5000)
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) { page ->
            val banner = banners[page]
            BannerItem(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF3F3F3), shape = RoundedCornerShape(12.dp)),
                banner = banner
            ) { clickedBanner ->
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(clickedBanner.link))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Timber.e(e)
                    Toast.makeText(
                        context,
                        "Не вдалося відкрити посилання: ${clickedBanner.link}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

@Composable
internal inline fun BannerItem(
    modifier: Modifier = Modifier,
    banner: AdsBanner,
    crossinline onClick: (AdsBanner) -> Unit
) {
    SubcomposeAsyncImage(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick(banner) },
        model = banner.imageUrl,
        loading = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.requiredSize(48.dp),
                    color = colorResource(R.color.blue_primary)
                )
            }
        },
        contentScale = ContentScale.FillHeight,
        contentDescription = banner.altText,
    )
}