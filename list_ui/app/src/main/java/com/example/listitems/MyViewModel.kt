package com.example.listitems

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Item(val icon: String, val firstName: String, val lastName: String)

enum class ItemEvent { ADD, UPDATE, DELETE }

class MyViewModel : ViewModel() {
    val itemsListData = MutableLiveData<ArrayList<Item>>()
    private val items = ArrayList<Item>()
    var itemsEvent = ItemEvent.ADD
    var itemsEventPos = -1

    val itemClickEvent = MutableLiveData<Int>()
    var itemLongClick = -1

    init {
        items.add(Item("person", "Yuh-jung", "Youn"))
        items.add(Item("person", "Steven", "Yeun"))
        items.add(Item("person", "Alan", "Kim"))
        items.add(Item("person outline", "Ye-ri", "Han"))
        items.add(Item("person outline", "Noel", "Cho"))
        items.add(Item("person pin", "Lee Issac", "Chung"))
        itemsListData.value = items
    }

    fun getItem(pos: Int) =  items[pos]
    val itemsSize
        get() = items.size

    fun addItem(item: Item) {
        itemsEvent = ItemEvent.ADD
        itemsEventPos = itemsSize
        items.add(item)
        itemsListData.value = items // let the observer know the livedata changed
    }

    fun updateItem(pos: Int, item: Item) {
        itemsEvent = ItemEvent.UPDATE
        itemsEventPos = pos
        items[pos] = item
        itemsListData.value = items // 옵저버에게 라이브데이터가 변경된 것을 알리기 위해
    }

    fun deleteItem(pos: Int) {
        itemsEvent = ItemEvent.DELETE
        itemsEventPos = pos
        items.removeAt(pos)
        itemsListData.value = items
    }

    companion object {
        val icons = sortedMapOf(
            "person" to R.drawable.ic_baseline_person_24,
            "person outline" to R.drawable.ic_baseline_person_outline_24,
            "person pin" to R.drawable.ic_baseline_person_pin_24
        )
    }
}