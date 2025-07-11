package com.k_office.presentation.base.compose

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode

@Composable
fun ShimmerPlaceholder(modifier: Modifier = Modifier) {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "shimmerTransition")
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f, // Adjust this value based on your content size
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000, // Duration of one shimmer cycle
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ), label = "shimmerTranslateAnimation"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnimation.value - 200f, y = 0f), // Offset the gradient start
        end = Offset(x = translateAnimation.value + 200f, y = 1000f), // Offset the gradient end and angle
        tileMode = TileMode.Clamp
    )

    Box(
        modifier = modifier
            .background(brush)
    )
}