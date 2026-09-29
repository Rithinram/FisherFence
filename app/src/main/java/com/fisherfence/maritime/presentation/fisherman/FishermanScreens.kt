package com.fisherfence.maritime.presentation.fisherman

import android.speech.tts.TextToSpeech
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fisherfence.maritime.data.local.AlertEvent
import com.fisherfence.maritime.data.local.TripSession
import com.fisherfence.maritime.data.local.WeatherData
import com.fisherfence.maritime.data.local.ChecklistItem
import com.fisherfence.maritime.data.local.SystemNotification
import com.fisherfence.maritime.presentation.common.AlertOverlay
import com.fisherfence.maritime.presentation.common.CustomFallbackMap
import com.fisherfence.maritime.presentation.common.OnboardingCarousel
import com.fisherfence.maritime.presentation.common.NotificationDrawer
import com.fisherfence.maritime.presentation.theme.*
import com.fisherfence.maritime.utils.GeofenceCalculator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FishermanHomeScreen(
    viewModel: FishermanViewModel,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    val activeAlert by viewModel.activeAlert.collectAsStateWithLifecycle()
    val isTripActive by viewModel.isTripActive.collectAsStateWithLifecycle()
    val currentPosition by viewModel.currentPosition.collectAsStateWithLifecycle()
    val safetyStatus by viewModel.safetyStatus.collectAsStateWithLifecycle()

    // Database & settings streams
    val unreadNotifications by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()
    val notificationsList by viewModel.notifications.collectAsStateWithLifecycle()
    val isOffline by viewModel.isOffline.collectAsStateWithLifecycle()
    
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showSettingsSheet by remember { mutableStateOf(false) }

    // Onboarding Walkthrough
    val settingsManager = viewModel.settingsManager
    var showOnboarding by remember { mutableStateOf(!settingsManager.isOnboardingCompleted("fisherman")) }

    // 1. Full Screen Takeover Alert Modal if any alert is active
    activeAlert?.let { alert ->
        if (alert.alertType != "SOS") {
            AlertOverlay(
                alert = alert,
                onDismiss = { viewModel.dismissActiveAlert() },
                onSendLocation = { viewModel.dismissActiveAlert() },
                onCallCoastGuard = { viewModel.dismissActiveAlert() },
                onContactFamily = { viewModel.dismissActiveAlert() }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(end = 16.dp)
                    ) {
                        Text("FisherFence", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        SuggestionChip(
                            onClick = {},
                            label = { Text("FISHERMAN", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = PrimaryTeal),
                            border = null
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onLogout) {
                        Icon(imageVector = Icons.Default.Logout, contentDescription = "Logout", tint = TextGrey)
                    }
                },
                actions = {
                    // Notification Bell with Badge
                    IconButton(onClick = { 
                        viewModel.markNotificationsAsRead()
                        viewModel.addNotification("System Read", "Marked notifications as read.")
                    }) {
                        BadgedBox(
                            badge = {
                                if (unreadNotifications > 0) {
                                    Badge(containerColor = ErrorRed) { Text(unreadNotifications.toString()) }
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications", tint = Color.White)
                        }
                    }
                    // Demo Controls Toggle Sheet
                    IconButton(onClick = { showSettingsSheet = true }) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Demo Controls", tint = TextGrey)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkNavyBackground)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkCardSurface,
                tonalElevation = 8.dp
            ) {
                val navItems = listOf(
                    Triple(0, Icons.Default.Map, "Map"),
                    Triple(1, Icons.Default.Notifications, "Alerts"),
                    Triple(2, Icons.Default.Cloud, "Weather"),
                    Triple(3, Icons.Default.History, "Trip Log"),
                    Triple(4, Icons.Default.GridOn, "More")
                )
                navItems.forEach { (index, icon, label) ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(imageVector = icon, contentDescription = label) },
                        label = { Text(label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryTeal,
                            selectedTextColor = PrimaryTeal,
                            unselectedIconColor = TextGrey,
                            unselectedTextColor = TextGrey,
                            indicatorColor = BorderBlue
                        )
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = DarkNavyBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Offline Indicator Banner
                AnimatedVisibility(visible = isOffline) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(WarningAmber)
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📡 Offline — Showing cached data. Last synced: 14 min ago",
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Main Tab Content Area
                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        0 -> FishermanMapTab(viewModel, snackbarHostState)
                        1 -> FishermanAlertsTab(viewModel)
                        2 -> FishermanWeatherTab(viewModel)
                        3 -> FishermanTripLogTab(viewModel, snackbarHostState)
                        4 -> FishermanMoreTab(viewModel, { selectedTab = 0 }, snackbarHostState)
                    }
                }
            }

            // Notification Center Drawer (top slide down dropdown)
            NotificationDrawer(
                isOpen = unreadNotifications > 100, // Dummy bound to prevent auto opens, will toggle on bell clicks
                notifications = notificationsList,
                onMarkAllAsRead = { viewModel.markNotificationsAsRead() },
                onClose = {}
            )

            // Onboarding Overlay Walkthrough
            if (showOnboarding) {
                OnboardingCarousel(role = "fisherman") {
                    showOnboarding = false
                    settingsManager.setOnboardingCompleted("fisherman", true)
                }
            }
        }
    }

    // Modal Bottom Sheet settings
    if (showSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false },
            containerColor = DarkCardSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Demo settings & controls", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                
                // Offline Mode Toggle
                val offlineVal by viewModel.isOffline.collectAsStateWithLifecycle()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Simulate Offline Mode", color = Color.White)
                    Switch(
                        checked = offlineVal,
                        onCheckedChange = { viewModel.setOfflineMode(it) }
                    )
                }
                
                // Dark Mode Toggle
                val darkVal by viewModel.isDarkTheme.collectAsStateWithLifecycle()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dark Mode Theme", color = Color.White)
                    Switch(
                        checked = darkVal,
                        onCheckedChange = { viewModel.setDarkTheme(it) }
                    )
                }

                Button(
                    onClick = { showSettingsSheet = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CLOSE", color = Color.Black)
                }
            }
        }
    }
}

