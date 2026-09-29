package com.fisherfence.maritime.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Anchor
import androidx.compose.material.icons.filled.Water
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fisherfence.maritime.presentation.theme.DarkNavyBackground
import com.fisherfence.maritime.presentation.theme.PrimaryTeal
import com.fisherfence.maritime.presentation.theme.SecondaryBlue
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToRoleSelection: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "splashWaves")
    val waveScale by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "waveScale"
    )

    LaunchedEffect(key1 = true) {
        delay(2000)
        onNavigateToRoleSelection()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkNavyBackground, Color(0x06, 0x12, 0x24))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Centered Logo
            Box(contentAlignment = Alignment.Center) {
                // Wave background
                Icon(
                    imageVector = Icons.Default.Water,
                    contentDescription = null,
                    tint = SecondaryBlue.copy(alpha = 0.4f),
                    modifier = Modifier
                        .size(140.dp)
                        .scale(waveScale)
                )
                // Anchor main logo
                Icon(
                    imageVector = Icons.Default.Anchor,
                    contentDescription = "FisherFence Logo",
                    tint = PrimaryTeal,
                    modifier = Modifier.size(75.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "FisherFence",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your safety, our priority",
                color = PrimaryTeal,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
