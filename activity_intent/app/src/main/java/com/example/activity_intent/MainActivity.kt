package com.example.activity_intent

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    private var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.i(TAG, "$localClassName.onCreate")

        findViewById<Button>(R.id.buttonSecondActivity)?.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.buttonDialActivity)?.setOnClickListener {
            val implicitIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:114"))
            startActivity(implicitIntent)
        }

        val activityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val msg = it.data?.getStringExtra("ResultString") ?: ""
            Snackbar.make(findViewById(R.id.buttonThirdActivity), "ActivityResult:${it.resultCode} $msg", Snackbar.LENGTH_SHORT).show()
            Log.i(TAG, "ActivityResult:${it.resultCode} $msg")
        }

        findViewById<Button>(R.id.buttonThirdActivity)?.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            intent.putExtra("UserDefinedExtra", "Hello")
            activityResult.launch(intent)
        }

        // ViewModel
        val viewModel = ViewModelProvider(this)[MyViewModel::class.java]
        val viewModel2 = ViewModelProvider(this, MyViewModel2Factory(10))[MyViewModel2::class.java]

        findViewById<TextView>(R.id.textView_count)?.text = getString(R.string.count_in_activity, count)
        findViewById<TextView>(R.id.textView_count_viewmodel)?.text = getString(R.string.count_in_ViewModel, viewModel.count)
        viewModel.countLivedata.observe(this) {
            findViewById<TextView>(R.id.textView_livedata)?.text = getString(R.string.count_in_ViewModel_LiveData, it)
        }

        findViewById<Button>(R.id.button_incr)?.setOnClickListener {
            count++
            viewModel.increaseCount()
            findViewById<TextView>(R.id.textView_count)?.text = getString(R.string.count_in_activity, count)
            findViewById<TextView>(R.id.textView_count_viewmodel)?.text = getString(R.string.count_in_ViewModel, viewModel.count)
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

    companion object {
        private const val TAG = "ActivityLifeCycle"
        private const val request_code = 0
    }
}