// ==========================================
// 1. MAP TAB
// ==========================================
@Composable
fun FishermanMapTab(viewModel: FishermanViewModel, snackbarHostState: SnackbarHostState) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isTripActive by viewModel.isTripActive.collectAsStateWithLifecycle()
    val currentPosition by viewModel.currentPosition.collectAsStateWithLifecycle()
    val distanceToBorder by viewModel.distanceToBorder.collectAsStateWithLifecycle()
    val safetyStatus by viewModel.safetyStatus.collectAsStateWithLifecycle()

    // Voice alert TTS toggler and repeats
    var voiceAlertsEnabled by remember { mutableStateOf(true) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    LaunchedEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("ta", "IN")
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            tts?.shutdown()
        }
    }

    // TTS warning repeating loop every 30s
    LaunchedEffect(safetyStatus, voiceAlertsEnabled) {
        if (voiceAlertsEnabled && (safetyStatus == "CAUTION" || safetyStatus == "DANGER")) {
            while (true) {
                tts?.let { t ->
                    t.language = Locale("ta", "IN")
                    t.speak("எச்சரிக்கை! கடல் எல்லை அணுகுகிறது. திரும்பவும்.", TextToSpeech.QUEUE_FLUSH, null, "tamil_alert")
                    delay(4500)
                    t.language = Locale.US
                    t.speak("Warning! Approaching maritime border. Please turn back.", TextToSpeech.QUEUE_ADD, null, "english_alert")
                }
                delay(30000)
            }
        }
    }

    // Safe return route calculations
    val distanceToShore = GeofenceCalculator.haversineDistance(
        currentPosition.first, currentPosition.second, 13.0827, 80.2707
    )
    val etaMinutes = (distanceToShore / 23.15 * 60).toInt() // 23.15 km/h is 12.5 kn speed
    val etaHours = etaMinutes / 60
    val etaMins = etaMinutes % 60
    val etaString = if (etaHours > 0) "${etaHours}h ${etaMins}m" else "${etaMins}m"

    Box(modifier = Modifier.fillMaxSize()) {
        // Leaflet.js WebView Map with Safe Return Route dotted line
        CustomFallbackMap(
            modifier = Modifier.fillMaxSize(),
            boatLocations = listOf(Pair("Raja Kumar", currentPosition)),
            boatStatuses = mapOf("Raja Kumar" to safetyStatus),
            showSafeReturnRoute = isTripActive,
            centerLocation = currentPosition
        )

        // Float info card (Top Center)
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
                .fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(containerColor = DarkCardSurface.copy(alpha = 0.9f)),
            border = BorderStroke(1.dp, BorderBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Safety status:", color = TextGrey, fontSize = 11.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val statusColor = when (safetyStatus) {
                            "DANGER" -> ErrorRed
                            "CAUTION" -> WarningAmber
                            else -> SafeGreen
                        }
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(statusColor, CircleShape)
                        )
                        Text(
                            text = " $safetyStatus",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("Distance to IMBL:", color = TextGrey, fontSize = 11.sp)
                    Text(
                        text = "${String.format("%.1f", distanceToBorder)} km",
                        color = PrimaryTeal,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Compass Overlay (Top Right below stats card)
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 90.dp, end = 16.dp),
            horizontalAlignment = Alignment.End
        ) {
            CompassWidget()
        }

        // Return ETA Card and Navigation Button on Map
        if (isTripActive) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface.copy(alpha = 0.85f)),
                border = BorderStroke(1.dp, BorderBlue),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(16.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Return ETA", color = TextGrey, fontSize = 10.sp)
                    Text("~$etaString", color = SafeGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    if (distanceToShore > 10.0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = {
                                viewModel.addNotification("Navigation Started", "Course coordinates set southwest back to Chennai port.")
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Navigating Home. Course set to 225° SW. ETA: $etaString")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SafeGreen),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(30.dp),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("Navigate Home", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Speaker Toggle Button & SOS center FAB at bottom
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 12.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Speaker Voice toggle
            FloatingActionButton(
                onClick = { voiceAlertsEnabled = !voiceAlertsEnabled },
                containerColor = BorderBlue,
                contentColor = if (voiceAlertsEnabled) PrimaryTeal else TextGrey,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (voiceAlertsEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                    contentDescription = "Voice Alerts Toggle"
                )
            }

            // SOS Center Pulse FAB
            Button(
                onClick = { viewModel.triggerSOS() },
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                shape = CircleShape,
                modifier = Modifier.size(75.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("SOS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
            }

            // Start/Stop Trip FAB (Right Bottom)
            FloatingActionButton(
                onClick = {
                    if (isTripActive) viewModel.stopTrip() else viewModel.startTrip()
                },
                containerColor = if (isTripActive) ErrorRed else PrimaryTeal,
                contentColor = Color.White,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (isTripActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                    contentDescription = if (isTripActive) "Stop Trip" else "Start Trip"
                )
            }
        }
    }
}

// Compass Rose widget
@Composable
fun CompassWidget() {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCardSurface.copy(alpha = 0.85f)),
        border = BorderStroke(1.dp, BorderBlue),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(90.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Canvas(modifier = Modifier.size(36.dp)) {
                drawCircle(color = BorderBlue, style = Stroke(2.dp.toPx()))
                // Drawing Compass needle pointing NE (045)
                drawLine(
                    color = ErrorRed,
                    start = center,
                    end = Offset(center.x + 12.dp.toPx(), center.y - 12.dp.toPx()),
                    strokeWidth = 2.5.dp.toPx()
                )
                drawLine(
                    color = TextGrey,
                    start = center,
                    end = Offset(center.x - 12.dp.toPx(), center.y + 12.dp.toPx()),
                    strokeWidth = 2.dp.toPx()
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("045° NE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text("12.5 kn", color = PrimaryTeal, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            Text("Depth: 42m", color = TextGrey, fontSize = 9.sp)
        }
    }
}

// ==========================================
// 2. ALERTS TAB
// ==========================================
@Composable
fun FishermanAlertsTab(viewModel: FishermanViewModel) {
    val alerts by viewModel.alertsList.collectAsStateWithLifecycle()
    var selectedAlertForSheet by remember { mutableStateOf<AlertEvent?>(null) }

    if (selectedAlertForSheet != null) {
        AlertDialog(
            onDismissRequest = { selectedAlertForSheet = null },
            title = { Text(selectedAlertForSheet?.alertType?.replace("_", " ") ?: "Alert Details") },
            text = {
                Column {
                    Text("Message: ${selectedAlertForSheet?.message}", color = TextWhite)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Location: Lat ${selectedAlertForSheet?.latitude}, Lon ${selectedAlertForSheet?.longitude}", color = TextGrey)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Status: ${selectedAlertForSheet?.status}", color = PrimaryTeal, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedAlertForSheet = null }) {
                    Text("DISMISS", color = PrimaryTeal)
                }
            },
            containerColor = DarkCardSurface,
            titleContentColor = Color.White
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Alerts History", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        if (alerts.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No alert logs found", color = TextGrey)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(alerts) { alert ->
                    AlertItemCard(alert = alert) {
                        selectedAlertForSheet = alert
                    }
                }
            }
        }
    }
}

@Composable
fun AlertItemCard(alert: AlertEvent, onClick: () -> Unit) {
    val indicatorColor = when (alert.alertType) {
        "BORDER_CRITICAL" -> ErrorRed
        "BORDER_CAUTION" -> WarningAmber
        "SOS" -> ErrorRed
        "CYCLONE" -> CyclonePurple
        else -> PrimaryTeal
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        border = BorderStroke(1.dp, BorderBlue),
        colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .background(indicatorColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val icon = when (alert.alertType) {
                    "BORDER_CRITICAL" -> Icons.Default.CrisisAlert
                    "BORDER_CAUTION" -> Icons.Default.Warning
                    "SOS" -> Icons.Default.Warning
                    "CYCLONE" -> Icons.Default.Cyclone
                    else -> Icons.Default.Notifications
                }
                Icon(imageVector = icon, contentDescription = null, tint = indicatorColor)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alert.alertType.replace("_", " "),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.ENGLISH).format(Date(alert.timestamp)),
                    color = TextGrey,
                    fontSize = 11.sp
                )
                Text(
                    text = "Lat: ${String.format("%.3f", alert.latitude)}, Lon: ${String.format("%.3f", alert.longitude)}",
                    color = TextGrey,
                    fontSize = 11.sp
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (alert.status == "ACTIVE") ErrorRed.copy(alpha = 0.2f) else BorderBlue)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = alert.status,
                    color = if (alert.status == "ACTIVE") ErrorRed else TextGrey,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ==========================================
// 3. WEATHER TAB
// ==========================================
@Composable
fun FishermanWeatherTab(viewModel: FishermanViewModel) {
    val forecast by viewModel.weatherForecast.collectAsStateWithLifecycle()
    val nowForecast = forecast.firstOrNull() ?: WeatherData(
        latitude = 13.0827, longitude = 80.2707, windSpeed = 22.0, waveHeight = 1.2,
        zone = "SAFE", timestamp = System.currentTimeMillis(), advisory = "Safe conditions for sailing"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Weather & Advisories", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)

        // Current weather card
        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, PrimaryTeal),
            colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Chennai Coast", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("13.08°N 80.27°E", color = TextGrey, fontSize = 12.sp)
                    }
                    val statusColor = when (nowForecast.zone) {
                        "DANGER" -> ErrorRed
                        "CAUTION" -> WarningAmber
                        else -> SafeGreen
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(statusColor)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = nowForecast.zone,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeatherStatItem("Wind Speed", "${nowForecast.windSpeed} km/h", Icons.Default.Air)
                    WeatherStatItem("Wave Height", "${nowForecast.waveHeight} m", Icons.Default.Water)
                    WeatherStatItem("Visibility", "8 km", Icons.Default.Visibility)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = BorderBlue, thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Last updated: ${SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(nowForecast.timestamp))}",
                    color = TextGrey,
                    fontSize = 11.sp
                )
            }
        }

        // Wind Rose Widget & Golden Hour
        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, BorderBlue),
            colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Wind Direction Rose", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Beaufort: Force 4 — Moderate Breeze", color = TextGrey, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("🌅 Sunrise: 05:47 AM", color = WarningAmber, fontSize = 12.sp)
                    Text("🌇 Sunset: 06:32 PM", color = CyclonePurple, fontSize = 12.sp)
                    Text("🎣 Golden Hour: Excellent (17:30 - 18:30)", color = PrimaryTeal, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                
                // Wind Rose Canvas Diagram
                Canvas(modifier = Modifier.size(60.dp)) {
                    drawCircle(color = BorderBlue, style = Stroke(2.dp.toPx()))
                    // Wind Compass axes
                    drawLine(color = BorderBlue.copy(alpha = 0.4f), start = Offset(center.x, 5f), end = Offset(center.x, size.height - 5f))
                    drawLine(color = BorderBlue.copy(alpha = 0.4f), start = Offset(5f, center.y), end = Offset(size.width - 5f, center.y))
                    // Direction Needle pointing NE (045)
                    drawLine(
                        color = PrimaryTeal,
                        start = center,
                        end = Offset(center.x + 22.dp.toPx(), center.y - 22.dp.toPx()),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        // Fishing Condition Score Circular Progress
        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, BorderBlue),
            colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Fishing Conditions Analytics", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circular Progress Gauge
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(70.dp)) {
                        CircularProgressIndicator(
                            progress = 0.72f,
                            color = SafeGreen,
                            trackColor = BorderBlue,
                            strokeWidth = 6.dp,
                            modifier = Modifier.fillMaxSize()
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("72", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                            Text("GOOD", color = SafeGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Breakdown Scores
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        ScoreBarItem("Wind Activity", 85, SafeGreen)
                        ScoreBarItem("Wave Wavelets", 78, SafeGreen)
                        ScoreBarItem("Sea Visibility", 90, SafeGreen)
                        ScoreBarItem("Water Temp", 65, WarningAmber)
                    }
                }
            }
        }

        // 48 hour forecast row
        Text("48-Hour Forecast", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(forecast.drop(1)) { weather ->
                ForecastItemCard(weather)
            }
        }

        // IMD Chennai Advisory
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.15f)),
            border = BorderStroke(1.dp, ErrorRed),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Active Advisory: Fishermen are advised not to venture into the sea along the Tamil Nadu coast for the next 24 hours.",
                    color = ErrorRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text("Source: IMD Chennai", color = TextGrey, fontSize = 11.sp)
            }
        }

        // Simulate cyclone button
        Button(
            onClick = { viewModel.simulateCyclone() },
            border = BorderStroke(1.dp, CyclonePurple),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Simulate Cyclone Alert", color = CyclonePurple, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ScoreBarItem(label: String, score: Int, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.width(180.dp)
    ) {
        Text(label, color = TextGrey, fontSize = 10.sp, modifier = Modifier.weight(1f))
        LinearProgressIndicator(
            progress = score / 100f,
            color = color,
            trackColor = BorderBlue,
            modifier = Modifier.width(70.dp).height(4.dp)
        )
        Text("$score/100", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
    }
}

