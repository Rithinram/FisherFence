package com.fisherfence.maritime.presentation.family

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fisherfence.maritime.data.local.*
import com.fisherfence.maritime.domain.repository.FisherRepository
import com.fisherfence.maritime.domain.usecase.GetAlertsUseCase
import com.fisherfence.maritime.domain.usecase.ManageTripUseCase
import com.fisherfence.maritime.utils.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FamilyViewModel @Inject constructor(
    private val getAlertsUseCase: GetAlertsUseCase,
    private val manageTripUseCase: ManageTripUseCase,
    val db: FisherDatabase,
    val settingsManager: SettingsManager
) : ViewModel() {

    val repository = manageTripUseCase.repository

    // Voyage states
    val isTripActive = manageTripUseCase.isTripActive
    val currentPosition = manageTripUseCase.currentPosition
    val distanceToBorder = manageTripUseCase.distanceToBorder
    val safetyStatus = manageTripUseCase.safetyStatus
    val activeAlert = manageTripUseCase.activeAlert
    val activeTrip = manageTripUseCase.activeTrip

    // Settings
    val isOffline = settingsManager.isOffline
    val isDarkTheme = settingsManager.isDarkTheme

    // Seed alert list for Raja Kumar
    val rajaAlerts: StateFlow<List<AlertEvent>> = getAlertsUseCase.getFishermanAlerts("fisherman_raja")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Database streams
    val notifications: StateFlow<List<SystemNotification>> = db.notificationDao().getNotificationsForRole("family")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = db.notificationDao().getUnreadNotificationsCount("family")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val pastTrips: StateFlow<List<TripSession>> = manageTripUseCase.isTripActive.flatMapLatest {
        repository.getPastTrips("fisherman_raja")
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun dismissAlert() {
        viewModelScope.launch {
            manageTripUseCase.dismissAlert()
        }
    }

    fun markNotificationsAsRead() {
        viewModelScope.launch {
            db.notificationDao().markAllAsRead("family")
        }
    }

    fun setDarkTheme(enabled: Boolean) {
        settingsManager.setDarkTheme(enabled)
    }

    fun setOfflineMode(enabled: Boolean) {
        settingsManager.setOfflineMode(enabled)
    }

    fun confirmSafeReturn() {
        viewModelScope.launch {
            // Confirm safe return in repository
            manageTripUseCase.stop()
        }
    }
}
