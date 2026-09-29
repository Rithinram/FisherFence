package com.fisherfence.maritime.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: FishermanLocation): Long

    @Query("SELECT * FROM fisherman_locations WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getLocationsForSession(sessionId: String): Flow<List<FishermanLocation>>

    @Query("SELECT * FROM fisherman_locations ORDER BY timestamp DESC LIMIT 1")
    fun getLatestLocation(): Flow<FishermanLocation?>

    @Query("SELECT * FROM fisherman_locations ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestLocationSync(): FishermanLocation?

    @Query("DELETE FROM fisherman_locations")
    suspend fun deleteAll()
}

@Dao
interface AlertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertEvent): Long

    @Query("SELECT * FROM alert_events ORDER BY timestamp DESC")
    fun getAllAlerts(): Flow<List<AlertEvent>>

    @Query("SELECT * FROM alert_events WHERE fishermanId = :fishermanId ORDER BY timestamp DESC")
    fun getAlertsForFisherman(fishermanId: String): Flow<List<AlertEvent>>

    @Query("UPDATE alert_events SET status = :status WHERE id = :id")
    suspend fun updateAlertStatus(id: Long, status: String)

    @Query("SELECT COUNT(*) FROM alert_events WHERE status = 'ACTIVE'")
    fun getActiveAlertsCount(): Flow<Int>

    @Query("DELETE FROM alert_events")
    suspend fun deleteAll()
}

@Dao
interface TripDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripSession): Long

    @Update
    suspend fun updateTrip(trip: TripSession)

    @Query("SELECT * FROM trip_sessions WHERE status = 'ACTIVE' LIMIT 1")
    suspend fun getActiveTrip(): TripSession?

    @Query("SELECT * FROM trip_sessions WHERE status = 'ACTIVE' LIMIT 1")
    fun getActiveTripFlow(): Flow<TripSession?>

    @Query("SELECT * FROM trip_sessions WHERE status = 'COMPLETED' ORDER BY startTime DESC")
    fun getPastTrips(): Flow<List<TripSession>>

    @Query("SELECT * FROM trip_sessions ORDER BY startTime DESC")
    fun getAllTrips(): Flow<List<TripSession>>

    @Query("DELETE FROM trip_sessions")
    suspend fun deleteAll()
}

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherData(weather: WeatherData): Long

    @Query("SELECT * FROM weather_data ORDER BY timestamp ASC")
    fun getForecast(): Flow<List<WeatherData>>

    @Query("SELECT * FROM weather_data ORDER BY timestamp DESC LIMIT 1")
    fun getLatestWeather(): Flow<WeatherData?>

    @Query("DELETE FROM weather_data")
    suspend fun deleteAll()
}

@Dao
interface ChecklistDao {
    @Query("SELECT * FROM checklist_items ORDER BY id ASC")
    fun getChecklistItems(): Flow<List<ChecklistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItems(items: List<ChecklistItem>)

    @Query("UPDATE checklist_items SET isChecked = :isChecked WHERE id = :id")
    suspend fun updateChecklistItem(id: Int, isChecked: Boolean)

    @Query("UPDATE checklist_items SET isChecked = :isChecked")
    suspend fun updateAllChecklistItems(isChecked: Boolean)

    @Query("DELETE FROM checklist_items")
    suspend fun deleteAll()
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE role = :role OR role = 'all' ORDER BY timestamp DESC LIMIT 10")
    fun getNotificationsForRole(role: String): Flow<List<SystemNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: SystemNotification): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE role = :role OR role = 'all'")
    suspend fun markAllAsRead(role: String)

    @Query("SELECT COUNT(*) FROM notifications WHERE (role = :role OR role = 'all') AND isRead = 0")
    fun getUnreadNotificationsCount(role: String): Flow<Int>

    @Query("DELETE FROM notifications")
    suspend fun deleteAll()
}

