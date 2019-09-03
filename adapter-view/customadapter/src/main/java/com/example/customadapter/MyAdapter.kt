package com.example.customadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

import java.util.ArrayList
import java.util.zip.Inflater

/**
 * Created by Kwanwoo on 2016-09-05.
 * Kotlin version by Jyheo on 2019-09-03.
 */

internal class MyItem(
    var mIcon: Int // image resource
    , var nName: String? // text
    , var nAge: String?  // text
)

internal class MyAdapter(
    private val mContext: Context,
    private val mResource: Int,
    private val mItems: ArrayList<MyItem>
) : BaseAdapter() {

    override fun getCount(): Int {
        return mItems.size
    }

    override fun getItem(position: Int): Any {
        return mItems[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            val inflater =
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            itemView = inflater.inflate(mResource, parent, false)
        }
        // Set Icon
        itemView!!.findViewById<ImageView>(R.id.iconItem).setImageResource(mItems[position].mIcon)

        // Set Text 01
        itemView.findViewById<TextView>(R.id.textItem1).text = mItems[position].nName

        // Set Text 02
        itemView.findViewById<TextView>(R.id.textItem2).text = mItems[position].nAge

        return itemView
    }
}
