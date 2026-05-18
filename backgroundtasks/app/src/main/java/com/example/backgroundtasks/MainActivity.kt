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
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.backgroundtasks.ui.theme.BackgroundTasksTheme

class MainActivity : ComponentActivity() {

    private var myService: MyService? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            myService = (service as MyService.LocalBinder).getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            myService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BackgroundTasksTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                        RequestPermission()
                    MainScreen(
                        onStartService = { startForegroundService() },
                        onGetServiceCount = { myService?.startedCount ?: 0 },
                        onStartWorker = { startWorker() },
                        onStopWorker = { stopWorker() }
                    )
                }
            }
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
            setRequiredNetworkType(NetworkType.UNMETERED)
            setRequiresBatteryNotLow(true)
        }.build()

        val oneTimeRequest = OneTimeWorkRequestBuilder<MyWorker>()
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(this).enqueueUniqueWork(
            MyWorker.name, ExistingWorkPolicy.KEEP, oneTimeRequest)
    }

    private fun stopWorker() {
        WorkManager.getInstance(this).cancelUniqueWork(MyWorker.name)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun RequestPermission() {
    val context = LocalContext.current
    val permission = Manifest.permission.POST_NOTIFICATIONS

    var showRationale by remember { mutableStateOf(false) }
    var showWarning by remember { mutableStateOf(false) }

    val requestPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) showWarning = true
    }

    LaunchedEffect(Unit) {
        permission.let {
            if (context.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED) {
                if ((context as? ComponentActivity)?.shouldShowRequestPermissionRationale(it) == true) {
                    showRationale = true
                } else {
                    requestPermLauncher.launch(it)
                }
            }
        }
    }

    if (showRationale) {
        AlertDialog(
            onDismissRequest = { showRationale = false },
            title = { Text("Notification Permission") },
            text = { Text(stringResource(R.string.req_permission_reason, "Notifications")) },
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

    if (showWarning) {
        AlertDialog(
            onDismissRequest = { showWarning = false },
            title = { Text("Permission Denied") },
            text = { Text(stringResource(R.string.no_permission, "Notifications")) },
            confirmButton = {
                TextButton(onClick = { showWarning = false }) { Text("OK") }
            }
        )
    }
}

@Composable
fun MainScreen(
    onStartService: () -> Unit,
    onGetServiceCount: () -> Int,
    onStartWorker: () -> Unit,
    onStopWorker: () -> Unit
) {
    val context = LocalContext.current

    val workInfos by WorkManager.getInstance(context)
        .getWorkInfosForUniqueWorkFlow(MyWorker.name)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    var serviceCount by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "worker Status: ${workInfos.firstOrNull()?.state?.name ?: "UNKNOWN"}")
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Service Count: $serviceCount", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onStartService,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Start Service")
        }
        Button(
            onClick = { serviceCount = onGetServiceCount() },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Refresh Service Count")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onStartWorker,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Start Worker")
        }
        Button(
            onClick = onStopWorker,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Stop Worker")
        }
    }
}
