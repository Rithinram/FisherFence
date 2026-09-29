package com.fisherfence.maritime

import android.app.Application
import com.fisherfence.maritime.data.mock.DatabaseSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class FisherApplication : Application() {

    @Inject
    lateinit var databaseSeeder: DatabaseSeeder

    override fun onCreate() {
        super.onCreate()
        
        // Seed mock data into Room Database on first app launch
        CoroutineScope(Dispatchers.IO).launch {
            databaseSeeder.seedIfNeeded()
        }
    }
}
