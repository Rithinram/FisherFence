package com.fisherfence.maritime.presentation.common

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Cyclone
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fisherfence.maritime.data.local.AlertEvent
import com.fisherfence.maritime.presentation.theme.*
import com.fisherfence.maritime.utils.SoundVibratorManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AlertOverlay(
    alert: AlertEvent,
    onDismiss: () -> Unit,
    onSendLocation: () -> Unit = {},
    onCallCoastGuard: () -> Unit = {},
    onContactFamily: () -> Unit = {}
) {
    // Intercept back key to prevent dismissing
    BackHandler(enabled = true) {
        // Do nothing, blocks back navigation
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val borderAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    val scaleFactor by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        when (alert.alertType) {
            "BORDER_CAUTION" -> {
                CautionAlertContent(
                    alert = alert,
                    pulseAlpha = borderAlpha,
                    scaleFactor = scaleFactor,
                    onAcknowledge = onDismiss,
                    onSendLocation = onSendLocation
                )
            }
            "BORDER_CRITICAL" -> {
                CriticalAlertContent(
                    alert = alert,
                    pulseAlpha = borderAlpha,
                    scaleFactor = scaleFactor,
                    onAcknowledge = onDismiss,
                    onCallCoastGuard = onCallCoastGuard
                )
            }
            "SOS" -> {
                SosAlertContent(
                    alert = alert,
                    scaleFactor = scaleFactor,
                    onCancel = onDismiss
                )
            }
            "CYCLONE" -> {
                CycloneAlertContent(
                    alert = alert,
                    pulseAlpha = borderAlpha,
                    scaleFactor = scaleFactor,
                    onAcknowledge = onDismiss,
                    onContactFamily = onContactFamily
                )
            }
        }
    }
}

@Composable
fun CautionAlertContent(
    alert: AlertEvent,
    pulseAlpha: Float,
    scaleFactor: Float,
    onAcknowledge: () -> Unit,
    onSendLocation: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .border(
                width = 3.dp,
                color = WarningAmber.copy(alpha = pulseAlpha),
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Caution",
                tint = WarningAmber,
                modifier = Modifier
                    .size(80.dp)
                    .scale(scaleFactor)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "⚠ CAUTION — Approaching Border",
                color = WarningAmber,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "You are 5 km from the International Maritime Boundary Line. Reduce speed and change course immediately.",
                color = TextWhite,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Current Distance: 5.0 km",
                color = WarningAmber,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onAcknowledge,
                colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("ACKNOWLEDGE", color = Color.Black, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onSendLocation,
                border = BorderStroke(1.dp, PrimaryTeal),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryTeal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("SEND LOCATION TO COAST GUARD", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CriticalAlertContent(
    alert: AlertEvent,
    pulseAlpha: Float,
    scaleFactor: Float,
    onAcknowledge: () -> Unit,
    onCallCoastGuard: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .border(
                width = 3.dp,
                color = ErrorRed.copy(alpha = pulseAlpha),
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = "Critical Border Crossing",
                tint = ErrorRed,
                modifier = Modifier
                    .size(80.dp)
                    .scale(scaleFactor)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "🚨 CRITICAL — Border Crossing Detected",
                color = ErrorRed,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "You have entered a restricted maritime zone. Stop immediately and return to Indian waters. Coast Guard has been automatically notified.",
                color = TextWhite,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onAcknowledge,
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("I UNDERSTAND — RETURNING", color = TextWhite, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onCallCoastGuard,
                border = BorderStroke(1.dp, TextWhite),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("CALL COAST GUARD", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SosAlertContent(
    alert: AlertEvent,
    scaleFactor: Float,
    onCancel: () -> Unit
) {
    var showCancelConfirm by remember { mutableStateOf(false) }

    if (showCancelConfirm) {
        AlertDialog(
            onDismissRequest = { showCancelConfirm = false },
            title = { Text("Cancel SOS?") },
            text = { Text("Are you sure you want to cancel the SOS distress signal?") },
            confirmButton = {
                TextButton(onClick = {
                    showCancelConfirm = false
                    onCancel()
                }) {
                    Text("YES, CANCEL", color = ErrorRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelConfirm = false }) {
                    Text("NO, KEEP ACTIVE", color = TextGrey)
                }
            },
            containerColor = DarkCardSurface,
            titleContentColor = TextWhite,
            textContentColor = TextGrey
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ErrorRed)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CrisisAlert,
                contentDescription = "SOS Alert",
                tint = TextWhite,
                modifier = Modifier
                    .size(100.dp)
                    .scale(scaleFactor)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "SOS DISTRESS SIGNAL SENT",
                color = TextWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Your location has been broadcast to Coast Guard and your registered family contacts. Help is on the way.",
                color = TextWhite,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.height(32.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Current Coordinates:",
                        color = TextGrey,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Lat: ${String.format("%.4f", alert.latitude)} N | Lon: ${String.format("%.4f", alert.longitude)} E",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = "Time: ${SimpleDateFormat("hh:mm:ss a", Locale.ENGLISH).format(Date(alert.timestamp))}",
                        color = TextGrey,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(48.dp))
            OutlinedButton(
                onClick = { showCancelConfirm = true },
                border = BorderStroke(2.dp, TextWhite),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("CANCEL SOS", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun CycloneAlertContent(
    alert: AlertEvent,
    pulseAlpha: Float,
    scaleFactor: Float,
    onAcknowledge: () -> Unit,
    onContactFamily: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rotate")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .border(
                width = 3.dp,
                color = CyclonePurple.copy(alpha = pulseAlpha),
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Cyclone,
                contentDescription = "Cyclone Warning",
                tint = CyclonePurple,
                modifier = Modifier
                    .size(80.dp)
                    .scale(scaleFactor)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "🌀 CYCLONE WARNING — Return to Shore",
                color = CyclonePurple,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Cyclone Alert issued by IMD. Wind Speed: 65 km/h | Wave Height: 3.2 m | Estimated landfall in 18 hours. Return to shore immediately.",
                color = TextWhite,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            SuggestionChip(
                onClick = {},
                label = { Text("DANGER", color = Color.White) },
                colors = SuggestionChipDefaults.suggestionChipColors(containerColor = ErrorRed),
                border = null
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onAcknowledge,
                colors = ButtonDefaults.buttonColors(containerColor = CyclonePurple),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("UNDERSTOOD — RETURNING", color = TextWhite, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onContactFamily,
                border = BorderStroke(1.dp, PrimaryTeal),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryTeal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("CONTACT FAMILY", fontWeight = FontWeight.Bold)
            }
        }
    }
}
