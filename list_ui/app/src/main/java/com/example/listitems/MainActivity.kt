package com.example.listitems

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val floatingActionButton = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        floatingActionButton.setOnClickListener {
            ItemDialog().show(supportFragmentManager, "ItemDialog")
        }

        val adapter = CustomAdapter(viewModel)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        viewModel.itemsListData.observe(this) {
            //adapter.notifyDataSetChanged()
            when(viewModel.itemsEvent) {
                ItemEvent.ADD -> adapter.notifyItemInserted(viewModel.itemsEventPos)
                ItemEvent.UPDATE -> adapter.notifyItemChanged(viewModel.itemsEventPos)
                ItemEvent.DELETE -> adapter.notifyItemRemoved(viewModel.itemsEventPos)
            }
        }

        viewModel.itemClickEvent.observe(this) {
            ItemDialog(it).show(supportFragmentManager, "ItemDialog")
        }

        registerForContextMenu(recyclerView)
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.item_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete -> viewModel.deleteItem(viewModel.itemLongClick)
            R.id.edit -> viewModel.itemClickEvent.value = viewModel.itemLongClick
            else -> return false
        }
        return true
    }
}

