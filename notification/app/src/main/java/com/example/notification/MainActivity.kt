package com.example.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            requestSinglePermission(Manifest.permission.POST_NOTIFICATIONS)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Android 8.0
            createNotificationChannel()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_notification -> showNotification()
            R.id.show_noti_bigtext -> showNotificationBigText()
            R.id.show_noti_bigpicture -> showNotificationBigPicture()
            R.id.show_noti_progress -> showNotificationProgress()
            R.id.show_noti_button -> showNotificationButton()
            R.id.show_noti_reg_activity -> showNotificationRegularActivity()
            R.id.show_noti_special_activity -> showNotificationSpecialActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun requestSinglePermission(permission: String) {
        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED)
            return

        val requestPermLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it == false) { // permission is not granted!
                AlertDialog.Builder(this).apply {
                    setTitle("Warning")
                    setMessage(getString(R.string.no_permission, permission))
                }.show()
            }
        }

        if (shouldShowRequestPermissionRationale(permission)) {
            // you should explain the reason why this app needs the permission.
            AlertDialog.Builder(this).apply {
                setTitle("Reason")
                setMessage(getString(R.string.req_permission_reason, permission))
                setPositiveButton("Allow") { _, _ -> requestPermLauncher.launch(permission) }
                setNegativeButton("Deny") { _, _ -> }
            }.show()
        } else {
            // should be called in onCreate()
            requestPermLauncher.launch(permission)
        }
    }

    private val channelID = "default"

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
                channelID, "default channel",
                NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "description text of this channel."
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private var myNotificationID = 1
        get() = field++

    private fun showNotification() {
        val builder = NotificationCompat.Builder(this, channelID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notification Title")
                .setContentText("Notification body")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this)
                .notify(myNotificationID, builder.build())
        }
    }

    private fun showNotificationBigText() {
        val builder = NotificationCompat.Builder(this, channelID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notification Title")
                .setContentText("Notification body")
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(resources.getString(R.string.long_notification_body)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this)
                .notify(myNotificationID, builder.build())
        }
    }

    private fun showNotificationBigPicture() {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.android_hsu)
        val builder = NotificationCompat.Builder(this, channelID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(bitmap)
                .setContentTitle("Notification Title")
                .setContentText("Notification body")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(null as Icon?))  // hide largeIcon while expanding
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this)
                .notify(myNotificationID, builder.build())
        }
    }

    private fun showNotificationButton() {
        val intent = Intent(this, TestActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(this, channelID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notification Title")
                .setContentText("Notification body")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(R.drawable.android_hsu, "Action", pendingIntent)
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this)
                .notify(myNotificationID, builder.build())
        }
    }

    private fun showNotificationProgress() {
        val progressNotificationID = myNotificationID
        val builder = NotificationCompat.Builder(this, channelID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Progress")
                .setContentText("In progress")
                .setProgress(100, 0, false)
                .setPriority(NotificationCompat.PRIORITY_LOW)  // need to change channel importance LOW for Android 8.0 or higher.
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this)
                .notify(progressNotificationID, builder.build())
        }

        CoroutineScope(Dispatchers.Default).apply {
            launch {
                for (i in (1..100).step(10)) {
                    delay(1000)
                    builder.setProgress(100, i, false)
                    NotificationManagerCompat.from(applicationContext)
                        .notify(progressNotificationID, builder.build())
                }
                builder.setContentText("Completed")
                    .setProgress(0, 0, false)
                NotificationManagerCompat.from(applicationContext)
                    .notify(progressNotificationID, builder.build())
            }
        }
    }

    private fun showNotificationRegularActivity() {
        val intent = Intent(this, SecondActivity::class.java)
        val pendingIntent = with (TaskStackBuilder.create(this)) {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }
        val builder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Notification Title")
            .setContentText("Notification body")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // auto remove this notification when user touches it
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this)
                .notify(myNotificationID, builder.build())
        }
    }

    private fun showNotificationSpecialActivity() {
        val intent = Intent(this, TempActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Notification Title")
            .setContentText("Notification body")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // auto remove this notification when user touches it
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(this)
                .notify(myNotificationID, builder.build())
        }
    }
}
