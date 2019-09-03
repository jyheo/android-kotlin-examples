package com.example.listviewtest

import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SimpleAdapter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val items = arrayOf("item1", "item2", "item3", "item4", "item5", "item6", "item7", "item8")
        //val adapt = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items)
        val arrayAdapter = ArrayAdapter<String>(this, R.layout.item, items)
        val list1 = findViewById<ListView>(R.id.listView1)
        list1.adapter = arrayAdapter
        // ListView appearance
        list1.choiceMode = ListView.CHOICE_MODE_MULTIPLE;
        list1.divider = ColorDrawable(Color.RED)
        list1.dividerHeight = 5

        // android.R.layout.simple_list_item_2
        val mlist = arrayListOf(
            hashMapOf("item1" to "musart", "item2" to "10"),
            hashMapOf("item1" to "sasim", "item2" to "20"),
            hashMapOf("item1" to "vincent", "item2" to "30") )
        val simpleAdapter = SimpleAdapter(this, mlist, android.R.layout.simple_list_item_2,
            arrayOf("item1", "item2"), intArrayOf(android.R.id.text1, android.R.id.text2))
        findViewById<ListView>(R.id.listView2).adapter = simpleAdapter
    }
}
