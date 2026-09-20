package com.example

import android.app.Application
import com.example.data.local.AppDatabase
import com.example.data.remote.FirebaseService
import com.example.data.repository.ShayariRepository
import com.example.notification.DailyNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShayariApplication : Application() {
    lateinit var database: AppDatabase
        private set
    lateinit var firebaseService: FirebaseService
        private set
    lateinit var repository: ShayariRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        DailyNotificationManager.createNotificationChannel(this)
        DailyNotificationManager.scheduleDailyMorningAlarm(this)
        com.example.widget.ShayariDailyWidgetProvider.schedule24HourUpdate(this)
        com.example.ads.AdMobManager.initialize(this)

        database = AppDatabase.getDatabase(this)
        firebaseService = FirebaseService()
        repository = ShayariRepository(database.shayariDao(), firebaseService)

        // Seed initial data asynchronously
        CoroutineScope(Dispatchers.IO).launch {
            repository.initializeSeedDataIfNeeded()
            repository.checkAndUpdateDailyTrending()
            com.example.data.repository.ShayariOfTheDayManager.resolveDailyPick(this@ShayariApplication, database)
        }
    }

    companion object {
        lateinit var instance: ShayariApplication
            private set
    }
}
