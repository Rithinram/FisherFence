@file:OptIn(ExperimentalMaterial3Api::class)
package com.fisherfence.maritime.presentation.coastguard

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.draw.scale
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fisherfence.maritime.data.local.AlertEvent
import com.fisherfence.maritime.data.local.SystemNotification
import com.fisherfence.maritime.presentation.common.CustomFallbackMap
import com.fisherfence.maritime.presentation.common.SosTakeoverScreen
import com.fisherfence.maritime.presentation.common.OnboardingCarousel
import com.fisherfence.maritime.presentation.common.NotificationDrawer
import com.fisherfence.maritime.presentation.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoastGuardHomeScreen(
    viewModel: CoastGuardViewModel,
    onLogout: () -> Unit
) {
    val activeAlert by viewModel.activeAlert.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Global states preserved across tab switches
    var selectedTab by remember { mutableIntStateOf(0) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    // Onboarding Walkthrough
    val settingsManager = viewModel.manageTripUseCase.repository.let {
        // Safe retrieve settingsManager
        viewModel.settingsManager
    }
    var showOnboarding by remember { mutableStateOf(!settingsManager.isOnboardingCompleted("coastguard")) }

    // Connectivity status & notifications count
    val isOffline by viewModel.isOffline.collectAsStateWithLifecycle()
    val unreadNotifications by viewModel.unreadNotificationsCount.collectAsStateWithLifecycle()
    val notificationsList by viewModel.notifications.collectAsStateWithLifecycle()

    // Dashboard Shared states
    var zoneOfficers by remember {
        mutableStateOf(
            mapOf(
                "Zone A" to "Rajan K",
                "Zone B" to "Suresh M",
                "Zone C" to "Priya N",
                "Zone D" to "Unassigned"
            )
        )
    }

    var zoneRestrictedStates by remember {
        mutableStateOf(
            mapOf(
                "A" to "OPEN",
                "B" to "OPEN",
                "C" to "OPEN",
                "D" to "CAUTION"
            )
        )
    }

    if (activeAlert != null && activeAlert?.alertType == "SOS" && activeAlert?.status == "ACTIVE") {
        SosTakeoverScreen(
            alert = activeAlert!!,
            role = "coastguard",
            onDismiss = { /* CG resolves through button action */ },
            onResolve = { viewModel.resolveAlert(activeAlert!!.id) },
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
                                label = { Text("COAST GUARD", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp) },
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
                        // Notifications Drawer bell icon
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
                        // Demo Settings
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
                        Triple(0, Icons.Default.Dashboard, "Dashboard"),
                        Triple(1, Icons.Default.DirectionsBoat, "Vessels"),
                        Triple(2, Icons.Default.Notifications, "Alerts"),
                        Triple(3, Icons.Default.BarChart, "Reports"),
                        Triple(4, Icons.Default.GridOn, "Operations")
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

                    // Main Content areas
                    Box(modifier = Modifier.weight(1f)) {
                        when (selectedTab) {
                            0 -> CoastGuardDashboardTab(
                                viewModel = viewModel,
                                snackbarHostState = snackbarHostState,
                                zoneOfficers = zoneOfficers,
                                onUpdateOfficer = { zone, name -> zoneOfficers = zoneOfficers.toMutableMap().apply { put(zone, name) } },
                                zoneRestrictions = zoneRestrictedStates
                            )
                            1 -> CoastGuardVesselsTab(snackbarHostState)
                            2 -> CoastGuardAlertsTab(viewModel, snackbarHostState)
                            3 -> CoastGuardReportsTab()
                            4 -> CoastGuardOperationsTab(
                                viewModel = viewModel,
                                snackbarHost = snackbarHostState,
                                zoneRestrictedStates = zoneRestrictedStates,
                                onToggleZone = { zone, status -> zoneRestrictedStates = zoneRestrictedStates.toMutableMap().apply { put(zone, status) } }
                            )
                        }
                    }
                }

                // Notification drawer dropdown
                NotificationDrawer(
                    isOpen = unreadNotifications > 100, // Dummy toggled on click manually
                    notifications = notificationsList,
                    onMarkAllAsRead = { viewModel.markNotificationsAsRead() },
                    onClose = {}
                )

                // Onboarding walkthrough
                if (showOnboarding) {
                    OnboardingCarousel(role = "coastguard") {
                        showOnboarding = false
                        settingsManager.setOnboardingCompleted("coastguard", true)
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
// 1. DASHBOARD TAB
// ==========================================
@Composable
fun CoastGuardDashboardTab(
    viewModel: CoastGuardViewModel,
    snackbarHostState: SnackbarHostState,
    zoneOfficers: Map<String, String>,
    onUpdateOfficer: (String, String) -> Unit,
    zoneRestrictions: Map<String, String>
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedZoneForMap by remember { mutableStateOf<String?>(null) }
    var showWeatherBriefing by remember { mutableStateOf(true) }

    // Mock active vessels data
    val activeVessels = listOf(
        Pair("Raja Kumar", Pair(13.12, 80.38)),
        Pair("Murugan S", Pair(13.18, 80.44)),
        Pair("Selvam P", Pair(13.05, 80.51))
    )
    val vesselStatuses = mapOf(
        "Raja Kumar" to "SAFE",
        "Murugan S" to "CAUTION",
        "Selvam P" to "DANGER"
    )

    if (selectedZoneForMap != null) {
        AlertDialog(
            onDismissRequest = { selectedZoneForMap = null },
            title = { Text("Sector Focus - $selectedZoneForMap", color = Color.White) },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    // Show map overlays only for selected zone
                    CustomFallbackMap(
                        modifier = Modifier.fillMaxSize(),
                        boatLocations = activeVessels,
                        boatStatuses = vesselStatuses,
                        restrictedZones = zoneRestrictions,
                        zoomLevel = 11
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedZoneForMap = null }) {
                    Text("CLOSE", color = PrimaryTeal)
                }
            },
            containerColor = DarkCardSurface
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Statistics Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatsCard("Active", "12", SafeGreen, Modifier.weight(1f))
            StatsCard("Border", "3", WarningAmber, Modifier.weight(1f))
            StatsCard("SOS alerts", "1", ErrorRed, Modifier.weight(1f))
            StatsCard("Returns", "8", PrimaryTeal, Modifier.weight(1f))
        }

        // Sector Patrol Assignment Widget
        Text("Sector Patrol Management", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val zones = listOf("Zone A", "Zone B", "Zone C", "Zone D")
            items(zones) { zone ->
                var showAssignDropdown by remember { mutableStateOf(false) }
                val officer = zoneOfficers[zone] ?: "Unassigned"
                val zoneCode = zone.takeLast(1)
                val status = zoneRestrictions[zoneCode] ?: "OPEN"

                Card(
                    modifier = Modifier
                        .width(170.dp)
                        .clickable { selectedZoneForMap = zone },
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    border = BorderStroke(1.dp, BorderBlue)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(zone, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (status == "RESTRICTED") ErrorRed.copy(0.15f) else SafeGreen.copy(0.15f))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(status, color = if (status == "RESTRICTED") ErrorRed else SafeGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        Text("Vessels: ${if (zoneCode == "B") 4 else 2}", color = TextGrey, fontSize = 11.sp)
                        Text("Officer: $officer", color = if (officer == "Unassigned") WarningAmber else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        Box {
                            Button(
                                onClick = { showAssignDropdown = true },
                                colors = ButtonDefaults.buttonColors(containerColor = BorderBlue),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("Assign Officer", color = Color.White, fontSize = 10.sp)
                            }
                            DropdownMenu(
                                expanded = showAssignDropdown,
                                onDismissRequest = { showAssignDropdown = false },
                                modifier = Modifier.background(DarkCardSurface)
                            ) {
                                val officersList = listOf("Rajan K", "Suresh M", "Priya N", "Anbu T")
                                officersList.forEach { name ->
                                    DropdownMenuItem(
                                        text = { Text(name, color = Color.White) },
                                        onClick = {
                                            onUpdateOfficer(zone, name)
                                            showAssignDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Collapsible weather briefing panel
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
            border = BorderStroke(1.dp, BorderBlue)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { showWeatherBriefing = !showWeatherBriefing },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Operational Weather Briefing — Chennai Sea", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Icon(
                        imageVector = if (showWeatherBriefing) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                AnimatedVisibility(visible = showWeatherBriefing) {
                    Column(modifier = Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(WarningAmber).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                Text("CAUTION", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Wind speed: 35 km/h from SSW | Waves: 2.1 m", color = Color.White, fontSize = 12.sp)
                        }
                        Text("Visibility: 6 km (Moderate visibility due to moisture fog)", color = TextGrey, fontSize = 12.sp)
                        Text("IMD Advisory: Fishermen advised not to venture beyond 20km from shore.", color = ErrorRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = {
                                viewModel.addNotification("Restricted Alert Broadcast", "Zone B restricted by Coast Guard. Returning advised.")
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Alert broadcasted: Restricted Zone B. Return warning pushed.")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Restrict Zone B Sector", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Live Map Viewport at the bottom
        Spacer(modifier = Modifier.height(16.dp))
        Text("Overview Tracking Sector Map", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, BorderBlue, RoundedCornerShape(12.dp))
        ) {
            CustomFallbackMap(
                modifier = Modifier.fillMaxSize(),
                boatLocations = activeVessels,
                boatStatuses = vesselStatuses,
                restrictedZones = zoneRestrictions
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ==========================================
// 2. VESSELS TAB
// ==========================================
@Composable
fun CoastGuardVesselsTab(snackbarHostState: SnackbarHostState) {
    val coroutineScope = rememberCoroutineScope()
    
    // Track screen dialog overlays
    var activeRouteVesselName by remember { mutableStateOf<String?>(null) }
    var activeRouteVesselCoords by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var showInterception by remember { mutableStateOf(false) }
    var activeDetailVessel by remember { mutableStateOf<VesselItem?>(null) }

    val vesselsList = listOf(
        VesselItem("Raja Kumar", "TN-04-MM-1234", "SAFE", Pair(13.15, 80.38), "2 min ago"),
        VesselItem("Murugan S", "TN-04-MM-5678", "CAUTION", Pair(13.22, 80.45), "5 min ago"),
        VesselItem("Selvam P", "TN-04-MM-4321", "DANGER", Pair(13.08, 80.52), "1 min ago"),
        VesselItem("Rajan V", "TN-04-MM-8765", "SAFE", Pair(12.95, 80.35), "8 min ago"),
        VesselItem("Karthik M", "TN-04-MM-9087", "SOS", Pair(13.31, 80.48), "just now"),
        VesselItem("Anbu T", "TN-04-MM-2211", "SAFE", Pair(13.05, 80.29), "3 min ago")
    )

    // Interception dialogue mapping
    if (showInterception && activeRouteVesselName != null && activeRouteVesselCoords != null) {
        AlertDialog(
            onDismissRequest = { showInterception = false },
            title = { Text("Interception Planner - $activeRouteVesselName", color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        CustomFallbackMap(
                            modifier = Modifier.fillMaxSize(),
                            boatLocations = listOf(Pair(activeRouteVesselName!!, activeRouteVesselCoords!!)),
                            boatStatuses = mapOf(activeRouteVesselName!! to "DANGER"),
                            interceptionPath = listOf(Pair(13.0900, 80.2850), activeRouteVesselCoords!!), // Base to Boat
                            zoomLevel = 11
                        )
                    }
                    Text("Base station: Chennai Port Port [13.0900, 80.2850]", color = TextGrey, fontSize = 11.sp)
                    Text("Target vessel position: ${activeRouteVesselCoords!!.first}, ${activeRouteVesselCoords!!.second}", color = TextGrey, fontSize = 11.sp)
                    Text("Distance to intercept: 28.5 km", color = PrimaryTeal, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Patrol Boat ETA: 34 minutes at 25 knots", color = WarningAmber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showInterception = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Patrol interceptor Alpha dispatched to $activeRouteVesselName.")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SafeGreen)
                ) {
                    Text("Dispatch Unit", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showInterception = false }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = DarkCardSurface
        )
    }

    // Detail profile dialogue with Comms Log Tab
    if (activeDetailVessel != null) {
        var detailSubTab by remember { mutableIntStateOf(0) } // 0 -> Details, 1 -> Comms Log
        var mockTypedMessage by remember { mutableStateOf("") }
        var mockChatMessages by remember {
            mutableStateOf(
                mutableListOf(
                    Pair("CG", "Vessel ${activeDetailVessel!!.name}, please confirm status"),
                    Pair("Boat", "All good, 18km from shore, heading back by 5PM"),
                    Pair("CG", "Weather advisory issued. Return by 3PM recommended"),
                    Pair("Boat", "Understood. Reducing depth, heading back"),
                    Pair("System", "Border caution alert triggered — auto-notified")
                )
            )
        }

        AlertDialog(
            onDismissRequest = { activeDetailVessel = null },
            title = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(activeDetailVessel!!.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(activeDetailVessel!!.boatId, color = TextGrey, fontSize = 12.sp)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth().height(320.dp)) {
                    // Sub Tabs inside dialogue
                    TabRow(
                        selectedTabIndex = detailSubTab,
                        containerColor = DarkCardSurface,
                        contentColor = PrimaryTeal,
                        divider = {}
                    ) {
                        Tab(selected = detailSubTab == 0, onClick = { detailSubTab = 0 }, text = { Text("Profile Specs", fontSize = 12.sp) })
                        Tab(selected = detailSubTab == 1, onClick = { detailSubTab = 1 }, text = { Text("Comms Log", fontSize = 12.sp) })
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))

                    if (detailSubTab == 0) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Boat registry:", color = TextGrey)
                                Text(activeDetailVessel!!.boatId, color = Color.White)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Status classification:", color = TextGrey)
                                Text(activeDetailVessel!!.status, color = if (activeDetailVessel!!.status == "SAFE") SafeGreen else WarningAmber)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Last coordinates:", color = TextGrey)
                                Text("${activeDetailVessel!!.coords.first}, ${activeDetailVessel!!.coords.second}", color = Color.White)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Emergency contact:", color = TextGrey)
                                Text("+91 94432 12345 (Family)", color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    activeRouteVesselName = activeDetailVessel!!.name
                                    activeRouteVesselCoords = activeDetailVessel!!.coords
                                    showInterception = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Plan Interception Route", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        // Comms Logs chat style
                        Column(modifier = Modifier.fillMaxSize()) {
                            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(mockChatMessages) { msg ->
                                    val isMe = msg.first == "CG"
                                    val isSys = msg.first == "System"
                                    
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = if (isSys) Alignment.Center else if (isMe) Alignment.CenterEnd else Alignment.CenterStart
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSys) Color.Gray.copy(0.2f) else if (isMe) PrimaryTeal else BorderBlue)
                                                .padding(8.dp)
                                        ) {
                                            Text(
                                                text = (if (isSys) "" else "${msg.first}: ") + msg.second,
                                                color = if (isMe) Color.Black else Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = mockTypedMessage,
                                    onValueChange = { mockTypedMessage = it },
                                    placeholder = { Text("Send warning...", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = {
                                        if (mockTypedMessage.isNotBlank()) {
                                            mockChatMessages.add(Pair("CG", mockTypedMessage))
                                            mockTypedMessage = ""
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Radio message dispatched via VHF band.")
                                            }
                                        }
                                    },
                                    modifier = Modifier.size(40.dp).background(PrimaryTeal, CircleShape)
                                ) {
                                    Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color.Black)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { activeDetailVessel = null }) {
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
        Text("Registered Vessels Directory", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(vesselsList) { vessel ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { activeDetailVessel = vessel },
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
                                .size(40.dp)
                                .background(BorderBlue, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(vessel.name.take(2).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(vessel.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("ID: ${vessel.boatId} | Last: ${vessel.lastSeen}", color = TextGrey, fontSize = 11.sp)
                        }
                        
                        val statusColor = when (vessel.status) {
                            "DANGER" -> ErrorRed
                            "CAUTION" -> WarningAmber
                            "SOS" -> ErrorRed
                            else -> SafeGreen
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(statusColor.copy(0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(vessel.status, color = statusColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

data class VesselItem(val name: String, val boatId: String, val status: String, val coords: Pair<Double, Double>, val lastSeen: String)

// ==========================================
// 3. ALERTS TAB
// ==========================================
@Composable
fun CoastGuardAlertsTab(viewModel: CoastGuardViewModel, snackbarHost: SnackbarHostState) {
    val alertsList by viewModel.allAlerts.collectAsStateWithLifecycle()
    var alertFilterTab by remember { mutableIntStateOf(0) } // 0 -> ALL, 1 -> PRIORITY
    val coroutineScope = rememberCoroutineScope()

    // Mock count up timers state
    var secondsTicker by remember { mutableLongStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            secondsTicker++
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Maritime Incident Feed", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Card(
                border = BorderStroke(1.dp, BorderBlue),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
            ) {
                Text("Avg SLA: 8.3 min", color = SafeGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(6.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))

        // Alerts filtration sub tabs
        TabRow(
            selectedTabIndex = alertFilterTab,
            containerColor = DarkCardSurface,
            contentColor = PrimaryTeal,
            divider = {}
        ) {
            Tab(selected = alertFilterTab == 0, onClick = { alertFilterTab = 0 }, text = { Text("ALL HISTORY", fontSize = 12.sp) })
            Tab(selected = alertFilterTab == 1, onClick = { alertFilterTab = 1 }, text = { Text("🚨 PRIORITY QUEUE", fontSize = 12.sp) })
        }

        Spacer(modifier = Modifier.height(12.dp))

        val filteredAlerts = remember(alertsList, alertFilterTab) {
            if (alertFilterTab == 0) {
                alertsList
            } else {
                // Priority alerts: SOS first, then CRITICAL, then CAUTION/CYCLONE, active ones first
                alertsList.filter { it.status == "ACTIVE" }.sortedWith(
                    compareByDescending<AlertEvent> { it.alertType == "SOS" }
                        .thenByDescending { it.alertType == "BORDER_CRITICAL" }
                )
            }
        }

        if (filteredAlerts.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No incidents logged in this queue", color = TextGrey)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredAlerts) { alert ->
                    val elapsedSinceAlert = (System.currentTimeMillis() - alert.timestamp) / 1000 + secondsTicker % 1 // approximate
                    val elapsedMinutes = (elapsedSinceAlert / 60)
                    
                    val timeColor = when {
                        elapsedMinutes >= 15 -> ErrorRed
                        elapsedMinutes >= 5 -> WarningAmber
                        else -> SafeGreen
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, BorderBlue),
                        colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.size(8.dp).background(timeColor, CircleShape))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(alert.alertType.replace("_", " "), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                if (alert.status == "ACTIVE") {
                                    Text(
                                        text = "${String.format("%02d", elapsedMinutes % 60)}m elapsed",
                                        color = timeColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                } else {
                                    Text("RESOLVED", color = SafeGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(alert.message, color = TextGrey, fontSize = 12.sp)
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Vessel: Raja Kumar | Port: Chennai",
                                    color = TextGrey.copy(0.7f),
                                    fontSize = 11.sp
                                )

                                if (alert.status == "ACTIVE") {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        // Escalate button
                                        Button(
                                            onClick = {
                                                coroutineScope.launch {
                                                    snackbarHost.showSnackbar("Alert escalated. Senior Commander officer notified.")
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                                            contentPadding = PaddingValues(horizontal = 10.dp),
                                            modifier = Modifier.height(30.dp),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text("Escalate", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }

                                        // Resolve button
                                        Button(
                                            onClick = {
                                                viewModel.resolveAlert(alert.id)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = SafeGreen),
                                            contentPadding = PaddingValues(horizontal = 10.dp),
                                            modifier = Modifier.height(30.dp),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text("Resolve", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. REPORTS TAB
// ==========================================
@Composable
fun CoastGuardReportsTab() {
    var incidentMapToggle by remember { mutableStateOf("Border") } // Border | SOS | Weather
    var incidentTimeSpan by remember { mutableStateOf("This Month") }
    var showHandoverModal by remember { mutableStateOf(false) }

    val handoverNotes = remember {
        mutableStateListOf(
            Triple("Morning Shift | 06:00–14:00", "Officer Suresh M", "Karthik M SOS resolved at 09:45. Cyclone advisory sent to all fleet vessels."),
            Triple("Afternoon Shift | 14:00–22:00", "Officer Priya N", "All monitored vessels returned safely to Chennai port by 18:30.")
        )
    }

    if (showHandoverModal) {
        var typedNotes by remember { mutableStateOf("") }
        var typedOfficer by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showHandoverModal = false },
            title = { Text("Add Shift Handover Note", color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = typedOfficer,
                        onValueChange = { typedOfficer = it },
                        label = { Text("Officer Name") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = typedNotes,
                        onValueChange = { typedNotes = it },
                        label = { Text("Shift summary notes details") },
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth().height(100.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (typedOfficer.isNotBlank() && typedNotes.isNotBlank()) {
                            handoverNotes.add(0, Triple("Active Shift | " + SimpleDateFormat("HH:mm", Locale.ENGLISH).format(Date()), "Officer " + typedOfficer, typedNotes))
                            showHandoverModal = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                ) {
                    Text("Save Note", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showHandoverModal = false }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = DarkCardSurface
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Incident Statistics & Handovers", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)

        // Mock bar chart card
        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, BorderBlue),
            colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Voyages vs Violations (Last 4 Weeks)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))
                // Canvas bar chart
                Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                    val bars = listOf(Pair(40f, 15f), Pair(55f, 25f), Pair(30f, 5f), Pair(48f, 12f)) // Voyages vs Alerts
                    val barWidth = 35.dp.toPx()
                    val spacing = 20.dp.toPx()
                    
                    bars.forEachIndexed { i, (voy, alr) ->
                        val startX = i.toFloat() * (barWidth * 2 + spacing) + 30f
                        // Voyages bar (Teal)
                        drawRect(
                            color = PrimaryTeal,
                            topLeft = Offset(startX, size.height - (voy * 1.5f).toInt().dp.toPx()),
                            size = androidx.compose.ui.geometry.Size(barWidth, (voy * 1.5f).toInt().dp.toPx())
                        )
                        // Alerts bar (Red)
                        drawRect(
                            color = ErrorRed,
                            topLeft = Offset(startX + barWidth + 2f, size.height - (alr * 1.5f).toInt().dp.toPx()),
                            size = androidx.compose.ui.geometry.Size(barWidth, (alr * 1.5f).toInt().dp.toPx())
                        )
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Week 1", color = TextGrey, fontSize = 9.sp)
                    Text("Week 2", color = TextGrey, fontSize = 9.sp)
                    Text("Week 3", color = TextGrey, fontSize = 9.sp)
                    Text("Week 4", color = TextGrey, fontSize = 9.sp)
                }
            }
        }

        // Heatmap of incidents below stats chart
        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, BorderBlue),
            colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Incident Cluster Hotspots", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                
                // Toggle choices
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Border", "SOS", "Weather").forEach { name ->
                        FilterChip(
                            selected = incidentMapToggle == name,
                            onClick = { incidentMapToggle = name },
                            label = { Text(name, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = PrimaryTeal)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                // Mini Map view
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    CustomFallbackMap(
                        modifier = Modifier.fillMaxSize(),
                        showHeatmap = true,
                        heatmapPoints = listOf(
                            Pair(Pair(13.15, 80.45), "high"),
                            Pair(Pair(13.25, 80.38), "moderate")
                        )
                    )
                }
            }
        }

        // Handover Shift log notes
        Card(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, BorderBlue),
            colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Shift Handover Logs", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    IconButton(
                        onClick = { showHandoverModal = true },
                        modifier = Modifier.size(32.dp).background(BorderBlue, CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = PrimaryTeal)
                    }
                }
                
                handoverNotes.forEach { (shift, officer, note) ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(shift, color = PrimaryTeal, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(officer, color = TextGrey, fontSize = 10.sp)
                        }
                        Text(note, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp))
                        Divider(modifier = Modifier.padding(top = 8.dp), color = BorderBlue.copy(0.4f))
                    }
                }
            }
        }
    }
}

// ==========================================
// 5. OPERATIONS TAB (NEW OPERATIONS GRID)
// ==========================================
@Composable
fun CoastGuardOperationsTab(
    viewModel: CoastGuardViewModel,
    snackbarHost: SnackbarHostState,
    zoneRestrictedStates: Map<String, String>,
    onToggleZone: (String, String) -> Unit
) {
    var activeSubScreen by remember { mutableStateOf<String?>(null) }

    when (activeSubScreen) {
        "sar" -> SearchRescueScreen(viewModel, { activeSubScreen = null })
        "database" -> FishermanDatabaseScreen(snackbarHost, { activeSubScreen = null })
        "restrictions" -> RestrictionManagerScreen(viewModel, zoneRestrictedStates, onToggleZone, { activeSubScreen = null }, snackbarHost)
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text("Operations Control Centre", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MoreGridCard("Search & Rescue (SAR)", Icons.Default.Search, "SAR expanding grids & CG Alpha/Beta tracking", Modifier.weight(1f)) {
                        activeSubScreen = "sar"
                    }
                    MoreGridCard("Fisherman Registry DB", Icons.Default.FolderShared, "Vessel licenses, contacts, violation files", Modifier.weight(1f)) {
                        activeSubScreen = "database"
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MoreGridCard("Zone Restriction Manager", Icons.Default.Block, "Toggle zone closures during emergencies", Modifier.weight(1f)) {
                        activeSubScreen = "restrictions"
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// Operations Screen A: Search & Rescue (SAR) map
@Composable
fun SearchRescueScreen(viewModel: CoastGuardViewModel, onBack: () -> Unit) {
    var sarActive by remember { mutableStateOf(true) }
    var secondsCount by remember { mutableIntStateOf(2722) } // elapsed: 45:22

    LaunchedEffect(sarActive) {
        while (sarActive) {
            delay(1000)
            secondsCount++
        }
    }

    val formatSeconds = { totalSecs: Int ->
        val h = totalSecs / 3600
        val m = (totalSecs % 3600) / 60
        val s = totalSecs % 60
        String.format("%02d:%02d:%02d", h, m, s)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text("Search & Rescue Coordination Map", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            // SAR Map: sector polygons, CG vessels Alpha/Beta/Gamma, Distressed pulsing, expanding yellow pattern
            val cgVessels = listOf(
                Triple("CG Vessel Alpha", Pair(13.20, 80.40), 45.0),
                Triple("CG Vessel Beta", Pair(12.90, 80.35), 0.0),
                Triple("CG Vessel Gamma", Pair(13.10, 80.55), 220.0)
            )
            CustomFallbackMap(
                modifier = Modifier.fillMaxSize(),
                sarBoatPositions = cgVessels,
                sarDistressedBoat = Pair(13.15, 80.48), // distressed
                showSearchPattern = sarActive,
                searchPatternCenter = Pair(13.15, 80.48)
            )

            // Panel Overlay at bottom
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface.copy(alpha = 0.9f)),
                border = BorderStroke(1.dp, BorderBlue),
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Active SAR Operation", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Switch(checked = sarActive, onCheckedChange = { sarActive = it })
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Units Deployed", color = TextGrey, fontSize = 10.sp)
                            Text("2 Vessels", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Column {
                            Text("Search area bounds", color = TextGrey, fontSize = 10.sp)
                            Text("15 sq km grid", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Time elapsed", color = TextGrey, fontSize = 10.sp)
                            Text(formatSeconds(secondsCount), color = ErrorRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                    if (sarActive) {
                        Button(
                            onClick = { sarActive = false },
                            colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("End SAR Operation", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// Operations Screen B: Fisherman Registration Database
@Composable
fun FishermanDatabaseScreen(snackbarHost: SnackbarHostState, onBack: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var selectedProfile by remember { mutableStateOf<FishermanProfile?>(null) }

    val dbList = listOf(
        FishermanProfile("Raja Kumar", "TN-04-MM-1234", "Chennai Port", "+91 98765 43210", "Active", "12 May 2023", "2 crossings"),
        FishermanProfile("Murugan S", "TN-04-MM-5678", "Chennai Port", "+91 94456 78901", "Active", "04 Jan 2024", "0 crossings"),
        FishermanProfile("Selvam P", "TN-04-MM-4321", "Ennore Port", "+91 98941 12345", "Suspended", "19 Oct 2022", "5 crossings"),
        FishermanProfile("Rajan V", "TN-04-MM-8765", "Chennai Port", "+91 94441 54321", "Active", "30 Jun 2023", "1 crossing"),
        FishermanProfile("Karthik M", "TN-04-MM-9087", "Royapuram Port", "+91 98765 11111", "Active", "15 Aug 2024", "0 crossings")
    )

    if (selectedProfile != null) {
        AlertDialog(
            onDismissRequest = { selectedProfile = null },
            title = { Text(selectedProfile!!.name, color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Personal Spec Profile", color = PrimaryTeal, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Registry ID: ${selectedProfile!!.boatId}", color = Color.White, fontSize = 12.sp)
                    Text("Home Port base: ${selectedProfile!!.homePort}", color = Color.White, fontSize = 12.sp)
                    Text("Mobile call: ${selectedProfile!!.phone}", color = Color.White, fontSize = 12.sp)
                    Text("Vessel registration: ${selectedProfile!!.regDate}", color = Color.White, fontSize = 12.sp)
                    Text("Violations history: ${selectedProfile!!.violations}", color = if (selectedProfile!!.violations.contains("0")) SafeGreen else ErrorRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Registry status: ${selectedProfile!!.status}", color = if (selectedProfile!!.status == "Active") SafeGreen else ErrorRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedProfile = null }) {
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
            Text("Fisherman Registry Database", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by name, boat ID, phone...", color = TextGrey) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White),
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextGrey) }
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
            val filtered = dbList.filter { it.name.contains(searchQuery, true) || it.boatId.contains(searchQuery, true) }
            items(filtered) { p ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { selectedProfile = p },
                    border = BorderStroke(1.dp, BorderBlue),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp).background(BorderBlue, CircleShape), contentAlignment = Alignment.Center) {
                            Text(p.name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(p.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("ID: ${p.boatId} | Port: ${p.homePort}", color = TextGrey, fontSize = 11.sp)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (p.status == "Active") SafeGreen.copy(0.15f) else ErrorRed.copy(0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(p.status, color = if (p.status == "Active") SafeGreen else ErrorRed, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                coroutineScope.launch {
                    snackbarHost.showSnackbar("Vessel registry database exported to Downloads/Fisherman_Registry.csv")
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Export Database", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

data class FishermanProfile(val name: String, val boatId: String, val homePort: String, val phone: String, val status: String, val regDate: String, val violations: String)

// Operations Screen C: Zone Restriction Manager
@Composable
fun RestrictionManagerScreen(
    viewModel: CoastGuardViewModel,
    zoneRestrictedStates: Map<String, String>,
    onToggleZone: (String, String) -> Unit,
    onBack: () -> Unit,
    snackbarHost: SnackbarHostState
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedReason by remember { mutableStateOf("Cyclone Advisory") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text("Sea Zone Restriction Manager", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            // Map showing sector restriction color fills (A, B, C, D)
            CustomFallbackMap(
                modifier = Modifier.fillMaxSize(),
                restrictedZones = zoneRestrictedStates,
                zoomLevel = 10
            )

            // Overlaid controls card on right
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface.copy(alpha = 0.9f)),
                border = BorderStroke(1.dp, BorderBlue),
                modifier = Modifier.align(Alignment.CenterStart).width(190.dp).padding(16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Zone toggles", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    
                    val zones = listOf("A", "B", "C", "D")
                    zones.forEach { code ->
                        val status = zoneRestrictedStates[code] ?: "OPEN"
                        val isRestricted = status == "RESTRICTED"
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Zone $code", color = Color.White, fontSize = 11.sp)
                            Switch(
                                checked = isRestricted,
                                onCheckedChange = {
                                    val newStatus = if (it) "RESTRICTED" else "OPEN"
                                    onToggleZone(code, newStatus)
                                    if (it) {
                                        viewModel.addNotification("Zone Restriction Issued", "Zone $code has been restricted due to $selectedReason.")
                                        coroutineScope.launch {
                                            snackbarHost.showSnackbar("Alert broadcasted: Zone $code restricted due to $selectedReason.")
                                        }
                                    }
                                },
                                modifier = Modifier.scale(0.8f)
                            )
                        }
                    }
                }
            }
        }

        // Bottom Settings Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
            border = BorderStroke(1.dp, BorderBlue),
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Restriction Broadcast settings", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Broadcast Reason: ", color = TextGrey, fontSize = 12.sp)
                    var expandedDropdown by remember { mutableStateOf(false) }
                    Box {
                        Text(
                            text = "$selectedReason ▼",
                            color = PrimaryTeal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.clickable { expandedDropdown = true }
                        )
                        DropdownMenu(expanded = expandedDropdown, onDismissRequest = { expandedDropdown = false }, modifier = Modifier.background(DarkCardSurface)) {
                            val reasons = listOf("Cyclone Advisory", "Military Exercise", "Border Spill Incident", "Fisheries Closure")
                            reasons.forEach { r ->
                                DropdownMenuItem(text = { Text(r, color = Color.White) }, onClick = { selectedReason = r; expandedDropdown = false })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatsCard(label: String, value: String, tint: Color, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
        border = BorderStroke(1.dp, BorderBlue),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = tint, fontWeight = FontWeight.Black, fontSize = 16.sp)
            Text(label, color = TextGrey, fontSize = 10.sp, maxLines = 1)
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
