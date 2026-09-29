package com.fisherfence.maritime.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("fisherfence_settings", Context.MODE_PRIVATE)

    private val _isDarkTheme = MutableStateFlow(prefs.getBoolean("dark_theme", true))
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    private val _isOffline = MutableStateFlow(prefs.getBoolean("offline_mode", false))
    val isOffline: StateFlow<Boolean> = _isOffline

    fun setDarkTheme(enabled: Boolean) {
        prefs.edit().putBoolean("dark_theme", enabled).apply()
        _isDarkTheme.value = enabled
    }

    fun setOfflineMode(enabled: Boolean) {
        prefs.edit().putBoolean("offline_mode", enabled).apply()
        _isOffline.value = enabled
    }

    fun isOnboardingCompleted(role: String): Boolean {
        return prefs.getBoolean("onboarding_completed_$role", false)
    }

    fun setOnboardingCompleted(role: String, completed: Boolean) {
        prefs.edit().putBoolean("onboarding_completed_$role", completed).apply()
    }
}
