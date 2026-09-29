package com.fisherfence.maritime.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        FishermanLocation::class,
        AlertEvent::class,
        TripSession::class,
        WeatherData::class,
        ChecklistItem::class,
        SystemNotification::class
    ],
    version = 2,
    exportSchema = false
)
abstract class FisherDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun alertDao(): AlertDao
    abstract fun tripDao(): TripDao
    abstract fun weatherDao(): WeatherDao
    abstract fun checklistDao(): ChecklistDao
    abstract fun notificationDao(): NotificationDao
}
