package com.example.navigationui

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val myViewModel: MyViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myViewModel.nameLiveData.observe(viewLifecycleOwner) {
            view.findViewById<TextView>(R.id.textViewName)?.text = it
        }
        // popup menu
        view.findViewById<TextView>(R.id.textViewPopup)?.setOnClickListener {
            showPopup(it)
        }
    }

    private fun showPopup(v: View) {
        PopupMenu(requireContext(), v).apply {
            inflate(R.menu.nav_menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.homeFragment -> {
                        Snackbar.make(v, "HomeFragment", Snackbar.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
        }.show()
    }
}

class Page1Fragment : Fragment(R.layout.fragment_page1) {
    private val myViewModel: MyViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myViewModel.nameLiveData.observe(viewLifecycleOwner) {
            view.findViewById<TextView>(R.id.textViewName)?.text = it
        }
        view.findViewById<Button>(R.id.button)?.setOnClickListener {
            findNavController().navigate(R.id.action_page1Fragment_to_page2Fragment)
        }
    }
}

class Page2Fragment : Fragment(R.layout.fragment_page2) {
    private val myViewModel: MyViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myViewModel.nameLiveData.observe(viewLifecycleOwner) {
            view.findViewById<TextView>(R.id.textViewName)?.text = it
        }
    }
}

class Page3Fragment : Fragment(R.layout.fragment_page3) {
    private val myViewModel: MyViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myViewModel.nameLiveData.observe(viewLifecycleOwner) {
            view.findViewById<TextView>(R.id.textViewName)?.text = it
        }
    }
}

class OkCancelDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        return AlertDialog.Builder(requireContext()).apply {
            setMessage("OK-CANCEL Dialog")
            setPositiveButton("OK") { dialog, id -> println("OK")}
            setNegativeButton("CANCEL") { dialog, id -> println("CANCEL")}
        }.create()
    }
}

class DatePickerFragment : DialogFragment(),  DatePickerDialog.OnDateSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return DatePickerDialog(requireContext(), this,
                2021, 4, 28) // Jan: 0, Feb:1, Mar:2, Apr: 3 ...
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        println("$year, $month, $dayOfMonth")
    }
}

class MyBottomSheetDialog : BottomSheetDialogFragment() {
    private val myViewModel: MyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        isCancelable = false
        return inflater.inflate(R.layout.my_bottom_dialog, container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<EditText>(R.id.editTextName)?.setText(myViewModel.nameLiveData.value)

        view.findViewById<Button>(R.id.buttonOk)?.setOnClickListener {
            myViewModel.nameLiveData.value = view.findViewById<EditText>(R.id.editTextName)?.text.toString()
            dismiss()
        }

        view.findViewById<Button>(R.id.buttonCancel)?.setOnClickListener {
            dismiss()
        }

    }
}