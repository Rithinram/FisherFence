@file:OptIn(ExperimentalMaterial3Api::class)
package com.fisherfence.maritime.presentation.family

import android.media.RingtoneManager
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fisherfence.maritime.data.local.AlertEvent
import com.fisherfence.maritime.data.local.SystemNotification
import com.fisherfence.maritime.data.local.TripSession
import com.fisherfence.maritime.presentation.common.CustomFallbackMap
import com.fisherfence.maritime.presentation.common.SosTakeoverScreen
import com.fisherfence.maritime.presentation.common.OnboardingCarousel
import com.fisherfence.maritime.presentation.common.NotificationDrawer
import com.fisherfence.maritime.presentation.fisherman.AlertItemCard
import com.fisherfence.maritime.presentation.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyHomeScreen(
    viewModel: FamilyViewModel,
    onLogout: () -> Unit
) {
    val activeAlert by viewModel.activeAlert.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Global states
    var selectedTab by remember { mutableIntStateOf(0) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    // Onboarding walkthrough
    val settingsManager = viewModel.settingsManager
    var showOnboarding by remember { mutableStateOf(!settingsManager.isOnboardingCompleted("family")) }

    // Settings & Notifications count
    val isOffline by viewModel.isOffline.collectAsStateWithLifecycle()
    val unreadNotifications by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()
    val notificationsList by viewModel.notifications.collectAsStateWithLifecycle()

    if (activeAlert != null && activeAlert?.alertType == "SOS" && activeAlert?.status == "ACTIVE") {
        SosTakeoverScreen(
            alert = activeAlert!!,
            role = "family",
            onDismiss = { viewModel.dismissAlert() },
            onResolve = { viewModel.dismissAlert() },
            snackbarHostState = snackbarHostState
        )
    } else {
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
                                label = { Text("FAMILY", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp) },
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
                        // Notifications drawer bell icon
                        IconButton(onClick = { viewModel.markNotificationsAsRead() }) {
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
                        // Demo Settings Settings
                        IconButton(onClick = { showSettingsSheet = true }) {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = "Demo Settings", tint = TextGrey)
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
                        Triple(0, Icons.Default.Room, "Track"),
                        Triple(1, Icons.Default.Notifications, "Alerts"),
                        Triple(2, Icons.Default.Person, "Profile"),
                        Triple(3, Icons.Default.GridOn, "Support")
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
                    // Offline indicator banner
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

                    // Tabs router
                    Box(modifier = Modifier.weight(1f)) {
                        when (selectedTab) {
                            0 -> FamilyTrackTab(viewModel, snackbarHostState)
                            1 -> FamilyAlertsTab(viewModel, snackbarHostState)
                            2 -> FamilyProfileTab(viewModel, onLogout, snackbarHostState)
                            3 -> FamilySupportTab(viewModel, snackbarHostState)
                        }
                    }
                }

                // Notifications Drawer Dropdown
                NotificationDrawer(
                    isOpen = unreadNotifications > 100, // Dummy
                    notifications = notificationsList,
                    onMarkAllAsRead = { viewModel.markNotificationsAsRead() },
                    onClose = {}
                )

                // Onboarding walkthrough
                if (showOnboarding) {
                    OnboardingCarousel(role = "family") {
                        showOnboarding = false
                        settingsManager.setOnboardingCompleted("family", true)
                    }
                }
            }
        }
    }

    // Settings sheet
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
                
                val offlineVal by viewModel.isOffline.collectAsStateWithLifecycle()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Simulate Offline Mode", color = Color.White)
                    Switch(checked = offlineVal, onCheckedChange = { viewModel.setOfflineMode(it) })
                }
                
                val darkVal by viewModel.isDarkTheme.collectAsStateWithLifecycle()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dark Mode Theme", color = Color.White)
                    Switch(checked = darkVal, onCheckedChange = { viewModel.setDarkTheme(it) })
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
// 1. TRACK TAB
// ==========================================
@Composable
fun FamilyTrackTab(viewModel: FamilyViewModel, snackbarHost: SnackbarHostState) {
    val isTripActive by viewModel.isTripActive.collectAsStateWithLifecycle()
    val currentPosition by viewModel.currentPosition.collectAsStateWithLifecycle()
    val safetyStatus by viewModel.safetyStatus.collectAsStateWithLifecycle()

    // 15-minute weather updater mock
    var weatherTick by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000 * 3) // simulate updates every 3 seconds for demo
            weatherTick++
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Fisherman status header card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
            border = BorderStroke(1.dp, BorderBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(BorderBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("RK", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Raja Kumar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Boat ID: TN-04-MM-1234", color = TextGrey, fontSize = 11.sp)
                        }
                    }
                    val statusColor = when (safetyStatus) {
                        "DANGER" -> ErrorRed
                        "CAUTION" -> WarningAmber
                        else -> SafeGreen
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(statusColor.copy(0.2f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(safetyStatus, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = BorderBlue, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(12.dp))

                // Overdue countdown card logic
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Distance Shore", color = TextGrey, fontSize = 10.sp)
                        Text("18.3 km", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Trip Duration", color = TextGrey, fontSize = 10.sp)
                        Text("4h 32m", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("Updated: 2 minutes ago", color = TextGrey, fontSize = 10.sp)
            }
        }

        // ETA & Overdue Tracker countdown
        if (isTripActive) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                border = BorderStroke(1.dp, BorderBlue),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Expected Return Analytics", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Expected return time:", color = TextGrey, fontSize = 12.sp)
                        Text("05:30 PM (Today)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Trip Completion Progress:", color = TextGrey, fontSize = 11.sp)
                        Text("65%", color = PrimaryTeal, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    LinearProgressIndicator(
                        progress = 0.65f,
                        color = PrimaryTeal,
                        trackColor = BorderBlue,
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp))
                    )
                    
                    // Overdue alert warnings logic
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(WarningAmber.copy(0.15f))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "⚠ OVERDUE — Expected return was 05:30 PM. 23 minutes overdue.",
                            color = WarningAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Leaflet WebView Map (Centered on Raja Kumar)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, BorderBlue, RoundedCornerShape(12.dp))
        ) {
            CustomFallbackMap(
                modifier = Modifier.fillMaxSize(),
                boatLocations = listOf(Pair("Raja Kumar", currentPosition)),
                boatStatuses = mapOf("Raja Kumar" to safetyStatus),
                showFamilyTrackerLine = true,
                familyLocation = Pair(13.0827, 80.2707),
                centerLocation = currentPosition
            )
        }

        // Weather at Raja's location widget
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
            border = BorderStroke(1.dp, BorderBlue),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Weather at Raja's Location", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Wind: 22 km/h | Waves: 1.2 m", color = Color.White, fontSize = 12.sp)
                        Text("Temperature: 28°C | Outlook: Good conditions", color = TextGrey, fontSize = 11.sp)
                    }
                    Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(SafeGreen).padding(horizontal = 6.dp, vertical = 2.dp)) {
                        Text("SAFE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Stepper Journey Timeline Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
            border = BorderStroke(1.dp, BorderBlue),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Voyage Step Timeline", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                
                TimelineStepItem("✅ Departed shore", "05:30 AM", true)
                TimelineStepItem("✅ Reached fishing zone", "07:15 AM", true)
                TimelineStepItem("⚠ Border caution alert", "09:12 AM (Resolved)", false, true)
                TimelineStepItem("🔄 Currently fishing", "Ongoing (4h 32m)", false)
                TimelineStepItem("⏳ Expected return", "05:30 PM", false)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun TimelineStepItem(label: String, time: String, complete: Boolean, isWarn: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(if (isWarn) WarningAmber else if (complete) SafeGreen else Color.Gray, CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(label, color = if (isWarn) WarningAmber else Color.White, fontSize = 12.sp, fontWeight = if (complete) FontWeight.Bold else FontWeight.Normal)
        }
        Text(time, color = TextGrey, fontSize = 11.sp)
    }
}

// ==========================================
// 2. ALERTS TAB (WITH PREFERENCES & DETAIL MAPS)
// ==========================================
@Composable
fun FamilyAlertsTab(viewModel: FamilyViewModel, snackbarHostState: SnackbarHostState) {
    val alertsList by viewModel.rajaAlerts.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    var showPrefSettings by remember { mutableStateOf(false) }
    var selectedAlertForScreen by remember { mutableStateOf<AlertEvent?>(null) }

    // Alert Detail Screen Overlay
    if (selectedAlertForScreen != null) {
        val alert = selectedAlertForScreen!!
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkNavyBackground)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { selectedAlertForScreen = null }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text("Alert Detail Specifications", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Card(colors = CardDefaults.cardColors(containerColor = DarkCardSurface), border = BorderStroke(1.dp, BorderBlue)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(alert.alertType.replace("_", " "), color = ErrorRed, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Text("Timestamp: " + SimpleDateFormat("dd MMM hh:mm:ss a", Locale.ENGLISH).format(Date(alert.timestamp)), color = TextGrey, fontSize = 12.sp)
                        Text(alert.message, color = Color.White, fontSize = 13.sp)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Resolution Status:", color = TextGrey)
                            Text(alert.status, color = if (alert.status == "ACTIVE") ErrorRed else SafeGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Map showing exact alert location
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, BorderBlue, RoundedCornerShape(12.dp))
                ) {
                    CustomFallbackMap(
                        modifier = Modifier.fillMaxSize(),
                        boatLocations = listOf(Pair("Raja Kumar", Pair(alert.latitude, alert.longitude))),
                        boatStatuses = mapOf("Raja Kumar" to alert.alertType),
                        centerLocation = Pair(alert.latitude, alert.longitude),
                        zoomLevel = 13
                    )
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Vessel Incident Feeds", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                IconButton(
                    onClick = { showPrefSettings = true },
                    modifier = Modifier.size(36.dp).background(BorderBlue, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Preferences", tint = PrimaryTeal)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (alertsList.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No alert history loaded", color = TextGrey)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(alertsList) { alert ->
                        AlertItemCard(alert = alert) {
                            selectedAlertForScreen = alert
                        }
                    }
                }
            }
        }
    }

    // Alerts preference subscription bottom sheet
    if (showPrefSettings) {
        ModalBottomSheet(
            onDismissRequest = { showPrefSettings = false },
            containerColor = DarkCardSurface
        ) {
            var prefSos by remember { mutableStateOf(true) }
            var prefBorder by remember { mutableStateOf(true) }
            var prefCyclone by remember { mutableStateOf(true) }
            var prefWeather by remember { mutableStateOf(false) }
            var prefReturn by remember { mutableStateOf(true) }

            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("Subscription Preferences", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("🚨 SOS Alerts (Critical)", color = Color.White)
                    Switch(checked = prefSos, onCheckedChange = {}, enabled = false) // Always ON
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("🟡 Border Warnings", color = Color.White)
                    Switch(checked = prefBorder, onCheckedChange = { prefBorder = it })
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("🌀 Cyclone Advisories", color = Color.White)
                    Switch(checked = prefCyclone, onCheckedChange = { prefCyclone = it })
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("🌊 General Weather warnings", color = Color.White)
                    Switch(checked = prefWeather, onCheckedChange = { prefWeather = it })
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("✅ Safe Return Landing logs", color = Color.White)
                    Switch(checked = prefReturn, onCheckedChange = { prefReturn = it })
                }

                Button(
                    onClick = {
                        showPrefSettings = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Preferences saved successfully.")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("SAVE SETTINGS", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// 3. PROFILE TAB (WITH RETURN ANIMATION & STATS)
// ==========================================
@Composable
fun FamilyProfileTab(
    viewModel: FamilyViewModel,
    onLogout: () -> Unit,
    snackbarHost: SnackbarHostState
) {
    val isTripActive by viewModel.isTripActive.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Confetti and Chime states
    var showConfetti by remember { mutableStateOf(false) }

    LaunchedEffect(showConfetti) {
        if (showConfetti) {
            // Soft chime sound
            try {
                val chimeUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                RingtoneManager.getRingtone(context, chimeUri)?.play()
            } catch (e: Exception) {
                // fail safe
            }
            delay(5000)
            showConfetti = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Safe Return Card Toggler
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
            border = BorderStroke(1.dp, BorderBlue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isTripActive) {
                    Text("Raja Kumar is currently at sea", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Button(
                        onClick = {
                            viewModel.confirmSafeReturn()
                            showConfetti = true
                            coroutineScope.launch {
                                snackbarHost.showSnackbar("Voyage return logged. Checked chimes sound played.")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SafeGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("CONFIRM SAFE RETURN", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text("Raja Kumar is safely ashore ✅", color = SafeGreen, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Last voyage landed: Today 04:47 PM", color = TextGrey, fontSize = 12.sp)
                }
            }
        }

        // Celebration Confetti Emojis
        if (showConfetti) {
            Box(
                modifier = Modifier.fillMaxWidth().height(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("🎉 Checkmark return confirmed! 🏆 Safe 🌊 Chime sound 🎉", color = PrimaryTeal, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        // Fisherman stats card
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
            border = BorderStroke(1.dp, BorderBlue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Raja Kumar — Trip Summary", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total trips this month:", color = TextGrey)
                    Text("14", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Sea hours this month:", color = TextGrey)
                    Text("89h", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Violations count:", color = TextGrey)
                    Text("0 alerts (Safest streak) 🏆", color = SafeGreen, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Bar Chart of Alerts per week: [1, 3, 0, 2]
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
            border = BorderStroke(1.dp, BorderBlue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Alerts per week — last 4 weeks", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(12.dp))
                
                Canvas(modifier = Modifier.fillMaxWidth().height(80.dp)) {
                    val points = listOf(1, 3, 0, 2)
                    val barWidth = 35.dp.toPx()
                    val spacing = 25.dp.toPx()
                    
                    points.forEachIndexed { i, score ->
                        if (score > 0) {
                            val startX = i.toFloat() * (barWidth + spacing) + 40f
                            drawRect(
                                color = ErrorRed,
                                topLeft = Offset(startX, size.height - (score * 20).dp.toPx()),
                                size = androidx.compose.ui.geometry.Size(barWidth, (score * 20).dp.toPx())
                            )
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Week 1 (1)", color = TextGrey, fontSize = 9.sp)
                    Text("Week 2 (3)", color = TextGrey, fontSize = 9.sp)
                    Text("Week 3 (0)", color = TextGrey, fontSize = 9.sp)
                    Text("Week 4 (2)", color = TextGrey, fontSize = 9.sp)
                }
            }
        }

        // Logout
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = BorderBlue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("LOG OUT")
        }
    }
}

// ==========================================
// 4. SUPPORT TAB (NEW SUPPORT GRID)
// ==========================================
@Composable
fun FamilySupportTab(
    viewModel: FamilyViewModel,
    snackbarHost: SnackbarHostState
) {
    var activeSubScreen by remember { mutableStateOf<String?>(null) }

    when (activeSubScreen) {
        "guide" -> EmergencyGuideScreen { activeSubScreen = null }
        "history" -> FamilyHistoryScreen(viewModel, { activeSubScreen = null })
        "messaging" -> FamilyMessageCenterScreen(snackbarHost, { activeSubScreen = null })
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text("Family Support Utilities", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MoreGridCard("Emergency Guide", Icons.Default.Help, "Step-by-step procedure guides", Modifier.weight(1f)) {
                        activeSubScreen = "guide"
                    }
                    MoreGridCard("Past Voyages Paths", Icons.Default.Map, "Read-only past trip maps", Modifier.weight(1f)) {
                        activeSubScreen = "history"
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MoreGridCard("Message Center", Icons.Default.Chat, "Feeds chat logs and preset messaging", Modifier.weight(1f)) {
                        activeSubScreen = "messaging"
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// Support Screen A: Emergency action guide
@Composable
fun EmergencyGuideScreen(onBack: () -> Unit) {
    var openCardIndex by remember { mutableIntStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text("Emergency Action Guide", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        // Scenario 1
        EmergencyCollapsibleCard(
            title = "Scenario 1: SOS Alert Received",
            color = ErrorRed,
            isOpen = openCardIndex == 0,
            onToggle = { openCardIndex = if (openCardIndex == 0) -1 else 0 },
            steps = listOf(
                "Step 1: Stay calm — Coast Guard is automatically notified.",
                "Step 2: Dial Coast Guard emergency desk: 1554.",
                "Step 3: Have Raja's Boat ID ready: TN-04-MM-1234.",
                "Step 4: Keep phone line open for dispatch updates.",
                "Step 5: Do NOT attempt to go to sea yourself."
            )
        )

        // Scenario 2
        EmergencyCollapsibleCard(
            title = "Scenario 2: Fisherman Is Overdue",
            color = WarningAmber,
            isOpen = openCardIndex == 1,
            onToggle = { openCardIndex = if (openCardIndex == 1) -1 else 1 },
            steps = listOf(
                "Step 1: Call Raja's mobile phone number first.",
                "Step 2: If no answer after 30 minutes, dial Coast Guard desk.",
                "Step 3: File a missing vessel query report at base.",
                "Step 4: Provide coordinates from the 'Track' screen."
            )
        )

        // Scenario 3
        EmergencyCollapsibleCard(
            title = "Scenario 3: Cyclone Warning Issued",
            color = CyclonePurple,
            isOpen = openCardIndex == 2,
            onToggle = { openCardIndex = if (openCardIndex == 2) -1 else 2 },
            steps = listOf(
                "Step 1: Verify Raja's position on the Track screen map.",
                "Step 2: Call him immediately to confirm return course.",
                "Step 3: Contact Coast Guard if no response received.",
                "Step 4: Note boat coordinates and colors for identification."
            )
        )
    }
}

@Composable
fun EmergencyCollapsibleCard(
    title: String,
    color: Color,
    isOpen: Boolean,
    onToggle: () -> Unit,
    steps: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, BorderBlue),
        colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { onToggle() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Icon(
                    imageVector = if (isOpen) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            AnimatedVisibility(visible = isOpen) {
                Column(modifier = Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    steps.forEach { step ->
                        Text(step, color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// Support Screen B: Past Voyages Maps list
@Composable
fun FamilyHistoryScreen(viewModel: FamilyViewModel, onBack: () -> Unit) {
    val pastTrips by viewModel.pastTrips.collectAsStateWithLifecycle()
    var selectedPathTrip by remember { mutableStateOf<TripSession?>(null) }

    if (selectedPathTrip != null) {
        AlertDialog(
            onDismissRequest = { selectedPathTrip = null },
            title = { Text("Trip #${selectedPathTrip!!.id} Route Path", color = Color.White) },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    CustomFallbackMap(
                        modifier = Modifier.fillMaxSize(),
                        boatLocations = listOf(Pair("Raja Kumar", Pair(13.12, 80.38))),
                        zoomLevel = 11
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedPathTrip = null }) {
                    Text("CLOSE", color = PrimaryTeal)
                }
            },
            containerColor = DarkCardSurface
        )
    }

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
            Text("Trip History Logs", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(pastTrips) { trip ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { selectedPathTrip = trip },
                    border = BorderStroke(1.dp, BorderBlue),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Voyage #${trip.id}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(Date(trip.startTime)) + " | " + trip.distanceCovered + " km", color = TextGrey, fontSize = 11.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (trip.alertCount > 0) WarningAmber.copy(0.15f) else SafeGreen.copy(0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(if (trip.alertCount > 0) "Alert Logged" else "Safe Voyage", color = if (trip.alertCount > 0) WarningAmber else SafeGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// Support Screen C: Message Center (Feeds Chat logs)
@Composable
fun FamilyMessageCenterScreen(snackbarHost: SnackbarHostState, onBack: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    
    val chatFeeds = listOf(
        Pair("System", "Raja Kumar started a trip at 05:30 AM"),
        Pair("System", "Weather is SAFE at Raja Kumar's location"),
        Pair("System", "Border caution alert triggered at 09:12 AM — Resolved"),
        Pair("System", "Raja Kumar is 5km from shore — returning"),
        Pair("System", "Raja Kumar safely returned at 04:47 PM ✅")
    )

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
            Text("Automated Message Center", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Message Feed Log
        LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(chatFeeds) { msg ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BorderBlue.copy(0.2f)),
                    border = BorderStroke(1.dp, BorderBlue)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("[System status]", color = PrimaryTeal, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        Text(msg.second, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Presets Toggles warning home
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
            border = BorderStroke(1.dp, BorderBlue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Broadcast Preset message to Raja Kumar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                
                val presetsList = listOf(
                    "Please call me when you can",
                    "Come back early today",
                    "Family emergency — return immediately",
                    "All good at home ❤️"
                )
                
                presetsList.forEach { preset ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                coroutineScope.launch {
                                    snackbarHost.showSnackbar("Message dispatched: \"$preset\"")
                                }
                            },
                        colors = CardDefaults.cardColors(containerColor = BorderBlue),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = preset,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MoreGridCard(
    title: String,
    icon: ImageVector,
    desc: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
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
