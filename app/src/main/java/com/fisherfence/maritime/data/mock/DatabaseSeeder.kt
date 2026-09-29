package com.fisherfence.maritime.data.mock

import com.fisherfence.maritime.data.local.*
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSeeder @Inject constructor(
    private val db: FisherDatabase
) {
    suspend fun seedIfNeeded() {
        val weatherDao = db.weatherDao()
        val alertDao = db.alertDao()
        val tripDao = db.tripDao()
        val checklistDao = db.checklistDao()
        val notificationDao = db.notificationDao()

        // Seed checklist items
        val checklistCount = try {
            db.openHelper.readableDatabase.compileStatement("SELECT COUNT(*) FROM checklist_items").simpleQueryForLong()
        } catch (e: Exception) {
            0L
        }
        if (checklistCount == 0L) {
            val checklistItems = listOf(
                ChecklistItem(1, "Life jackets available and accessible", false),
                ChecklistItem(2, "Emergency flares stocked (min. 3)", false),
                ChecklistItem(3, "Fuel level checked (above 75%)", false),
                ChecklistItem(4, "Engine oil checked", false),
                ChecklistItem(5, "VHF radio tested and working", false),
                ChecklistItem(6, "First aid kit present", false),
                ChecklistItem(7, "Food and drinking water stocked", false),
                ChecklistItem(8, "Weather forecast checked (shown inline — today's summary)", false),
                ChecklistItem(9, "Family/coast guard notified of departure", false),
                ChecklistItem(10, "Boat registration documents onboard", false),
                ChecklistItem(11, "Mobile phone charged (above 60%)", false),
                ChecklistItem(12, "GPS device functioning", false)
            )
            checklistDao.insertChecklistItems(checklistItems)
        }

        // Seed notifications
        val notificationCount = try {
            db.openHelper.readableDatabase.compileStatement("SELECT COUNT(*) FROM notifications").simpleQueryForLong()
        } catch (e: Exception) {
            0L
        }
        if (notificationCount == 0L) {
            val initialNotifications = listOf(
                SystemNotification(
                    title = "System Update",
                    message = "Welcome to FisherFence! Ensure GPS and alerts are active before departure.",
                    timestamp = System.currentTimeMillis() - 24 * 3600 * 1000,
                    isRead = false,
                    role = "all"
                ),
                SystemNotification(
                    title = "Weather Advisory",
                    message = "IMD issues general warning for SSW wind increase tomorrow morning.",
                    timestamp = System.currentTimeMillis() - 6 * 3600 * 1000,
                    isRead = false,
                    role = "all"
                ),
                SystemNotification(
                    title = "Zone B Restricted",
                    message = "Coast Guard restricted Zone B Center sector due to military exercise.",
                    timestamp = System.currentTimeMillis() - 2 * 3600 * 1000,
                    isRead = false,
                    role = "fisherman"
                ),
                SystemNotification(
                    title = "New Vessel Registered",
                    message = "Vessel TN-04-MM-9087 (Karthik M) registered and active.",
                    timestamp = System.currentTimeMillis() - 1 * 3600 * 1000,
                    isRead = false,
                    role = "coastguard"
                )
            )
            for (n in initialNotifications) {
                notificationDao.insertNotification(n)
            }
        }

        // Check if already seeded
        val count = db.openHelper.readableDatabase.compileStatement("SELECT COUNT(*) FROM weather_data").simpleQueryForLong()
        if (count > 0) return

        val sdf = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.ENGLISH)
        val parseTime = { dateStr: String ->
            try {
                sdf.parse(dateStr)?.time ?: System.currentTimeMillis()
            } catch (e: Exception) {
                System.currentTimeMillis()
            }
        }

        // 1. Seed Weather Forecast (8 slots)
        val now = System.currentTimeMillis()
        val forecastData = listOf(
            WeatherData(latitude = 13.0827, longitude = 80.2707, windSpeed = 22.0, waveHeight = 1.2, zone = "SAFE", timestamp = now, advisory = "Safe conditions for sailing"),
            WeatherData(latitude = 13.0827, longitude = 80.2707, windSpeed = 25.0, waveHeight = 1.4, zone = "SAFE", timestamp = now + 6 * 3600 * 1000, advisory = "Safe conditions for sailing"),
            WeatherData(latitude = 13.0827, longitude = 80.2707, windSpeed = 35.0, waveHeight = 2.0, zone = "CAUTION", timestamp = now + 12 * 3600 * 1000, advisory = "Approaching weather disturbance"),
            WeatherData(latitude = 13.0827, longitude = 80.2707, windSpeed = 38.0, waveHeight = 2.2, zone = "CAUTION", timestamp = now + 18 * 3600 * 1000, advisory = "Approaching weather disturbance"),
            WeatherData(latitude = 13.0827, longitude = 80.2707, windSpeed = 52.0, waveHeight = 2.8, zone = "DANGER", timestamp = now + 24 * 3600 * 1000, advisory = "IMD Advisory: Return to shore immediately"),
            WeatherData(latitude = 13.0827, longitude = 80.2707, windSpeed = 65.0, waveHeight = 3.2, zone = "DANGER", timestamp = now + 30 * 3600 * 1000, advisory = "IMD Advisory: Return to shore immediately"),
            WeatherData(latitude = 13.0827, longitude = 80.2707, windSpeed = 70.0, waveHeight = 3.5, zone = "DANGER", timestamp = now + 36 * 3600 * 1000, advisory = "IMD Advisory: Return to shore immediately"),
            WeatherData(latitude = 13.0827, longitude = 80.2707, windSpeed = 40.0, waveHeight = 2.3, zone = "CAUTION", timestamp = now + 42 * 3600 * 1000, advisory = "Slightly improving weather conditions")
        )
        for (w in forecastData) {
            weatherDao.insertWeatherData(w)
        }

        // 2. Seed Alerts (8 alerts for Raja Kumar + extra for Coast Guard)
        // Coast Guard expects alerts from multiple vessels: Raja Kumar, Murugan S, Selvam P, Rajan V, Karthik M, etc.
        val alerts = listOf(
            AlertEvent(
                fishermanId = "fisherman_raja", // Raja Kumar
                alertType = "BORDER_CAUTION",
                latitude = 13.1234,
                longitude = 80.3456,
                timestamp = parseTime("20 May 2025 08:50 AM"),
                status = "ACKNOWLEDGED",
                message = "Approaching border line (caution zone). Current distance: 5.0 km."
            ),
            AlertEvent(
                fishermanId = "fisherman_raja",
                alertType = "CYCLONE",
                latitude = 13.0891,
                longitude = 80.2134,
                timestamp = parseTime("18 May 2025 06:30 AM"),
                status = "ACKNOWLEDGED",
                message = "Cyclone Warning issued by IMD. Return to shore immediately."
            ),
            AlertEvent(
                fishermanId = "fisherman_raja",
                alertType = "BORDER_CRITICAL",
                latitude = 13.2341,
                longitude = 80.5123,
                timestamp = parseTime("15 May 2025 11:20 AM"),
                status = "ACKNOWLEDGED",
                message = "Critical border crossing detected! Return to Indian waters immediately."
            ),
            AlertEvent(
                fishermanId = "fisherman_raja",
                alertType = "SOS",
                latitude = 13.1567,
                longitude = 80.3765,
                timestamp = parseTime("10 May 2025 09:30 AM"),
                status = "RESOLVED",
                message = "SOS signal triggered. Vessel experienced engine failure. Resolved by rescue team."
            ),
            AlertEvent(
                fishermanId = "fisherman_raja",
                alertType = "WEATHER_CAUTION",
                latitude = 13.0456,
                longitude = 80.1987,
                timestamp = parseTime("08 May 2025 07:15 AM"),
                status = "ACKNOWLEDGED",
                message = "Strong winds and rough sea conditions warning."
            ),
            AlertEvent(
                fishermanId = "fisherman_raja",
                alertType = "BORDER_CAUTION",
                latitude = 13.3012,
                longitude = 80.4890,
                timestamp = parseTime("05 May 2025 10:45 AM"),
                status = "ACKNOWLEDGED",
                message = "Approaching border line (caution zone). Current distance: 5.0 km."
            ),
            AlertEvent(
                fishermanId = "fisherman_raja",
                alertType = "CYCLONE",
                latitude = 12.9876,
                longitude = 80.1234,
                timestamp = parseTime("01 May 2025 05:00 AM"),
                status = "ACKNOWLEDGED",
                message = "Severe weather warning. Returning to base."
            ),
            AlertEvent(
                fishermanId = "fisherman_raja",
                alertType = "SOS",
                latitude = 13.2109,
                longitude = 80.4321,
                timestamp = parseTime("28 Apr 2025 02:20 PM"),
                status = "RESOLVED",
                message = "SOS signal triggered. Medical emergency. Resolved."
            )
        )
        for (a in alerts) {
            alertDao.insertAlert(a)
        }

        // Seed 4 additional alerts for other fishermen to make Coast Guard alerts look rich (total 12 mock alerts)
        val cgAlerts = listOf(
            AlertEvent(
                fishermanId = "vessel_murugan", // Murugan S
                alertType = "BORDER_CAUTION",
                latitude = 13.2200,
                longitude = 80.4500,
                timestamp = parseTime("21 May 2025 09:15 AM"),
                status = "ACTIVE",
                message = "Murugan S is approaching restricted border line."
            ),
            AlertEvent(
                fishermanId = "vessel_selvam", // Selvam P
                alertType = "BORDER_CRITICAL",
                latitude = 13.0800,
                longitude = 80.5200,
                timestamp = parseTime("21 May 2025 10:00 AM"),
                status = "ACTIVE",
                message = "Selvam P crossed border boundary! Alert dispatched."
            ),
            AlertEvent(
                fishermanId = "vessel_karthik", // Karthik M
                alertType = "SOS",
                latitude = 13.3100,
                longitude = 80.4800,
                timestamp = parseTime("21 May 2025 10:15 AM"),
                status = "ACTIVE",
                message = "Karthik M: Engine stall. Distress broadcast sent!"
            ),
            AlertEvent(
                fishermanId = "vessel_subramani", // Subramani K
                alertType = "BORDER_CAUTION",
                latitude = 13.1800,
                longitude = 80.5500,
                timestamp = parseTime("20 May 2025 11:30 PM"),
                status = "ACKNOWLEDGED",
                message = "Subramani K is in caution zone."
            )
        )
        for (a in cgAlerts) {
            alertDao.insertAlert(a)
        }

        // 3. Seed Past Trips (5 entries for Raja Kumar)
        val trips = listOf(
            TripSession(
                fishermanId = "fisherman_raja",
                startTime = parseTime("20 May 2025 05:30 AM"),
                endTime = parseTime("20 May 2025 04:15 PM"),
                distanceCovered = 47.3,
                alertCount = 2,
                status = "COMPLETED"
            ),
            TripSession(
                fishermanId = "fisherman_raja",
                startTime = parseTime("18 May 2025 04:45 AM"),
                endTime = parseTime("18 May 2025 03:30 PM"),
                distanceCovered = 38.6,
                alertCount = 1,
                status = "COMPLETED"
            ),
            TripSession(
                fishermanId = "fisherman_raja",
                startTime = parseTime("15 May 2025 05:00 AM"),
                endTime = parseTime("15 May 2025 05:45 PM"),
                distanceCovered = 52.1,
                alertCount = 3,
                status = "COMPLETED"
            ),
            TripSession(
                fishermanId = "fisherman_raja",
                startTime = parseTime("10 May 2025 06:00 AM"),
                endTime = parseTime("10 May 2025 02:00 PM"),
                distanceCovered = 31.4,
                alertCount = 1,
                status = "COMPLETED"
            ),
            TripSession(
                fishermanId = "fisherman_raja",
                startTime = parseTime("08 May 2025 05:15 AM"),
                endTime = parseTime("08 May 2025 03:45 PM"),
                distanceCovered = 29.8,
                alertCount = 0,
                status = "COMPLETED"
            )
        )
        for (t in trips) {
            tripDao.insertTrip(t)
        }
    }
}