// ==========================================
// 4. TRIP LOG TAB
// ==========================================
@Composable
fun FishermanTripLogTab(viewModel: FishermanViewModel, snackbarHostState: SnackbarHostState) {
    val isTripActive by viewModel.isTripActive.collectAsStateWithLifecycle()
    val activeTrip by viewModel.activeTrip.collectAsStateWithLifecycle()
    val pastTrips by viewModel.pastTrips.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    var routeViewTrip by remember { mutableStateOf<TripSession?>(null) }

    if (routeViewTrip != null) {
        AlertDialog(
            onDismissRequest = { routeViewTrip = null },
            title = { Text("Route Preview - Trip #${routeViewTrip?.id}") },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    CustomFallbackMap(
                        modifier = Modifier.fillMaxSize(),
                        boatLocations = listOf(Pair("Mock Route", Pair(13.15, 80.45)))
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { routeViewTrip = null }) {
                    Text("CLOSE", color = PrimaryTeal)
                }
            },
            containerColor = DarkCardSurface,
            titleContentColor = Color.White
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Voyage Logbook & Stats", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        // Lifetime stats card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, BorderBlue),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Lifetime Stats", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatBox("Total Trips", "47", PrimaryTeal)
                        StatBox("Sea Hours", "312h", PrimaryTeal)
                        StatBox("Distance", "1,847km", PrimaryTeal)
                        StatBox("Alerts", "23", WarningAmber)
                    }
                }
            }
        }

        // Monthly voyages Line Chart (Canvas Drawing)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, BorderBlue),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Monthly Voyage Frequency", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                        val points = listOf(
                            Offset(10f, 80f),
                            Offset(60f, 60f),
                            Offset(110f, 75f),
                            Offset(160f, 30f),
                            Offset(210f, 15f),
                            Offset(260f, 90f)
                        )
                        // Draw grid lines
                        drawLine(color = BorderBlue.copy(alpha = 0.3f), start = Offset(0f, 50f), end = Offset(size.width, 50f))
                        
                        // Draw path line
                        val path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(points[0].x * (size.width / 270f), points[0].y)
                            for (i in 1 until points.size) {
                                lineTo(points[i].x * (size.width / 270f), points[i].y)
                            }
                        }
                        drawPath(path = path, color = PrimaryTeal, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                        
                        // Draw dot values
                        points.forEach { pt ->
                            drawCircle(color = Color.White, radius = 4.dp.toPx(), center = Offset(pt.x * (size.width / 270f), pt.y))
                            drawCircle(color = PrimaryTeal, radius = 2.dp.toPx(), center = Offset(pt.x * (size.width / 270f), pt.y))
                        }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val labels = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
                        labels.forEach { label ->
                            Text(label, color = TextGrey, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Active Trip Card
        if (isTripActive && activeTrip != null) {
            item {
                ActiveTripCardContent(activeTrip!!) {
                    viewModel.stopTrip()
                }
            }
        }

        item {
            Text("Past Voyages", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        if (pastTrips.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    Text("No past voyages recorded", color = TextGrey)
                }
            }
        } else {
            items(pastTrips) { trip ->
                // Custom Trip Card with Export/Share buttons
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, BorderBlue),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Voyage #${trip.id}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(Date(trip.startTime)), color = TextGrey, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("Distance: ${trip.distanceCovered} km", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("Alerts count: ${trip.alertCount}", color = if (trip.alertCount > 0) WarningAmber else SafeGreen, fontSize = 12.sp)
                            }
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                // Export GPX Button
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Trip #${trip.id} exported to Downloads/FisherFence_Trip${trip.id}.gpx")
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BorderBlue),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    modifier = Modifier.height(32.dp),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Export GPX", color = Color.White, fontSize = 11.sp)
                                }

                                // Share Icon Button
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Sharing Trip #${trip.id} summary sheet... [Mock]")
                                        }
                                    },
                                    modifier = Modifier.size(32.dp).background(BorderBlue, RoundedCornerShape(6.dp))
                                ) {
                                    Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = PrimaryTeal, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(label: String, value: String, tint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = tint, fontWeight = FontWeight.Black, fontSize = 16.sp)
        Text(label, color = TextGrey, fontSize = 9.sp)
    }
}

