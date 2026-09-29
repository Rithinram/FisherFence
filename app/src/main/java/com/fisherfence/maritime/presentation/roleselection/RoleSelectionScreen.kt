package com.fisherfence.maritime.presentation.roleselection

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Anchor
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fisherfence.maritime.presentation.theme.*

@Composable
fun RoleSelectionScreen(
    onNavigateToLogin: (role: String) -> Unit
) {
    // Background wave animation using float transition
    val waveTransition = rememberInfiniteTransition(label = "waveBackground")
    val waveOffset by waveTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
    ) {
        // Draw moving wave lines in the background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val paint = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 2.dp.toPx()
            )
            // Draw 2 background waves
            val path1 = androidx.compose.ui.graphics.Path()
            path1.moveTo(0f, height * 0.85f)
            for (x in 0..width.toInt() step 20) {
                val y = height * 0.85f + 25f * kotlin.math.sin((x + waveOffset) * 0.008f)
                path1.lineTo(x.toFloat(), y.toFloat())
            }
            drawPath(path = path1, color = SecondaryBlue.copy(alpha = 0.15f), style = paint)

            val path2 = androidx.compose.ui.graphics.Path()
            path2.moveTo(0f, height * 0.9f)
            for (x in 0..width.toInt() step 20) {
                val y = height * 0.9f + 20f * kotlin.math.sin((x - waveOffset + 200) * 0.01f)
                path2.lineTo(x.toFloat(), y.toFloat())
            }
            drawPath(path = path2, color = PrimaryTeal.copy(alpha = 0.12f), style = paint)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "Welcome to FisherFence",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Select your role to get started",
                    color = TextGrey,
                    fontSize = 14.sp
                )
            }

            // Vertically aligned role cards
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RoleCard(
                    title = "Fisherman",
                    description = "Track your voyage, stay safe at sea",
                    icon = Icons.Default.Anchor,
                    onClick = { onNavigateToLogin("fisherman") },
                    modifier = Modifier.weight(1f)
                )

                RoleCard(
                    title = "Coast Guard",
                    description = "Monitor and protect fishermen in real time",
                    icon = Icons.Default.Shield,
                    onClick = { onNavigateToLogin("coastguard") },
                    modifier = Modifier.weight(1f)
                )

                RoleCard(
                    title = "Family Member",
                    description = "Keep track of your loved ones at sea",
                    icon = Icons.Default.Favorite,
                    onClick = { onNavigateToLogin("family") },
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = "Secure Maritime Safety Protocol v1.0",
                color = BorderBlue,
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }
}

@Composable
fun RoleCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderBlue),
        colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryTeal.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "$title Icon",
                    tint = PrimaryTeal,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    color = TextGrey,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
