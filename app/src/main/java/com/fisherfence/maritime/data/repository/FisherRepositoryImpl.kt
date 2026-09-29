package com.fisherfence.maritime.data.repository

import android.content.Context
import android.content.Intent
import android.util.Log
import com.fisherfence.maritime.data.local.*
import com.fisherfence.maritime.domain.repository.FisherRepository
import com.fisherfence.maritime.utils.GeofenceCalculator
import com.fisherfence.maritime.utils.SoundVibratorManager
import com.fisherfence.maritime.utils.TrackingService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FisherRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: FisherDatabase
) : FisherRepository {

    private val locationDao = db.locationDao()
    private val alertDao = db.alertDao()
    private val tripDao = db.tripDao()
    private val weatherDao = db.weatherDao()

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    // Flow variables
    override val isTripActive = MutableStateFlow(false)
    override val currentBoatLocation = MutableStateFlow(Pair(13.0827, 80.2707))
    override val distanceToBorder = MutableStateFlow(18.3)
    override val currentSafetyStatus = MutableStateFlow("SAFE")
    override val activeAlertEvent = MutableStateFlow<AlertEvent?>(null)
    override val activeTripSession = MutableStateFlow<TripSession?>(null)

    init {
        // Collect active trip session on startup if any (e.g. recovery)
        repositoryScope.launch {
            val active = tripDao.getActiveTrip()
            if (active != null) {
                activeTripSession.value = active
                isTripActive.value = true
            }
        }
    }

    override suspend fun startTrip(fishermanId: String) {
        if (isTripActive.value) return
        
        Log.d("FisherRepository", "Starting trip for $fishermanId")
        
        // 1. Setup Active Trip Session
        val newSession = TripSession(
            fishermanId = fishermanId,
            startTime = System.currentTimeMillis(),
            endTime = 0L,
            distanceCovered = 0.0,
            alertCount = 0,
            status = "ACTIVE"
        )
        val id = tripDao.insertTrip(newSession)
        val sessionWithId = newSession.copy(id = id)
        activeTripSession.value = sessionWithId
        
        // 2. Set State
        isTripActive.value = true
        currentBoatLocation.value = Pair(13.0827, 80.2707)
        distanceToBorder.value = GeofenceCalculator.getDistanceToBoundary(13.0827, 80.2707)
        currentSafetyStatus.value = "SAFE"
        activeAlertEvent.value = null

        // 3. Start Tracking Foreground Service
        val serviceIntent = Intent(context, TrackingService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    override suspend fun stopTrip() {
        if (!isTripActive.value) return
        
        Log.d("FisherRepository", "Stopping trip")
        
        // 1. Stop service
        val serviceIntent = Intent(context, TrackingService::class.java)
        context.stopService(serviceIntent)
        SoundVibratorManager.stopAlarm()

        // 2. Update Room Trip Database
        activeTripSession.value?.let { session ->
            val updated = session.copy(
                endTime = System.currentTimeMillis(),
                status = "COMPLETED",
                distanceCovered = 45.2 // Mock total distance covered
            )
            tripDao.updateTrip(updated)
        }

        // 3. Clear State
        isTripActive.value = false
        activeTripSession.value = null
        currentSafetyStatus.value = "SAFE"
        activeAlertEvent.value = null
    }

    override fun getAlerts(fishermanId: String): Flow<List<AlertEvent>> {
        return alertDao.getAlertsForFisherman(fishermanId)
    }

    override fun getAllAlerts(): Flow<List<AlertEvent>> {
        return alertDao.getAllAlerts()
    }

    override fun getPastTrips(fishermanId: String): Flow<List<TripSession>> {
        return tripDao.getPastTrips()
    }

    override fun getWeatherForecast(): Flow<List<WeatherData>> {
        return weatherDao.getForecast()
    }

    override suspend fun updateAlertStatus(id: Long, status: String) {
        alertDao.updateAlertStatus(id, status)
    }

    override suspend fun triggerSOS(fishermanId: String, lat: Double, lon: Double) {
        // Find existing unresolved SOS
        val alert = AlertEvent(
            fishermanId = fishermanId,
            alertType = "SOS",
            latitude = lat,
            longitude = lon,
            timestamp = System.currentTimeMillis(),
            status = "ACTIVE",
            message = "SOS DISTRESS SIGNAL SENT! broadcast coordinates."
        )
        val id = alertDao.insertAlert(alert)
        val alertWithId = alert.copy(id = id)
        activeAlertEvent.value = alertWithId
    }

    override suspend fun cancelSOS(fishermanId: String) {
        activeAlertEvent.value?.let { alert ->
            if (alert.alertType == "SOS") {
                alertDao.updateAlertStatus(alert.id, "RESOLVED")
            }
        }
        activeAlertEvent.value = null
        SoundVibratorManager.stopAlarm()
    }

    override suspend fun triggerCycloneWarning() {
        val alert = AlertEvent(
            fishermanId = "fisherman_raja",
            alertType = "CYCLONE",
            latitude = currentBoatLocation.value.first,
            longitude = currentBoatLocation.value.second,
            timestamp = System.currentTimeMillis(),
            status = "ACTIVE",
            message = "🌀 CYCLONE WARNING — Wind Speed: 65 km/h | Landfall in 18 hours. Return to shore!"
        )
        val id = alertDao.insertAlert(alert)
        activeAlertEvent.value = alert.copy(id = id)
        
        // Start alarm for cyclone alert too
        SoundVibratorManager.startAlarm(context)
    }

    override suspend fun clearActiveAlert() {
        activeAlertEvent.value?.let { alert ->
            alertDao.updateAlertStatus(alert.id, "ACKNOWLEDGED")
        }
        activeAlertEvent.value = null
        SoundVibratorManager.stopAlarm()
    }

    override fun updateSimulatedPosition(lat: Double, lon: Double) {
        currentBoatLocation.value = Pair(lat, lon)
        
        val distance = GeofenceCalculator.getDistanceToBoundary(lat, lon)
        distanceToBorder.value = distance

        val crossed = GeofenceCalculator.isCrossedBorder(lat, lon)
        val safetyStatus = when {
            crossed -> "DANGER"
            distance <= 5.0 -> "CAUTION"
            else -> "SAFE"
        }
        
        currentSafetyStatus.value = safetyStatus

        // Check if we need to auto trigger UI popup
        val currentAlert = activeAlertEvent.value
        if (currentAlert == null || currentAlert.alertType == "SOS" || currentAlert.status != "ACTIVE") {
            if (safetyStatus == "CAUTION") {
                val alert = AlertEvent(
                    fishermanId = "fisherman_raja",
                    alertType = "BORDER_CAUTION",
                    latitude = lat,
                    longitude = lon,
                    timestamp = System.currentTimeMillis(),
                    status = "ACTIVE",
                    message = "Approaching border line. Distance: ${String.format("%.1f", distance)} km."
                )
                repositoryScope.launch {
                    val id = alertDao.insertAlert(alert)
                    activeAlertEvent.value = alert.copy(id = id)
                }
            } else if (safetyStatus == "DANGER") {
                val alert = AlertEvent(
                    fishermanId = "fisherman_raja",
                    alertType = "BORDER_CRITICAL",
                    latitude = lat,
                    longitude = lon,
                    timestamp = System.currentTimeMillis(),
                    status = "ACTIVE",
                    message = "Critical border crossing detected! Return to Indian waters immediately."
                )
                repositoryScope.launch {
                    val id = alertDao.insertAlert(alert)
                    activeAlertEvent.value = alert.copy(id = id)
                }
            }
        }

        // Insert into Room DB
        if (isTripActive.value) {
            repositoryScope.launch {
                locationDao.insertLocation(
                    FishermanLocation(
                        fishermanId = "fisherman_raja",
                        latitude = lat,
                        longitude = lon,
                        speed = 12.5,
                        timestamp = System.currentTimeMillis(),
                        sessionId = activeTripSession.value?.id?.toString() ?: "default",
                        syncedToCloud = false
                    )
                )
            }
        }
    }
}
