package com.example.ajedrez.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData

class GameViewModel : ViewModel() {

    private val _gameName = MutableLiveData<String>()
    val gameName: LiveData<String> = _gameName

    private val _playerStartsFirst = MutableLiveData<Boolean>()
    val playerStartsFirst: LiveData<Boolean> = _playerStartsFirst

    fun setGameName(name: String) {
        _gameName.value = name
    }

    fun setPlayerStartsFirst(starts: Boolean) {
        _playerStartsFirst.value = starts
    }
}
