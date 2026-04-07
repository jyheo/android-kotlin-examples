package com.example.activity_intent

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activity_intent.ui.theme.ActivityIntentTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "$localClassName.onCreate")
        setContent {
            ActivityIntentTheme {
                MainScreen()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "$localClassName.onStart")
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(TAG, "$localClassName.onRestart")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "$localClassName.onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "$localClassName.onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "$localClassName.onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "$localClassName.onDestroy")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.i(TAG, "$localClassName.onConfigurationChanged")
    }

    companion object {
        private const val TAG = "ActivityLifeCycle"
    }
}

@Composable
fun MainScreen(
    myViewModel: MyViewModel = viewModel(),
    myViewModel2: MyViewModel2 = viewModel(factory = MyViewModel2Factory(10))
) {
    val countState by myViewModel.countStateFlow.collectAsStateWithLifecycle()

    MainScreenContent(
        countState = countState,
        onIncreaseCount = { myViewModel.increaseCount() }
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    ActivityIntentTheme {
        MainScreenContent(
            countState = 0,
            onIncreaseCount = {}
        )
    }
}

@Composable
fun MainScreenContent(
    countState: Int,
    onIncreaseCount: () -> Unit
) {
    val context = LocalContext.current
    var resultText by remember { mutableStateOf("") }

    var count by remember { mutableIntStateOf(0) }

    val activityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val msg = result.data?.getStringExtra("ResultString") ?: ""
        resultText = "ActivityResult:${result.resultCode} $msg"
        Log.i("ActivityLifeCycle", resultText)
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (resultText.isNotEmpty()) {
                Text(text = resultText)
            }

            Button(onClick = {
                val intent = Intent(context, SecondActivity::class.java)
                context.startActivity(intent)
            }) {
                Text(text = stringResource(id = R.string.start_second_activity))
            }

            Button(onClick = {
                val implicitIntent = Intent(Intent.ACTION_DIAL, "tel:114".toUri())
                val chooser = Intent.createChooser(implicitIntent, "Title")
                context.startActivity(chooser)
            }) {
                Text(text = stringResource(id = R.string.start_dial_activity))
            }

            Button(onClick = {
                val intent = Intent(context, ThirdActivity::class.java).apply {
                    putExtra("UserDefinedExtra", "Hello")
                }
                activityResultLauncher.launch(intent)
            }) {
                Text(text = stringResource(id = R.string.start_third_activity_with_extra_data))
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column {
                    Text(text = stringResource(id = R.string.count_in_activity, count))
                    Text(text = stringResource(id = R.string.count_in_ViewModel, countState))
                }
                Button(onClick = {
                    count++
                    onIncreaseCount()
                }) {
                    Text(text = stringResource(id = R.string.Increase))
                }
            }
        }
    }
}