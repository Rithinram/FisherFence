package com.fisherfence.maritime.presentation.fisherman

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fisherfence.maritime.data.local.*
import com.fisherfence.maritime.domain.usecase.GetAlertsUseCase
import com.fisherfence.maritime.domain.usecase.GetWeatherUseCase
import com.fisherfence.maritime.domain.usecase.ManageTripUseCase
import com.fisherfence.maritime.utils.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class FishermanViewModel @Inject constructor(
    private val getAlertsUseCase: GetAlertsUseCase,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val manageTripUseCase: ManageTripUseCase,
    val db: FisherDatabase,
    val settingsManager: SettingsManager
) : ViewModel() {

    val repository = manageTripUseCase.repository

    // Active Voyage States (bound to TrackingService through repository)
    val isTripActive = manageTripUseCase.isTripActive
    val currentPosition = manageTripUseCase.currentPosition
    val distanceToBorder = manageTripUseCase.distanceToBorder
    val safetyStatus = manageTripUseCase.safetyStatus
    val activeAlert = manageTripUseCase.activeAlert
    val activeTrip = manageTripUseCase.activeTrip

    // Settings & Polish
    val isOffline = settingsManager.isOffline
    val isDarkTheme = settingsManager.isDarkTheme

    // Database Streams
    val alertsList: StateFlow<List<AlertEvent>> = getAlertsUseCase.getFishermanAlerts("fisherman_raja")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weatherForecast: StateFlow<List<WeatherData>> = getWeatherUseCase.getForecast()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pastTrips: StateFlow<List<TripSession>> = manageTripUseCase.isTripActive.flatMapLatest {
        manageTripUseCase.repository.getPastTrips("fisherman_raja")
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Checklist State
    val checklistItems: StateFlow<List<ChecklistItem>> = db.checklistDao().getChecklistItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notification State
    val notifications: StateFlow<List<SystemNotification>> = db.notificationDao().getNotificationsForRole("fisherman")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = db.notificationDao().getUnreadNotificationsCount("fisherman")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun startTrip() {
        viewModelScope.launch {
            manageTripUseCase.start("fisherman_raja")
        }
    }

    fun stopTrip() {
        viewModelScope.launch {
            manageTripUseCase.stop()
        }
    }

    fun triggerSOS() {
        viewModelScope.launch {
            val (lat, lon) = currentPosition.value
            manageTripUseCase.triggerSOS("fisherman_raja", lat, lon)
        }
    }

    fun cancelSOS() {
        viewModelScope.launch {
            manageTripUseCase.cancelSOS("fisherman_raja")
        }
    }

    fun simulateCyclone() {
        viewModelScope.launch {
            getWeatherUseCase.simulateCyclone()
        }
    }

    fun dismissActiveAlert() {
        viewModelScope.launch {
            manageTripUseCase.dismissAlert()
        }
    }

    // Safety Checklist Actions
    fun updateChecklistItem(id: Int, isChecked: Boolean) {
        viewModelScope.launch {
            db.checklistDao().updateChecklistItem(id, isChecked)
        }
    }

    fun checkAllChecklistItems() {
        viewModelScope.launch {
            db.checklistDao().updateAllChecklistItems(true)
        }
    }

    fun clearAllChecklistItems() {
        viewModelScope.launch {
            db.checklistDao().updateAllChecklistItems(false)
        }
    }

    // Settings Preference Actions
    fun setDarkTheme(enabled: Boolean) {
        settingsManager.setDarkTheme(enabled)
    }

    fun setOfflineMode(enabled: Boolean) {
        settingsManager.setOfflineMode(enabled)
    }

    fun markNotificationsAsRead() {
        viewModelScope.launch {
            db.notificationDao().markAllAsRead("fisherman")
        }
    }

    fun addNotification(title: String, message: String) {
        viewModelScope.launch {
            db.notificationDao().insertNotification(
                SystemNotification(
                    title = title,
                    message = message,
                    timestamp = System.currentTimeMillis(),
                    isRead = false,
                    role = "fisherman"
                )
            )
        }
    }
}
