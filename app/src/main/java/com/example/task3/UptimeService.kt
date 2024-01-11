package com.example.task3

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.SystemClock

class UptimeService : Service() {

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): UptimeService = this@UptimeService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun getUptimeMillis(): Long {
        return SystemClock.elapsedRealtime()
    }
}
