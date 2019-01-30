package com.jagoancoding.planyoursemester

import android.app.Application

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        AppRepository.init(this)
    }
}