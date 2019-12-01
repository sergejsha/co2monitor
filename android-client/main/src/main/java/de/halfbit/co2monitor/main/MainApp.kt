package de.halfbit.co2monitor.main

import com.jakewharton.threetenabp.AndroidThreeTen
import de.halfbit.co2monitor.main.magnet.MagnetApp

internal class MainApp : MagnetApp() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}