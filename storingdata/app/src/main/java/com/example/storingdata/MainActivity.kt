package com.example.storingdata

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit

class ShowValueDialog(private val msg: String) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext()).apply {
            setMessage(msg)
            setPositiveButton("Ok") { _, _ -> }
        }.create()
    }
}

class MainFragment : Fragment(R.layout.fragment_main) {
    private val viewModel by activityViewModels<MyViewModel> { MyViewModelFactory(requireContext()) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textViewSettings = view.findViewById<TextView>(R.id.textViewSettings)
        val editText = view.findViewById<EditText>(R.id.editText)
        val buttonWriteIn = view.findViewById<Button>(R.id.buttonWriteIn)
        val buttonReadIn = view.findViewById<Button>(R.id.buttonReadIn)
        val buttonWriteExt = view.findViewById<Button>(R.id.buttonWriteExt)
        val buttonReadExt = view.findViewById<Button>(R.id.buttonReadExt)
        val buttonDatastorePref = view.findViewById<Button>(R.id.buttonDataStorePref)

        buttonWriteIn.setOnClickListener {
            viewModel.valueInternal = editText.text.toString()
        }

        buttonReadIn.setOnClickListener {
            ShowValueDialog(viewModel.valueInternal).show(parentFragmentManager, "ShowValueDialog")
        }

        buttonWriteExt.setOnClickListener {
            viewModel.valueExternal = editText.text.toString()

        }
        buttonReadExt.setOnClickListener {
            ShowValueDialog(viewModel.valueExternal).show(parentFragmentManager, "ShowValueDialog")
        }

        buttonDatastorePref.setOnClickListener {
            parentFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.fragment, SettingsFragment::class.java, null)
                addToBackStack(null)
            }
        }

        viewModel.myPref.observe(viewLifecycleOwner) {
            val str = "name: ${it[MyPrefKey.name_key] ?: ""}   student id: ${it[MyPrefKey.student_id_key] ?: 0}"
            textViewSettings.text = str
        }
    }
}

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MyViewModel> { MyViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }
}


