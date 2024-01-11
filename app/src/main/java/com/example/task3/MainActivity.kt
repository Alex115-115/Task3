package com.example.task3

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {

    private var service: UptimeService? = null
    private var isBound by mutableStateOf(false)

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as UptimeService.LocalBinder
            this@MainActivity.service = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Intent(this, UptimeService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                PrintBoot(isBound, service)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }
}

@Composable
fun PrintBoot(isBound: Boolean, uptimeService: UptimeService?) {
    var showUptime by remember { mutableStateOf(false) }
    var hours by remember { mutableStateOf(0L)}
    var minutes by remember { mutableStateOf(0L)}
    var seconds by remember { mutableStateOf(0L)}

    if (isBound) {
        LaunchedEffect(showUptime) {
            while (showUptime) {
                uptimeService?.let {
                    val uptimeMillis = it.getUptimeMillis()
                    val uptimeSeconds = uptimeMillis / 1000
                    hours = uptimeSeconds / 3600
                    minutes = (uptimeSeconds % 3600) / 60
                    seconds = uptimeSeconds % 60
                }
                delay(1000)
            }
        }

        Column {
            Button(onClick = { showUptime = !showUptime }) {
                Text(if (showUptime) "Hide BootTime" else "Show BootTime")
            }

            if (showUptime) {
                Text("Time since boot...")
                Text("Hours: $hours")
                Text("Minutes: $minutes")
                Text("Seconds: $seconds")
            }
        }
    }
}
