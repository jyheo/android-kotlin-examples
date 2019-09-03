package com.example.activityintent

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button


class FirstActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ActivityLifeCycle"
        private const val request_code = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
        Log.i(TAG, getLocalClassName() + ".onCreate")

        findViewById<Button>(R.id.buttonSecondActivity)!!.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.buttonDialActivity).setOnClickListener {
            val implicit = Intent(Intent.ACTION_DIAL, Uri.parse("tel:114"))
            startActivity(implicit)
        }

        findViewById<Button>(R.id.buttonThirdActivity).setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            intent.putExtra("UserDefinedExtra", "Hello")
            startActivityForResult(intent, request_code)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != request_code || data == null)
            return
        val msg = data.getStringExtra("ResultString")
        Log.i(TAG, "ActivityResult:$resultCode $msg")
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG,"${localClassName}.onStart")
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
