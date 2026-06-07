package com.ai.voicechanger

import android.app.Application
import androidx.preference.PreferenceManager

class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: AppApplication
            private set
    }

    val preferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }
}
