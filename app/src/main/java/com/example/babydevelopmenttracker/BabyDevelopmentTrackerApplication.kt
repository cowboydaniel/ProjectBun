package com.example.babydevelopmenttracker

import android.app.Application
import com.google.android.material.color.DynamicColors

class BabyDevelopmentTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
