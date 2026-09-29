package com.fisherfence.maritime.domain.usecase

import com.fisherfence.maritime.data.local.*
import com.fisherfence.maritime.domain.repository.FisherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlertsUseCase @Inject constructor(
    private val repository: FisherRepository
) {
    fun getFishermanAlerts(fishermanId: String): Flow<List<AlertEvent>> =
        repository.getAlerts(fishermanId)

    fun getAllAlerts(): Flow<List<AlertEvent>> =
        repository.getAllAlerts()
}

class ManageTripUseCase @Inject constructor(
    val repository: FisherRepository
) {
    val isTripActive = repository.isTripActive
    val currentPosition = repository.currentBoatLocation
    val distanceToBorder = repository.distanceToBorder
    val safetyStatus = repository.currentSafetyStatus
    val activeAlert = repository.activeAlertEvent
    val activeTrip = repository.activeTripSession

    suspend fun start(fishermanId: String) = repository.startTrip(fishermanId)
    suspend fun stop() = repository.stopTrip()
    suspend fun triggerSOS(fishermanId: String, lat: Double, lon: Double) = repository.triggerSOS(fishermanId, lat, lon)
    suspend fun cancelSOS(fishermanId: String) = repository.cancelSOS(fishermanId)
    suspend fun dismissAlert() = repository.clearActiveAlert()
}

class GetWeatherUseCase @Inject constructor(
    private val repository: FisherRepository
) {
    fun getForecast(): Flow<List<WeatherData>> = repository.getWeatherForecast()
    suspend fun simulateCyclone() = repository.triggerCycloneWarning()
}
