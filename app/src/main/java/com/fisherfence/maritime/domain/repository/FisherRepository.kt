package com.fisherfence.maritime.domain.repository

import com.fisherfence.maritime.data.local.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface FisherRepository {
    // Trip controls
    val isTripActive: StateFlow<Boolean>
    val currentBoatLocation: StateFlow<Pair<Double, Double>> // Lat, Lon
    val distanceToBorder: StateFlow<Double> // km
    val currentSafetyStatus: StateFlow<String> // "SAFE", "CAUTION", "DANGER"
    val activeAlertEvent: StateFlow<AlertEvent?>
    val activeTripSession: StateFlow<TripSession?>

    suspend fun startTrip(fishermanId: String)
    suspend fun stopTrip()

    // Database access flows
    fun getAlerts(fishermanId: String): Flow<List<AlertEvent>>
    fun getAllAlerts(): Flow<List<AlertEvent>>
    fun getPastTrips(fishermanId: String): Flow<List<TripSession>>
    fun getWeatherForecast(): Flow<List<WeatherData>>

    // Actions
    suspend fun updateAlertStatus(id: Long, status: String)
    suspend fun triggerSOS(fishermanId: String, lat: Double, lon: Double)
    suspend fun cancelSOS(fishermanId: String)
    suspend fun triggerCycloneWarning()
    suspend fun clearActiveAlert()
    
    // Position simulation updates
    fun updateSimulatedPosition(lat: Double, lon: Double)
}
