package com.example.activityintent

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText

class ThirdActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        val msg = intent.getStringExtra("UserDefinedExtra")
        findViewById<EditText>(R.id.editText).setText(msg)
    }

    override fun onBackPressed() {
        val resultString = findViewById<EditText>(R.id.editText).text.toString()
        val resultIntent = Intent()
        resultIntent.putExtra("ResultString", resultString)
        setResult(RESULT_OK, resultIntent)

        super.onBackPressed()
    }
}
