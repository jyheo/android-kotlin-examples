package com.example.fragment2


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class DetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        val i = arguments!!.getInt("index", 0)
        val tv = view.findViewById<TextView>(R.id.textview)
        tv.text = Shakespeare.DIALOGUE[i]

        return view
    }

    companion object {

        fun newInstance(index: Int): DetailsFragment {
            val f = DetailsFragment()

            // Supply index input as an argument.
            val args = Bundle()
            args.putInt("index", index)
            f.arguments = args

            return f
        }
    }
}
