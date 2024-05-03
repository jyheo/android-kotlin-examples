package com.example.storingdata

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException


class MyViewModel(private val myRepository: MyRepository) : ViewModel() {
    var valueInternal = myRepository.valueInternal
        set(value) {
            field = value
            myRepository.valueInternal = value
        }
    var valueExternal = myRepository.valueExternal
        set(value) {
            field = value
            myRepository.valueExternal = value
        }
    val myPref = myRepository.myPref
    fun setPref(key: Preferences.Key<String>, value: String) = viewModelScope.launch {
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