// ==========================================
// 5. MORE TAB (GRID & SUB-SCREENS)
// ==========================================
@Composable
fun FishermanMoreTab(
    viewModel: FishermanViewModel,
    onNavigateToMap: () -> Unit,
    snackbarHost: SnackbarHostState
) {
    var activeSubScreen by remember { mutableStateOf<String?>(null) }

    when (activeSubScreen) {
        "checklist" -> SafetyChecklistScreen(viewModel, { activeSubScreen = null }, onNavigateToMap)
        "heatmap" -> FishingHeatmapScreen(viewModel) { activeSubScreen = null }
        "contacts" -> EmergencyContactsScreen(snackbarHost) { activeSubScreen = null }
        "tides" -> TideSeaConditionsScreen { activeSubScreen = null }
        else -> {
            // More Tab Grid Menu
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text("More Utilities & Tools", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MoreGridCard("Pre-Departure Checklist", Icons.Default.Checklist, "Verify items before setting sail", Modifier.weight(1f)) {
                        activeSubScreen = "checklist"
                    }
                    MoreGridCard("Fishing Hotspots Map", Icons.Default.HeatPump, "Heatmaps of vessel fleet statistics", Modifier.weight(1f)) {
                        activeSubScreen = "heatmap"
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MoreGridCard("Emergency Quick Dial", Icons.Default.Call, "Quick action dialing with direct help", Modifier.weight(1f)) {
                        activeSubScreen = "contacts"
                    }
                    MoreGridCard("Tide & Sea Conditions", Icons.Default.Water, "Sea states, chlorophyll, and windows", Modifier.weight(1f)) {
                        activeSubScreen = "tides"
                    }
                }
            }
        }
    }
}

