package com.fisherfence.maritime.presentation.common

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fisherfence.maritime.data.local.AlertEvent
import com.fisherfence.maritime.presentation.theme.ErrorRed
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SosTakeoverOverlay(
    alert: AlertEvent,
    role: String,
    onResolve: () -> Unit,
    onFalseAlarm: () -> Unit = {},
    onCall: (String) -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sosPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    var countdown by remember { mutableIntStateOf(30) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            elapsedSeconds++
            if (countdown > 0) countdown--
        }
    }

    // Full screen deep red background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x8B, 0x00, 0x00)) // Deep red #8B0000
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🚨 SOS",
                color = Color.White,
                fontSize = 72.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.scale(pulseScale)
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            // Fisherman details card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (role == "family") "YOUR FISHERMAN HAS TRIGGERED AN SOS" else "FISHERMAN IN DISTRESS",
                        color = Color(0x8B, 0x00, 0x00),
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Name: Raja Kumar", fontWeight = FontWeight.Bold)
                    Text("Boat ID: TN-04-MM-1234")
                    Text("Last Known Location: ${alert.latitude}°N, ${alert.longitude}°E")
                    Text("Time: ${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(alert.timestamp))}")
                    Text("Distance from shore: 22.4 km")
                    
                    if (role == "coastguard") {
                        Text(
                            "Time Elapsed: ${String.format("%02d:%02d", elapsedSeconds / 60, elapsedSeconds % 60)}",
                            color = ErrorRed,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Map preview placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Live SOS Map Location", color = Color.White.copy(alpha = 0.6f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            when (role) {
                "coastguard" -> {
                    Button(
                        onClick = onResolve,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("DISPATCH RESCUE TEAM", color = Color(0x8B, 0x00, 0x00), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onFalseAlarm,
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("MARK AS FALSE ALARM", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                "family" -> {
                    Text(
                        "Coast Guard has been notified automatically",
                        color = Color.White,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Button(
                        onClick = { onCall("Coast Guard") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF, 0x8C, 0x00)), // Orange
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("CALL COAST GUARD — 1800-123-4567", color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { onCall("Fisherman") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x00, 0x80, 0x80)), // Teal
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("CALL FISHERMAN", color = Color.White)
                    }
                    
                    if (countdown > 0) {
                        Text(
                            "You can dismiss in ${countdown}s...",
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    } else {
                        TextButton(onClick = onResolve, modifier = Modifier.padding(top = 8.dp)) {
                            Text("DISMISS", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
