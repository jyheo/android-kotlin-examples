package com.example.activity_intent

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MyViewModel : ViewModel() {
    var count = 0
    val countLivedata: MutableLiveData<Int> = MutableLiveData<Int>()

    init {
        countLivedata.value = 0
        Log.i("MyViewModel", "MyViewModel created!")
    }

    fun increaseCount() {
        count++
        countLivedata.value = (countLivedata.value ?: 0) + 1
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("MyViewModel", "MyViewModel destroyed!")
    }
}

class MyViewModel2(count: Int) : ViewModel() {
    var count = count
    val countLivedata: MutableLiveData<Int> = MutableLiveData<Int>()
    init {
        countLivedata.value = count
    }
    fun increaseCount() {
        count++
        countLivedata.value = (countLivedata.value ?: 0) + 1
    }
}

class MyViewModel2Factory(private val count: Int) : ViewModelProvider.Factory {
    override fun <T :  ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MyViewModel2(count) as T // the warning 'unchecked cast' is unavoidable.
    }
}