package com.thryvii.app

import android.app.Application
import com.thryvii.app.di.AppContainer

class ThryviiApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(applicationContext)
    }
}
