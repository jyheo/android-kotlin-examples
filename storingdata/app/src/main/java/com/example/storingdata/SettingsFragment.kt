package com.example.storingdata

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val viewModel by activityViewModels<MyViewModel> { MyViewModelFactory(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = view.findViewById<EditText>(R.id.editText)
        val student_id = view.findViewById<EditText>(R.id.editText2)

        lifecycleScope.launch {
            viewModel.myPref.collect {
                name.setText(it[MyPrefKey.name_key] ?: "")
                student_id.setText((it[MyPrefKey.student_id_key] ?: 0).toString())
            }
        }

        view.findViewById<Button>(R.id.button).setOnClickListener {
            viewModel.setPref(MyPrefKey.name_key, name.text.toString())
            viewModel.setPref(MyPrefKey.student_id_key, student_id.text.toString())
            parentFragmentManager.popBackStack()
        }
    }
}