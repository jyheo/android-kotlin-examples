package com.example.prog_ui

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val appName = resources.getString(R.string.app_name)
        println(appName)

        val imageView = findViewById<ImageView>(R.id.imageView)
        val button = findViewById<Button>(R.id.button)
        val radioDog = findViewById<RadioButton>(R.id.radioDog)
        val radioCat = findViewById<RadioButton>(R.id.radioCat)
        val textView2 = findViewById<TextView>(R.id.textView2)
        val editTextTextPersonName = findViewById<EditText>(R.id.editTextTextPersonName)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)

        imageView.scaleType = ImageView.ScaleType.CENTER
        radioGroup.check(R.id.radioDog)

        button.setOnClickListener {
            val pet = "Dog:${radioDog.isChecked}, Cat:${radioCat.isChecked}"
            textView2.text = editTextTextPersonName.text
            Snackbar.make(it, pet, Snackbar.LENGTH_SHORT).show()

            when(radioGroup.checkedRadioButtonId) {
                R.id.radioDog -> println("radioDog")
                R.id.radioCat -> println("radioCat")
            }
        }
    }

}