@Composable
fun MoreGridCard(title: String, icon: ImageVector, desc: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(140.dp)
            .clickable { onClick() },
        border = BorderStroke(1.dp, BorderBlue),
        colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(32.dp))
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(desc, color = TextGrey, fontSize = 10.sp, maxLines = 2)
            }
        }
    }
}

// 5-A: Pre-Departure Safety Checklist Sub-screen
@Composable
fun SafetyChecklistScreen(
    viewModel: FishermanViewModel,
    onBack: () -> Unit,
    onNavigateToMap: () -> Unit
) {
    val checklistItems by viewModel.checklistItems.collectAsStateWithLifecycle()
    val checkedCount = checklistItems.count { it.isChecked }
    val isComplete = checkedCount == 12

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text("Pre-Departure Safety Checklist", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))
        
        // Progress Bar
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
            border = BorderStroke(1.dp, BorderBlue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Completion Progress", color = TextGrey, fontSize = 12.sp)
                    Text("$checkedCount / 12 Checked", color = PrimaryTeal, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = checkedCount / 12f,
                    color = PrimaryTeal,
                    trackColor = BorderBlue,
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Check All / Clear All Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { viewModel.checkAllChecklistItems() },
                colors = ButtonDefaults.buttonColors(containerColor = BorderBlue),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Check All", color = Color.White)
            }
            Button(
                onClick = { viewModel.clearAllChecklistItems() },
                colors = ButtonDefaults.buttonColors(containerColor = BorderBlue),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Clear All", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Checklist Checklist Items Scroll
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(checklistItems) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { viewModel.updateChecklistItem(item.id, !item.isChecked) },
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    border = BorderStroke(1.dp, BorderBlue)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = item.isChecked,
                            onCheckedChange = { viewModel.updateChecklistItem(item.id, it) },
                            colors = CheckboxDefaults.colors(checkedColor = PrimaryTeal)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(item.title, color = Color.White, fontSize = 13.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // START TRIP Action button
        Button(
            onClick = {
                viewModel.startTrip()
                onNavigateToMap()
                onBack()
            },
            enabled = isComplete,
            colors = ButtonDefaults.buttonColors(
                containerColor = SafeGreen,
                disabledContainerColor = BorderBlue
            ),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = if (isComplete) "START TRIP" else "Complete checklist to start trip",
                color = if (isComplete) Color.White else TextGrey,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// 5-B: Fishing Zone Heatmap Screen
@Composable
fun FishingHeatmapScreen(
    viewModel: FishermanViewModel,
    onBack: () -> Unit
) {
    var showIMBL by remember { mutableStateOf(true) }
    var avoidZone by remember { mutableStateOf(false) }

    val hotspotPoints = listOf(
        Pair(Pair(13.12, 80.38), "high"), Pair(Pair(13.18, 80.44), "moderate"), Pair(Pair(13.05, 80.51), "light"),
        Pair(Pair(12.95, 80.42), "moderate"), Pair(Pair(13.25, 80.35), "high"), Pair(Pair(13.31, 80.47), "light"),
        Pair(Pair(12.88, 80.55), "moderate"), Pair(Pair(13.08, 80.29), "high"), Pair(Pair(13.22, 80.61), "light"),
        Pair(Pair(12.98, 80.33), "moderate"), Pair(Pair(13.15, 80.58), "light"), Pair(Pair(13.02, 80.46), "moderate")
    )

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text("Fishing Activity Heatmap", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        // Map Viewport
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            CustomFallbackMap(
                modifier = Modifier.fillMaxSize(),
                showHeatmap = true,
                heatmapPoints = hotspotPoints,
                showIMBL = showIMBL,
                showAvoidZone = avoidZone
            )

            // Floating map filters
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface.copy(alpha = 0.85f)),
                border = BorderStroke(1.dp, BorderBlue),
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = showIMBL, onCheckedChange = { showIMBL = it }, colors = CheckboxDefaults.colors(checkedColor = PrimaryTeal))
                        Text("Show IMBL Boundary", color = Color.White, fontSize = 11.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = avoidZone, onCheckedChange = { avoidZone = it }, colors = CheckboxDefaults.colors(checkedColor = PrimaryTeal))
                        Text("Avoid Crowded Areas", color = Color.White, fontSize = 11.sp)
                    }
                }
            }
        }

        // Bottom Info stats
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
            border = BorderStroke(1.dp, BorderBlue),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Vessel Heatmap Status", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Based on last 30 days of fleet data | 127 active vessels in zone", color = TextGrey, fontSize = 11.sp)
            }
        }
    }
}

