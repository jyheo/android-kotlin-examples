package com.example.activity_intent

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MyViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    // 1. 키(Key) 정의
    private val COUNT_KEY = "count"
    // 2. SavedStateHandle에서 값을 가져오거나 기본값 설정
    // StateFlow로 변환하여 Compose에서 관찰 가능하게 만듭니다.
    val countStateFlow: StateFlow<Int> = savedStateHandle.getStateFlow(COUNT_KEY, 0)

    init {
        Log.i("MyViewModel", "MyViewModel created!")
    }

    fun increaseCount() {
        // 3. 값 업데이트: 자동으로 저장소(Saved State)에 기록됩니다.
        val currentCount = savedStateHandle.get<Int>(COUNT_KEY) ?: 0
        savedStateHandle[COUNT_KEY] = currentCount + 1
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("MyViewModel", "MyViewModel destroyed!")
    }
}

class MyViewModel2(initialCount: Int) : ViewModel() {
    private val _countStateFlow = MutableStateFlow(initialCount)
    val countStateFlow: StateFlow<Int> = _countStateFlow.asStateFlow()

    fun increaseCount() {
        _countStateFlow.value += 1
    }
}

class MyViewModel2Factory(private val count: Int) : ViewModelProvider.Factory {
    override fun <T :  ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MyViewModel2(count) as T
    }
}