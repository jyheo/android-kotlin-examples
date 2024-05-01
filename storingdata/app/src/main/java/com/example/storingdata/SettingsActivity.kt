package com.example.storingdata

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val name = findViewById<EditText>(R.id.editText)
        val student_id = findViewById<EditText>(R.id.editText2)
        dataStore.data.asLiveData().observe(this) {
            name.setText(it[MyPrefKey.name_key] ?: "")
            student_id.setText((it[MyPrefKey.student_id_key] ?: 0).toString())
        }

        findViewById<Button>(R.id.button).setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                dataStore.edit { settings ->
                    settings[MyPrefKey.name_key] = name.text.toString()
                    settings[MyPrefKey.student_id_key] = try {
                            student_id.text.toString().toInt()
                        } catch (e : NumberFormatException) {
                            0
                        }
                }
            }
            finish()
        }

    }
}