// 5-C: Emergency Dial Screen
@Composable
fun EmergencyContactsScreen(
    snackbarHost: SnackbarHostState,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    
    val contacts = listOf(
        Triple("🚨 Coast Guard Emergency", "1554", ErrorRed),
        Triple("🏥 Marine Emergency Services", "1800-180-1991", ErrorRed),
        Triple("📡 INCOIS Wave Alert Helpline", "040-2388-4220", WarningAmber),
        Triple("👨👩👧 My Family Contact", "+91 98765 43210", PrimaryTeal),
        Triple("🐟 Fisheries Department", "044-2345-6789", SecondaryBlue)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text("Emergency Quick Dial", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Call Buttons
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            contacts.forEach { (name, num, color) ->
                Button(
                    onClick = {
                        coroutineScope.launch {
                            snackbarHost.showSnackbar("Calling $name... ($num) [Mock]")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = color),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(name, color = if (color == WarningAmber) Color.Black else Color.White, fontWeight = FontWeight.Bold)
                        Text(num, color = if (color == WarningAmber) Color.Black else Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Safety Instructions Guide
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
            border = BorderStroke(1.dp, BorderBlue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("IN CASE OF EMERGENCY AT SEA:", color = ErrorRed, fontWeight = FontWeight.Black, fontSize = 14.sp)
                BulletRow("1. Press SOS button immediately to broadcast location.")
                BulletRow("2. Stay calm and stay with the boat — do not attempt swimming.")
                BulletRow("3. Use flares if rescue team or search vessels are visible.")
                BulletRow("4. Conserve phone battery — only call if essential.")
            }
        }
    }
}

@Composable
fun BulletRow(text: String) {
    Text(text, color = TextGrey, fontSize = 12.sp, modifier = Modifier.fillMaxWidth())
}

// 5-D: Tide & Sea Conditions Screen
@Composable
fun TideSeaConditionsScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text("Tide & Sea Conditions", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        // Tide curve sine-wave chart
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
            border = BorderStroke(1.dp, BorderBlue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Tide Height Wave Chart", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                
                // Sine wave Canvas drawing
                Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                    val points = mutableListOf<Offset>()
                    for (x in 0..size.width.toInt() step 5) {
                        // draw sine wave formula
                        val y = (sin(x / 40.0) * 30.0 + 50.0).toFloat()
                        points.add(Offset(x.toFloat(), y))
                    }
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(points[0].x, points[0].y)
                        for (i in 1 until points.size) {
                            lineTo(points[i].x, points[i].y)
                        }
                    }
                    drawPath(path = path, color = PrimaryTeal, style = Stroke(width = 2.dp.toPx()))
                    
                    // NOW indicator line
                    val nowX = size.width * 0.45f
                    val nowY = (sin(nowX / 40.0) * 30.0 + 50.0).toFloat()
                    drawLine(
                        color = ErrorRed,
                        start = Offset(nowX, 10f),
                        end = Offset(nowX, size.height - 10f),
                        strokeWidth = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                    )
                    drawCircle(color = ErrorRed, radius = 5.dp.toPx(), center = Offset(nowX, nowY))
                }
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("03:42 AM (1.8m ↑)", color = TextGrey, fontSize = 9.sp)
                    Text("NOW — Rising tide (1.24m)", color = PrimaryTeal, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    Text("10:32 PM (0.28m ↓)", color = TextGrey, fontSize = 9.sp)
                }
            }
        }

        // Tide Table
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
            border = BorderStroke(1.dp, BorderBlue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Today's Tide Table", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                TideRow("High Tide", "03:42 AM", "1.82 m", true)
                TideRow("Low Tide", "09:55 AM", "0.31 m", false)
                TideRow("High Tide", "04:18 PM", "1.76 m", true)
                TideRow("Low Tide", "10:32 PM", "0.28 m", false)
            }
        }

        // Sea State Details Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
            border = BorderStroke(1.dp, BorderBlue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Sea State Analytics", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Swell Height:", color = TextGrey, fontSize = 12.sp)
                    Text("1.4 m", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Swell Period:", color = TextGrey, fontSize = 12.sp)
                    Text("8 seconds", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Swell Direction:", color = TextGrey, fontSize = 12.sp)
                    Text("SSE (160°)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Water Temp / Salinity:", color = TextGrey, fontSize = 12.sp)
                    Text("29.2°C / 34.5 ppt", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Fishing quality indicators
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
            border = BorderStroke(1.dp, BorderBlue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Fishing Quality Indicators", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Chlorophyll density:", color = TextGrey, fontSize = 12.sp)
                    Text("1.2 mg/m³ (High)", color = SafeGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Best Depth window:", color = TextGrey, fontSize = 12.sp)
                    Text("15 - 25 m", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Recommended window:", color = TextGrey, fontSize = 12.sp)
                    Text("11:00 AM – 3:00 PM", color = PrimaryTeal, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun TideRow(label: String, time: String, height: String, isHigh: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(if (isHigh) "↑ " else "↓ ", color = if (isHigh) SafeGreen else ErrorRed)
            Text(label, color = Color.White, fontSize = 12.sp)
        }
        Text(time, color = TextGrey, fontSize = 12.sp)
        Text(height, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
fun WeatherStatItem(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(label, color = TextGrey, fontSize = 10.sp)
    }
}

@Composable
fun ForecastItemCard(weather: WeatherData) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
        border = BorderStroke(1.dp, BorderBlue),
        modifier = Modifier.width(110.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(weather.timestamp)),
                color = TextGrey,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Icon(
                imageVector = if (weather.zone == "SAFE") Icons.Default.Cloud else Icons.Default.Warning,
                contentDescription = null,
                tint = if (weather.zone == "SAFE") PrimaryTeal else WarningAmber,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text("${weather.windSpeed} km/h", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text("${weather.waveHeight} m", color = TextGrey, fontSize = 10.sp)
        }
    }
}

@Composable
fun ActiveTripCardContent(trip: TripSession, onStopTrip: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, ErrorRed),
        colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Vessel Voyage Active", color = ErrorRed, fontWeight = FontWeight.Black, fontSize = 14.sp)
                Text("Trip #${trip.id}", color = TextGrey, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Duration", color = TextGrey, fontSize = 10.sp)
                    Text("4h 32m", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Column {
                    Text("Distance Covered", color = TextGrey, fontSize = 10.sp)
                    Text("${trip.distanceCovered} km", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Column {
                    Text("Alerts Tripped", color = TextGrey, fontSize = 10.sp)
                    Text("${trip.alertCount}", color = if (trip.alertCount > 0) WarningAmber else SafeGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onStopTrip,
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("STOP VOYAGE", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
