package com.example.colormaker

import android.app.Application
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import androidx.lifecycle.ViewModel

data class ColorState(
    val red: Float,
    val green: Float,
    val blue: Float,
    val redOn: Boolean,
    val greenOn: Boolean,
    val blueOn: Boolean
)

class ColorViewModel : ViewModel() {
    var _red = MutableStateFlow(0f)
    var _green = MutableStateFlow(0f)
    var _blue = MutableStateFlow(0f)

    private var _redOn = MutableStateFlow(false)
    private var _greenOn = MutableStateFlow(false)
    private var _blueOn = MutableStateFlow(false)

    //immutable public observers
    var red: StateFlow<Float> = _red
    var green: StateFlow<Float> = _green
    var blue: StateFlow<Float> = _blue

    val redOn: StateFlow<Boolean> = _redOn
    val greenOn: StateFlow<Boolean> = _greenOn
    val blueOn: StateFlow<Boolean> = _blueOn


    //ovserve to connect flowStates to MainActivity
    fun observeState() = combine(
        red, green, blue, redOn, greenOn, blueOn
    ) { r, g, b, ro, go, bo ->
        ColorState(r, g, b, ro, go, bo)
    }

    //setters, update values
    fun setRed(value: Float) {
        _red.value = value
    }

    fun setGreen(value: Float) {
        _green.value = value
    }

    fun setBlue(value: Float) {
        _blue.value = value
    }

    fun setRedOn(on: Boolean) {
        _redOn.value = on
    }

    fun setGreenOn(on: Boolean) {
        _greenOn.value = on
    }

    fun setBlueOn(on: Boolean) {
        _blueOn.value = on
    }
}
