package com.example.listitems

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ItemDialog(private val itemPos: Int = -1): BottomSheetDialogFragment() {
    private val viewModel by activityViewModels<MyViewModel>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.item_dialog_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val spinner = view.findViewById<Spinner>(R.id.spinner)
        val editTextFirstName = view.findViewById<EditText>(R.id.editTextFirstName)
        val editTextLastName = view.findViewById<EditText>(R.id.editTextLastName)
        val buttonOk = view.findViewById<Button>(R.id.buttonOk)
        val buttonCancel = view.findViewById<Button>(R.id.buttonCancel)
        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item, MyViewModel.icons.keys.toList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        if (itemPos >= 0) {
            with (viewModel.getItem(itemPos)) {
                val i = MyViewModel.icons.keys.toList().indexOf(icon)
                spinner.setSelection(i)
                editTextFirstName.setText(firstName)
                editTextLastName.setText(lastName)
            }
        }

        buttonOk.setOnClickListener {
            val item = Item(
                    spinner.selectedItem as String,
                    editTextFirstName.text.toString(),
                    editTextLastName.text.toString()
            )
            if (itemPos < 0)
                viewModel.addItem(item)
            else
                viewModel.updateItem(itemPos, item)
            dismiss()
        }

        buttonCancel.setOnClickListener {
            dismiss()
        }

    }
}