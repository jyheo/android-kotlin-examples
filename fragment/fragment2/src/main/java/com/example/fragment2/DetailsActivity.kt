package com.example.fragment2

import androidx.appcompat.app.AppCompatActivity
import android.content.res.Configuration
import android.os.Bundle

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish()
            return
        }

        val details = DetailsFragment()
        details.arguments = intent.extras
        supportFragmentManager.beginTransaction().replace(R.id.details, details).commit()
    }
}
