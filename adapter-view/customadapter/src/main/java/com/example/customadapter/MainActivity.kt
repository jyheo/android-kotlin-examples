package com.example.customadapter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast

import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // preparing data
        val data = ArrayList<MyItem>()
        data.add(MyItem(R.drawable.sample_0, "Bella", "1"))
        data.add(MyItem(R.drawable.sample_1, "Charlie", "2"))
        data.add(MyItem(R.drawable.sample_2, "Daisy", "1.5"))
        data.add(MyItem(R.drawable.sample_3, "Duke", "1"))
        data.add(MyItem(R.drawable.sample_4, "Max", "2"))
        data.add(MyItem(R.drawable.sample_5, "Happy", "4"))
        data.add(MyItem(R.drawable.sample_6, "Luna", "3"))
        data.add(MyItem(R.drawable.sample_7, "Bob", "2"))

        // creating adapter
        val myAdapter = MyAdapter(this, R.layout.item, data)

        // connecting adapter to ListView
        val listView = findViewById<ListView>(R.id.listView)
        listView.adapter = myAdapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val name = (myAdapter.getItem(position) as MyItem).nName
            Toast.makeText(this@MainActivity, "${name?:""} selected", Toast.LENGTH_SHORT).show()
        }
    }

}
