package com.example.activityintent

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ActivityLifeCycle"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        Log.i(TAG, "${localClassName}.onCreate")
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "${localClassName}.onStart")
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(TAG, "${localClassName}.onRestart")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "${localClassName}.onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "${localClassName}.onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "${localClassName}.onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "${localClassName}.onDestroy")
    }
}
