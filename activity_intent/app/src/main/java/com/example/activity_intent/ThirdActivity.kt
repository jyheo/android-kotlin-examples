package com.example.activity_intent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.activity.addCallback

class ThirdActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)
        val msg: String = intent?.getStringExtra("UserDefinedExtra") ?: ""

        findViewById<EditText>(R.id.editText)?.setText(msg)

        onBackPressedDispatcher.addCallback(this, true) {
                val et = findViewById<EditText>(R.id.editText)
                val resultIntent = Intent()
                resultIntent.putExtra("ResultString", et.text.toString())
                setResult(RESULT_OK, resultIntent)
                finish()
        }
    }
}