package com.example.basicui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button;
import android.widget.Toast;

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        val btn = findViewById<Button>(R.id.button1)!!
        btn.setOnClickListener {
            Toast.makeText(this, R.string.button_clicked_msg, Toast.LENGTH_SHORT).show()
        }

        val btn4 = findViewById<Button>(R.id.button4)!!
        btn4.setOnClickListener {
            val btn = findViewById<Button>(R.id.button1)
            if (btn.text == "Button 1")
                btn.text = "Button One"
            else
                btn.text = "Button 1"
        }
    }
}
