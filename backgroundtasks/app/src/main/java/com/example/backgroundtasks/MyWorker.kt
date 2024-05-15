package com.example.backgroundtasks

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class MyWorker(context: Context, parameters: WorkerParameters) : CoroutineWorker(context, parameters) {
    private val notificationID = 5
    private val channelID = "worker foreground channel"

    override suspend fun doWork(): Result {
        // foreground notification
        setForeground(createForegroundInfo("Starting Download"))

        for (i in 0..100) {
            withContext(Dispatchers.IO) {
                Thread.sleep(1000)
            }
            setForeground(createForegroundInfo("Downloading $i%"))
        }
        return Result.success()
    }

    private fun createForegroundInfo(progress: String): ForegroundInfo {
        val title = applicationContext.getString(R.string.noti_title)
        val cancel = applicationContext.getString(R.string.cancel_task)
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val notification = NotificationCompat.Builder(applicationContext, channelID)
            .setContentTitle(title)
            .setContentText(progress)
            .setSmallIcon(R.drawable.baseline_music_note_24)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            ForegroundInfo(notificationID, notification, FOREGROUND_SERVICE_TYPE_SHORT_SERVICE)
        else
            ForegroundInfo(notificationID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelID, "default channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "description text of this channel."
        NotificationManagerCompat.from(applicationContext).createNotificationChannel(channel)
    }


    companion object {
        const val name = "com.example.backgroundtasks.MyWorker"
    }
}