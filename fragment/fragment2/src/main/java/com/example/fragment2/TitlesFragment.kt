package com.example.fragment2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class TitlesFragment : Fragment() {

    private var mCurCheckPosition = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_titles, container, false)
    }

    interface OnTitleSelectedListener {
        fun onTitleSelected(i: Int, restoreSaved: Boolean)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val lv = view!!.findViewById<ListView>(R.id.listview)

        lv.adapter = ArrayAdapter(activity,
            android.R.layout.simple_list_item_activated_1, Shakespeare.TITLES
        )
        lv.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            mCurCheckPosition = i
            (activity as OnTitleSelectedListener).onTitleSelected(i, false)
        }

        lv.choiceMode = ListView.CHOICE_MODE_SINGLE

        if (savedInstanceState != null) {
            mCurCheckPosition = savedInstanceState.getInt("curChoice", -1)
            if (mCurCheckPosition >= 0)
                (activity as OnTitleSelectedListener).onTitleSelected(mCurCheckPosition, true)
        }

        lv.setSelection(mCurCheckPosition)
        lv.smoothScrollToPosition(mCurCheckPosition)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("curChoice", mCurCheckPosition)
    }

}

