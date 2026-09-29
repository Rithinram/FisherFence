package com.fisherfence.maritime.utils

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

object SoundVibratorManager {
    private const val TAG = "SoundVibratorManager"
    private var ringtone: Ringtone? = null
    private var vibrator: Vibrator? = null

    @Suppress("DEPRECATION")
    fun startAlarm(context: Context) {
        if (ringtone != null || vibrator != null) {
            // Already running
            return
        }
        
        Log.d(TAG, "Starting sound and vibration alarm...")
        
        // 1. Start Alarm Ringtone
        try {
            val alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            ringtone = RingtoneManager.getRingtone(context, alarmUri)?.apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    isLooping = true
                }
                play()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing ringtone", e)
        }

        // 2. Start Vibrator
        try {
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            vibrator?.let { v ->
                if (v.hasVibrator()) {
                    val timings = longArrayOf(0, 500, 200, 500)
                    // repeat = 0 means repeat starting from index 0
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createWaveform(timings, 0))
                    } else {
                        v.vibrate(timings, 0)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting vibrator", e)
        }
    }

    fun stopAlarm() {
        Log.d(TAG, "Stopping sound and vibration alarm...")
        try {
            ringtone?.let {
                if (it.isPlaying) {
                    it.stop()
                }
            }
            ringtone = null
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping ringtone", e)
        }

        try {
            vibrator?.cancel()
            vibrator = null
        } catch (e: Exception) {
            Log.e(TAG, "Error cancelling vibrator", e)
        }
    }
}
