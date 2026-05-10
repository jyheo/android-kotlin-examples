package com.example.datastorage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.datastorage.ui.theme.DataStorageTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DataStorageTheme {
                val navController = rememberNavController()
                val viewModel: MyViewModel = viewModel(factory = MyViewModelFactory(LocalContext.current))

                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(
                            viewModel = viewModel,
                            onNavigateToSettings = { navController.navigate("settings") },
                            onNavigateToRoom = { navController.navigate("room") }
                        )
                    }
                    composable("settings") {
                        SettingsScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                    }
                    composable("room") {
                        RoomScreen(viewModel = viewModel, onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MyViewModel, onNavigateToSettings: () -> Unit, onNavigateToRoom: () -> Unit) {
    val myPref by viewModel.myPref.collectAsStateWithLifecycle(initialValue = null)

    var inputText by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    // Dialog logic
    if (showDialog && dialogMessage != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Ok")
                }
            },
            text = { Text(dialogMessage!!) }
        )
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Settings Text (DataStore)
            val prefText = myPref?.let {
                "name: ${it[MyPrefKey.name_key] ?: ""}   student id: ${it[MyPrefKey.student_id_key] ?: 0}"
            } ?: "Loading preferences..."

            Text(text = prefText, style = MaterialTheme.typography.bodyLarge)

            // Edit Text
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Input Value") },
                modifier = Modifier.fillMaxWidth()
            )

            // Internal Storage Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { viewModel.valueInternal = inputText }) {
                    Text("Write Internal")
                }
                Button(onClick = {
                    dialogMessage = viewModel.valueInternal
                    showDialog = true
                }) {
                    Text("Read Internal")
                }
            }

            // External Storage Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { viewModel.valueExternal = inputText }) {
                    Text("Write External")
                }
                Button(onClick = {
                    dialogMessage = viewModel.valueExternal
                    showDialog = true
                }) {
                    Text("Read External")
                }
            }

            // Settings Button
            Button(
                onClick = onNavigateToSettings,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("DataStore Preferences")
            }

            // Room Button
            Button(
                onClick = onNavigateToRoom,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Room Database")
            }
        }
    }
}

@Composable
fun SettingsScreen(viewModel: MyViewModel, onBack: () -> Unit) {
    val myPref by viewModel.myPref.collectAsStateWithLifecycle(initialValue = null)

    var name by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }

    // key1이 변경되거나 컴포저블이 처음 화면에 나타달 때 실행
    LaunchedEffect(myPref) {
        myPref?.let {
            name = it[MyPrefKey.name_key] ?: ""
            studentId = it[MyPrefKey.student_id_key] ?: ""
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)
                            .padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = studentId, onValueChange = { studentId = it }, label = { Text("Student ID") }, modifier = Modifier.fillMaxWidth())
            Button(onClick = {
                viewModel.setPref(MyPrefKey.name_key, name)
                viewModel.setPref(MyPrefKey.student_id_key, studentId)
                onBack()
            }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Save and Back")
            }
        }
    }
}

@Composable
fun RoomScreen(viewModel: MyViewModel, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()

    val allStudents by viewModel.allStudents.collectAsStateWithLifecycle(initialValue = emptyList())

    var studentIdInput by remember { mutableStateOf("") }
    var studentNameInput by remember { mutableStateOf("") }
    var queryResultText by remember { mutableStateOf("") }

    // Initial Data Setup
    LaunchedEffect(Unit) {
        viewModel.initialSetup()
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Student List", style = MaterialTheme.typography.headlineSmall)

            // Student List Display
            Card(modifier = Modifier.fillMaxWidth()) {
                val listText = allStudents.joinToString("\n") { "${it.id}-${it.name}" }
                Text(
                    text = listText.ifEmpty { "No students" },
                    modifier = Modifier.padding(8.dp)
                )
            }

            HorizontalDivider()

            // Inputs
            OutlinedTextField(
                value = studentIdInput,
                onValueChange = { studentIdInput = it },
                label = { Text("Student ID") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = studentNameInput,
                onValueChange = { studentNameInput = it },
                label = { Text("Student Name") },
                modifier = Modifier.fillMaxWidth()
            )

            // Action Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    val id = studentIdInput.toIntOrNull() ?: 0
                    val name = studentNameInput
                    if (id > 0 && name.isNotEmpty()) {
                        viewModel.insertStudent(id, name)
                    }
                }) {
                    Text("Add Student")
                }

                Button(onClick = {
                    val id = studentIdInput.toIntOrNull() ?: 0
                    scope.launch {
                        queryResultText = viewModel.getStudentQuery(id)
                    }
                }) {
                    Text("Query Student")
                }
            }

            // Query Result Area
            Text(
                text = queryResultText,
                color = Color.Blue,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Back to Home")
            }
        }
    }
}
