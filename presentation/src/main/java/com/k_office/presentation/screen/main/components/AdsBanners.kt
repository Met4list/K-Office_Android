package com.k_office.presentation.screen.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.k_office.domain.model.AdsBanner
import com.k_office.presentation.base.compose.ShimmerPlaceholder
import com.k_office.presentation.base.utils.openBrowserPage
import kotlinx.coroutines.delay

@Composable
internal inline fun AdsBanners(banners: List<AdsBanner>) {

    val context = LocalContext.current

    val pagerState = rememberPagerState(pageCount = { banners.size })

    val localHeight = remember {
        mutableStateOf(1)
    }

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
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
        ) { page ->
            val banner = banners[page]
            BannerItem(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF3F3F3), shape = RoundedCornerShape(12.dp)),
                banner = banner,
                localHeight = localHeight
            ) { clickedBanner ->
                context.openBrowserPage(clickedBanner.link)
            }
        }
    }
}

@Composable
private inline fun BannerItem(
    modifier: Modifier = Modifier,
    banner: AdsBanner,
    localHeight: MutableState<Int>,
    crossinline onClick: (AdsBanner) -> Unit
) {
    val context = LocalContext.current

    val imageRequest = ImageRequest.Builder(context)
        .data(banner.imageUrl)
        .memoryCacheKey(banner.imageUrl)
        .diskCacheKey(banner.imageUrl)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()

    SubcomposeAsyncImage(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick(banner) }
            .onSizeChanged {
                if (it.height > localHeight.value) localHeight.value = it.height
            },
        model = imageRequest,
        loading = {
            ShimmerPlaceholder(modifier = Modifier.fillMaxSize())
        },
//        contentScale = ContentScale.FillHeight,
        contentDescription = banner.altText,
    )
}