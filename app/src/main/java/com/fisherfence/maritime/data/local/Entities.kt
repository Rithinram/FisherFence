package com.fisherfence.maritime.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fisherman_locations")
data class FishermanLocation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fishermanId: String,
    val latitude: Double,
    val longitude: Double,
    val speed: Double,
    val timestamp: Long,
    val sessionId: String,
    val syncedToCloud: Boolean
)

@Entity(tableName = "alert_events")
data class AlertEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fishermanId: String,
    val alertType: String, // "BORDER_CAUTION", "BORDER_CRITICAL", "SOS", "CYCLONE", "WEATHER_CAUTION"
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val status: String, // "ACKNOWLEDGED", "ACTIVE", "RESOLVED"
    val message: String
)

@Entity(tableName = "trip_sessions")
data class TripSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fishermanId: String,
    val startTime: Long,
    val endTime: Long,
    val distanceCovered: Double,
    val alertCount: Int,
    val status: String // "ACTIVE", "COMPLETED"
)

@Entity(tableName = "weather_data")
data class WeatherData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val windSpeed: Double,
    val waveHeight: Double,
    val zone: String, // "SAFE", "CAUTION", "DANGER"
    val timestamp: Long,
    val advisory: String
)

@Entity(tableName = "checklist_items")
data class ChecklistItem(
    @PrimaryKey val id: Int,
    val title: String,
    val isChecked: Boolean
)

@Entity(tableName = "notifications")
data class SystemNotification(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val role: String // "fisherman", "coastguard", "family", "all"
)

