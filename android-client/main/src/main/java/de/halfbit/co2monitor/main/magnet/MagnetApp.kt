package de.halfbit.co2monitor.main.magnet

import android.app.Application
import android.content.Context
import de.halfbit.co2monitor.commons.APPLICATION
import de.halfbit.co2monitor.main.MainApp
import magnet.Magnet
import magnet.Scope
import magnet.bind
import magnet.getSingle
import magnetx.AppExtension

abstract class MagnetApp : Application() {

    val scope: Scope by lazy {
        Magnet.createRootScope()
            .bind(this as Application)
            .bind(applicationContext, APPLICATION)
            .bind(resources, APPLICATION)
            .bind(contentResolver)
            .limit(APPLICATION)
    }

    private lateinit var appExtensions: AppExtension.Delegate

    override fun onCreate() {
        super.onCreate()
        appExtensions = scope.getSingle()
        appExtensions.onCreate()
    }

    override fun onTrimMemory(level: Int) {
        appExtensions.onTrimMemory(level)
        super.onTrimMemory(level)
    }
}

fun Context.getAppScope(): Scope =
    (applicationContext as? MainApp)?.scope
        ?: error("AppScope cannot be found in context $this")
