package com.example.storingdata

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException


class MyViewModel(private val myRepository: MyRepository) : ViewModel() {
    var valueInternal = myRepository.valueInternal
    var valueExternal = myRepository.valueExternal
    val myPref = myRepository.myPref
    fun setPref(key: Preferences.Key<String>, value: String) = CoroutineScope(Dispatchers.IO).launch {
        myRepository.setPref(key, value)
    }
}

class MyViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MyViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            MyViewModel(MyRepository(context)) as T
        else
            throw IllegalArgumentException()
    }
}