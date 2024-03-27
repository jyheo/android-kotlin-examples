package com.example.fragment_test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit

class ExampleFragment : Fragment(R.layout.example_fragment)

class ExampleFragment2 : Fragment(R.layout.example_fragment2)

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.buttonAdd)?.setOnClickListener {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(R.id.fragment, ExampleFragment2::class.java, null)
            }
        }

        findViewById<Button>(R.id.buttonReplace)?.setOnClickListener {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.fragment, ExampleFragment2::class.java, null)
            }
        }

        findViewById<Button>(R.id.buttonReplaceBack)?.setOnClickListener {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.fragment, ExampleFragment2::class.java, null)
                addToBackStack(null)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.viewmodel -> startActivity(Intent(this, FruitsActivity::class.java))
            R.id.navigation -> startActivity(Intent(this, NavActivity::class.java))
        }
        return true
    }
}