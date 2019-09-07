package com.example.fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class FragSwitchActivity : AppCompatActivity() {

    private val firstFragment = FirstFragment()
    private val secondFragment = SecondFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frag_switch)

        findViewById<Button>(R.id.button_first).setOnClickListener {
            switchFragment(0)
        }
        findViewById<Button>(R.id.button_second).setOnClickListener {
            switchFragment(1)
        }
    }

    private fun switchFragment(id: Int) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        if (id == 0)
            fragmentTransaction.replace(R.id.fragment, firstFragment)
        else
            fragmentTransaction.replace(R.id.fragment, secondFragment)
        fragmentTransaction.commit()
    }
}
