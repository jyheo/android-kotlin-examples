package com.example.fragment_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController


class HomeFragment : Fragment(R.layout.home_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.button)?.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_nav1Fragment)
        }
    }
}

class Nav1Fragment : Fragment(R.layout.nav1_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.button2)?.setOnClickListener {
            findNavController().navigate(R.id.action_nav1Fragment_to_nav2Fragment)
        }
    }
}

class Nav2Fragment : Fragment(R.layout.nav2_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.button3)?.setOnClickListener {
            findNavController().navigate(R.id.action_nav2Fragment_to_homeFragment)
        }
    }
}

class NavActivity : AppCompatActivity(R.layout.activity_nav)