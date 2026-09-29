package com.fisherfence.maritime.presentation.coastguard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fisherfence.maritime.data.local.*
import com.fisherfence.maritime.domain.repository.FisherRepository
import com.fisherfence.maritime.domain.usecase.GetAlertsUseCase
import com.fisherfence.maritime.domain.usecase.ManageTripUseCase
import com.fisherfence.maritime.utils.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoastGuardViewModel @Inject constructor(
    private val getAlertsUseCase: GetAlertsUseCase,
    val manageTripUseCase: ManageTripUseCase,
    val db: FisherDatabase,
    val settingsManager: SettingsManager
) : ViewModel() {

    val repository = manageTripUseCase.repository

    val allAlerts: StateFlow<List<AlertEvent>> = getAlertsUseCase.getAllAlerts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeAlert = manageTripUseCase.activeAlert

    // Settings
    val isOffline = settingsManager.isOffline
    val isDarkTheme = settingsManager.isDarkTheme

    // Notification feeds
    val notifications: StateFlow<List<SystemNotification>> = db.notificationDao().getNotificationsForRole("coastguard")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = db.notificationDao().getUnreadNotificationsCount("coastguard")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun updateAlertStatus(alertId: Long, status: String) {
        viewModelScope.launch {
            repository.updateAlertStatus(alertId, status)
        }
    }

    fun resolveAlert(alertId: Long) {
        viewModelScope.launch {
            repository.updateAlertStatus(alertId, "RESOLVED")
            repository.clearActiveAlert()
        }
    }

    fun setDarkTheme(enabled: Boolean) {
        settingsManager.setDarkTheme(enabled)
    }

    fun setOfflineMode(enabled: Boolean) {
        settingsManager.setOfflineMode(enabled)
    }

    fun markNotificationsAsRead() {
        viewModelScope.launch {
            db.notificationDao().markAllAsRead("coastguard")
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
                    role = "coastguard"
                )
            )
        }
    }
}
