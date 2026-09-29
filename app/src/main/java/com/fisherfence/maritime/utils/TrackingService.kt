package com.fisherfence.maritime.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.fisherfence.maritime.R
import com.fisherfence.maritime.data.local.AlertEvent
import com.fisherfence.maritime.data.local.FishermanLocation
import com.fisherfence.maritime.data.local.TripSession
import com.fisherfence.maritime.domain.repository.FisherRepository
import com.fisherfence.maritime.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : Service() {

    @Inject
    lateinit var repository: FisherRepository

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private var simulationJob: Job? = null

    companion object {
        private const val CHANNEL_ID = "FisherFence_Tracking"
        private const val NOTIFICATION_ID = 101
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildNotification("SAFE", 18.3))
        startSimulation()
        return START_NOT_STICKY
    }

    private fun startSimulation() {
        simulationJob?.cancel()
        simulationJob = serviceScope.launch {
            // Chennai Port Coordinates as starting point
            var lat = 13.0827
            var lon = 80.2707
            val sessionId = "session_${System.currentTimeMillis()}"
            val fishermanId = "fisherman_raja"

            // Save active trip session
            val activeSession = TripSession(
                fishermanId = fishermanId,
                startTime = System.currentTimeMillis(),
                endTime = 0L,
                distanceCovered = 0.0,
                alertCount = 0,
                status = "ACTIVE"
            )
            // Wait, repository implementation will handle database insertion for trip
            
            var lastStatus = "SAFE"

            // Seed initial position in repo
            repository.updateSimulatedPosition(lat, lon)

            while (isActive) {
                delay(3000)

                // Move 0.005 degrees east/northeast (toward IMBL polygon)
                lat += 0.0005
                lon += 0.0050

                val distance = GeofenceCalculator.getDistanceToBoundary(lat, lon)
                val crossed = GeofenceCalculator.isCrossedBorder(lat, lon)

                val safetyStatus = when {
                    crossed -> "DANGER"
                    distance <= 5.0 -> "CAUTION"
                    else -> "SAFE"
                }

                // Update repository parameters
                repository.updateSimulatedPosition(lat, lon)

                // Trigger alerts if status upgraded
                if (safetyStatus != lastStatus) {
                    lastStatus = safetyStatus
                    
                    if (safetyStatus == "CAUTION") {
                        val alert = AlertEvent(
                            fishermanId = fishermanId,
                            alertType = "BORDER_CAUTION",
                            latitude = lat,
                            longitude = lon,
                            timestamp = System.currentTimeMillis(),
                            status = "ACTIVE",
                            message = "Approaching border line (caution zone). Current distance: ${String.format("%.1f", distance)} km."
                        )
                        // This updates repository alert state, which alerts ViewModels to display the fullscreen modal
                        repository.triggerSOS(fishermanId, lat, lon) // Trigger caution/alert takeover in repo
                        // Trigger sound and vibration
                        SoundVibratorManager.startAlarm(this@TrackingService)
                    } else if (safetyStatus == "DANGER") {
                        val alert = AlertEvent(
                            fishermanId = fishermanId,
                            alertType = "BORDER_CRITICAL",
                            latitude = lat,
                            longitude = lon,
                            timestamp = System.currentTimeMillis(),
                            status = "ACTIVE",
                            message = "Critical border crossing detected! Return to Indian waters immediately."
                        )
                        // Trigger critical border alert takeover in repo
                        repository.triggerSOS(fishermanId, lat, lon)
                        // Trigger sound and vibration
                        SoundVibratorManager.startAlarm(this@TrackingService)
                    }
                }

                // Update notification text
                updateNotification(safetyStatus, distance)
            }
        }
    }

    private fun updateNotification(status: String, distance: Double) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val distanceText = String.format("%.1f", distance)
        notificationManager.notify(NOTIFICATION_ID, buildNotification(status, distance))
    }

    private fun buildNotification(status: String, distance: Double): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val statusMsg = "FisherFence — Trip Active | Status: $status"
        val bodyText = if (status == "DANGER") {
            "CRITICAL BORDER CROSSING! Stop immediately."
        } else {
            "Distance to boundary: ${String.format("%.1f", distance)} km."
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(statusMsg)
            .setContentText(bodyText)
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "FisherFence Tracking Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        simulationJob?.cancel()
        serviceJob.cancel()
        SoundVibratorManager.stopAlarm()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
