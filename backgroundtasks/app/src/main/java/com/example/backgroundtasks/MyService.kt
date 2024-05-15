package com.example.backgroundtasks

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.*

class MyService : Service() {
    private val binder = LocalBinder()
    private val notificationID = 10
    private val channelID = "service foreground channel"

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService() = this@MyService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    var startedCount = 0
        private set

    override fun onCreate() {
        super.onCreate()
        println("MyService:onCreate")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Android 8.0
            createNotificationChannel()
        }
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("MyService:onStartCommand $startId")
        startedCount++
        startForeground(notificationID, createNotification())

        CoroutineScope(Dispatchers.Default).apply {
            launch {
                delay(5000)
                for (i in 1..10) {
                    println("in service $startId#$i")
                    startForeground(notificationID, createNotification(i*10))
                    delay(5000)
                }
                stopSelf(startId)
            }
        }

        return super.onStartCommand(intent, flags, startId) // START_STICKY, NOT_STICKY, REDELIVERY_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        println("MyService:onDestroy")
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(channelID, "service channel",
            NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = "notification channel for service."
        NotificationManagerCompat.from(this).createNotificationChannel(channel)
    }

    private fun showNotification(id: Int, notification: Notification) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this).notify(id, notification)
        }
    }

    private fun createNotification(progress: Int = 0) = NotificationCompat.Builder(this, channelID)
        .setContentTitle("Downloading")
        .setContentText("Downloading a file from a cloud")
        .setSmallIcon(R.drawable.baseline_music_note_24)
        .setOnlyAlertOnce(true)  // importance 에 따라 알림 소리가 날 때, 처음에만 소리나게 함
        .setProgress(100, progress, false)
        .build()


}