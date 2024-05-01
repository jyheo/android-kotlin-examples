package com.example.storingdata

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.asLiveData

class ShowValueDialog(private val msg: String) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext()).apply {
            setMessage(msg)
            setPositiveButton("Ok") { _, _ -> }
        }.create()
    }
}

class MyPrefKey {
    companion object {
        val name_key = stringPreferencesKey("name")
        val student_id_key = intPreferencesKey("student_id")

    }
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MyViewModel> { MyViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val textViewSettings = findViewById<TextView>(R.id.textViewSettings)
        val editText = findViewById<EditText>(R.id.editText)
        val buttonWriteIn = findViewById<Button>(R.id.buttonWriteIn)
        val buttonReadIn = findViewById<Button>(R.id.buttonReadIn)
        val buttonWriteExt = findViewById<Button>(R.id.buttonWriteExt)
        val buttonReadExt = findViewById<Button>(R.id.buttonReadExt)
        val buttonDatastorePref = findViewById<Button>(R.id.buttonDataStorePref)

        buttonWriteIn.setOnClickListener {
            viewModel.valueInternal = editText.text.toString()
        }

        buttonReadIn.setOnClickListener {
            ShowValueDialog(viewModel.valueInternal).show(supportFragmentManager, "ShowValueDialog")
        }

        buttonWriteExt.setOnClickListener {
            viewModel.valueExternal = editText.text.toString()

        }
        buttonReadExt.setOnClickListener {
            ShowValueDialog(viewModel.valueExternal).show(supportFragmentManager, "ShowValueDialog")
        }

        buttonDatastorePref.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        dataStore.data.asLiveData().observe(this) {
            val str = "name: ${it[MyPrefKey.name_key] ?: ""}   student id: ${it[MyPrefKey.student_id_key] ?: 0}"
            textViewSettings.text = str
        }
    }
}


