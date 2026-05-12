package com.example.backgroundtasks

import android.os.Bundle
import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.backgroundtasks.ui.theme.BackgroundTasksTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BackgroundTasksTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainScreen()
                }
            }
        }
    }

    @Composable
    fun MainScreen() {
        // --- Permission Logic ---
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else {
            null
        }

        var showRationale by remember { mutableStateOf(false) }
        var showWarning by remember { mutableStateOf(false) }

        val requestPermLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (!isGranted) showWarning = true
        }

        LaunchedEffect(Unit) {
            permission?.let {
                if (checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED) {
                    if (shouldShowRequestPermissionRationale(it)) {
                        showRationale = true
                    } else {
                        requestPermLauncher.launch(it)
                    }
                }
            }
        }

        if (showRationale && permission != null) {
            AlertDialog(
                onDismissRequest = { showRationale = false },
                title = { Text("Reason") },
                text = { Text(getString(R.string.req_permission_reason, permission)) },
                confirmButton = {
                    TextButton(onClick = {
                        showRationale = false
                        requestPermLauncher.launch(permission)
                    }) { Text("Allow") }
                },
                dismissButton = {
                    TextButton(onClick = { showRationale = false }) { Text("Deny") }
                }
            )
        }

        if (showWarning && permission != null) {
            AlertDialog(
                onDismissRequest = { showWarning = false },
                title = { Text("Warning") },
                text = { Text(getString(R.string.no_permission, permission)) },
                confirmButton = {
                    TextButton(onClick = { showWarning = false }) { Text("OK") }
                }
            )
        }
        // -------------------------

        val workInfos by WorkManager.getInstance(applicationContext)
            .getWorkInfosForUniqueWorkLiveData(MyWorker.name)
            .observeAsState(initial = emptyList())

        val workerStatus = remember(workInfos) {
            workInfos.firstOrNull()?.state?.name ?: "UNKNOWN"
        }

        var serviceCount by remember { mutableStateOf(0) }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Worker Status: $workerStatus", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Service Count: $serviceCount", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { startForegroundService() }) {
                Text("Start Service")
            }
            Button(onClick = { serviceCount = myService?.startedCount ?: 0 }) {
                Text("Get Started Count")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { startWorker() }) {
                Text("Start Worker")
            }
            Button(onClick = { stopWorker() }) {
                Text("Stop Worker")
            }
        }
    }

    private fun startForegroundService() {
        Intent(this, MyService::class.java).also {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(it)
            } else {
                startService(it)
            }
        }
    }

    private fun startWorker() {
        val constraints = Constraints.Builder().apply {
            setRequiredNetworkType(NetworkType.UNMETERED) // un-metered network such as WiFi
            setRequiresBatteryNotLow(true)
            //setRequiresCharging(true)
            // setRequiresDeviceIdle(true) // android 6.0(M) or higher
        }.build()

        /*val repeatingRequest = PeriodicWorkRequestBuilder<MyWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            MyWorker.name,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest)*/

        val oneTimeRequest = OneTimeWorkRequestBuilder<MyWorker>()
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(this).enqueueUniqueWork(
            MyWorker.name, ExistingWorkPolicy.KEEP, oneTimeRequest)

    }

    private fun stopWorker() {
        // to stop the MyWorker
        WorkManager.getInstance(this).cancelUniqueWork(MyWorker.name)
    }



    private var myService: MyService? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            myService = (service as MyService.LocalBinder).getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            myService = null
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, MyService::class.java).also {
            bindService(it, serviceConnection, BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }
}