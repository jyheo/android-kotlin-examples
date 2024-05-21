package com.example.broadcastreceivertest

import android.content.*
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private val broadcastReceiver = MyBroadcastReceiver()
    private val buttonSendMyBroad: Button by lazy { findViewById(R.id.buttonSendMyBroad) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestSinglePermission(android.Manifest.permission.RECEIVE_SMS)

        buttonSendMyBroad.setOnClickListener {
            sendBroadcast(Intent(ACTION_MY_BROADCAST))
        }
    }

    private fun startBroadcastReceiver() {
        IntentFilter().also {
            it.addAction(Intent.ACTION_POWER_CONNECTED)
            it.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
            it.addAction("android.provider.Telephony.SMS_RECEIVED")
            it.addAction(ACTION_MY_BROADCAST)
            ContextCompat.registerReceiver(this, broadcastReceiver, it, ContextCompat.RECEIVER_EXPORTED)
            // NOT_EXPORTED로 하면 앱(자신 포함)에서 보내는 방송은 못 받고 시스템이 보내는 것 만 받음
            // EXPORTED로 해야 시스템 뿐 아니라 다른 앱(문자, MY_BROADCAST)도 수신 가능
        }
    }

    override fun onStart() {
        super.onStart()
        startBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    inner class MyBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_POWER_CONNECTED -> { showBroadcast(Intent.ACTION_POWER_CONNECTED) }
                ACTION_MY_BROADCAST -> { showBroadcast(ACTION_MY_BROADCAST) }
                else -> { showBroadcast(intent?.action ?: "NO ACTION")}
            }
        }

        private fun showBroadcast(msg: String) {
            println(msg)
            Snackbar.make(buttonSendMyBroad, msg, Snackbar.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val ACTION_MY_BROADCAST = "ACTION_MY_BROADCAST"
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
}