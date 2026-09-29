package com.fisherfence.maritime.presentation.common

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fisherfence.maritime.data.local.AlertEvent
import com.fisherfence.maritime.presentation.theme.PrimaryTeal
import com.fisherfence.maritime.presentation.theme.WarningAmber
import com.fisherfence.maritime.utils.SoundVibratorManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SosTakeoverScreen(
    alert: AlertEvent,
    role: String, // "coastguard" or "family"
    onDismiss: () -> Unit,
    onResolve: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Looping siren and vibration control
    DisposableEffect(Unit) {
        SoundVibratorManager.startAlarm(context)
        onDispose {
            SoundVibratorManager.stopAlarm()
        }
    }

    // Intercept back key to prevent escape
    BackHandler(enabled = true) {
        // Do nothing, blocks back key
    }

    // Centered pulsing "🚨 SOS" animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_sos")
    val scaleFactor by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Countdown / Count-up timers
    var elapsedSeconds by remember { mutableLongStateOf(0L) }
    var familyCountdownSeconds by remember { mutableIntStateOf(30) }
    var dispatchConfirmed by remember { mutableStateOf(false) }
    var showFalseAlarmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(alert) {
        val startTime = alert.timestamp
        while (true) {
            elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000
            if (familyCountdownSeconds > 0) {
                familyCountdownSeconds--
            }
            delay(1000)
        }
    }

    val formatElapsedTime = { seconds: Long ->
        val m = seconds / 60
        val s = seconds % 60
        String.format("%02d:%02d", m, s)
    }

    if (showFalseAlarmDialog) {
        AlertDialog(
            onDismissRequest = { showFalseAlarmDialog = false },
            title = { Text("Confirm False Alarm?", color = Color.White) },
            text = { Text("Are you sure you want to mark this distress alert as a false alarm? The alarm will be cleared.", color = Color.LightGray) },
            confirmButton = {
                TextButton(onClick = {
                    showFalseAlarmDialog = false
                    onResolve() // resolve alert
                }) {
                    Text("YES, FALSE ALARM", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFalseAlarmDialog = false }) {
                    Text("CANCEL", color = Color.LightGray)
                }
            },
            containerColor = Color(0xFF1E1E2C)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8B0000)) // Deep red background
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Section - Pulsing SOS
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "🚨 SOS",
                    color = Color.White,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.scale(scaleFactor)
                )
                
                if (role == "coastguard") {
                    Text(
                        text = "ELAPSED TIME: ${formatElapsedTime(elapsedSeconds)}",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                } else {
                    Text(
                        text = "YOUR FISHERMAN HAS TRIGGERED AN SOS",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // 2. Center Section - Details Card and Map Side-by-Side (or Stacked)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Details Card (White on Red)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "FISHERMAN IN DISTRESS",
                            color = Color(0xFF8B0000),
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val timeStr = SimpleDateFormat("dd MMM hh:mm:ss a", Locale.ENGLISH).format(Date(alert.timestamp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Name:", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Raja Kumar", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 13.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Boat ID:", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("TN-04-MM-1234", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Last Position:", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("${String.format("%.4f", alert.latitude)}°N, ${String.format("%.4f", alert.longitude)}°E", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Distance to Shore:", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("22.4 km", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Time:", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(timeStr, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                // Mini Leaflet Map viewport
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                ) {
                    CustomFallbackMap(
                        modifier = Modifier.fillMaxSize(),
                        boatLocations = listOf(Pair("Raja Kumar", Pair(alert.latitude, alert.longitude))),
                        boatStatuses = mapOf("Raja Kumar" to "SOS"),
                        centerLocation = Pair(alert.latitude, alert.longitude),
                        zoomLevel = 13
                    )
                }
            }

            // 3. Bottom Section - Actions
            if (role == "coastguard") {
                if (dispatchConfirmed) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(2.dp, Color.Green),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00C853), modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("RESCUE DISPATCHED", color = Color(0xFF00C853), fontWeight = FontWeight.Black, fontSize = 16.sp)
                            Text("Patrol Unit ETA: 45 minutes", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    SoundVibratorManager.stopAlarm()
                                    onResolve()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("DISMISS", color = Color.White)
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                SoundVibratorManager.stopAlarm()
                                dispatchConfirmed = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "DISPATCH RESCUE TEAM",
                                color = Color(0xFF8B0000),
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }

                        OutlinedButton(
                            onClick = { showFalseAlarmDialog = true },
                            border = BorderStroke(2.dp, Color.White),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("MARK AS FALSE ALARM", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            } else {
                // Family Role Actions
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Coast Guard has been notified automatically",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                    )

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Calling Coast Guard (1800-123-4567)... [Mock]")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CALL COAST GUARD — 1800-123-4567", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Calling Fisherman Raja Kumar (+91 98765 43210)... [Mock]")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CALL FISHERMAN", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    if (familyCountdownSeconds > 0) {
                        OutlinedButton(
                            onClick = {},
                            enabled = false,
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("You can dismiss in ${familyCountdownSeconds}s...", color = Color.White.copy(alpha = 0.5f))
                        }
                    } else {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("DISMISS TAKEOVER", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
