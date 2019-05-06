package com.arctouch.codechallenge.scenes.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _cachedQuery = MutableLiveData<String>()
    val cachedQuery: LiveData<String> = _cachedQuery

    fun updateCachedValue(newValue: String?) {
        newValue?.let { if (it.isNotBlank()) _cachedQuery.postValue(it) }
    }

    fun clearCache() {
        _cachedQuery.postValue("")
    }

}