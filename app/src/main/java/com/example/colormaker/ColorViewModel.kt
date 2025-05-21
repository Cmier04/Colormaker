package com.example.colormaker

import android.app.Application
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

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
    val colorState: Flow<ColorState> = combine(
        red, green, blue, redOn, greenOn, blueOn
    ) { values ->
        val r = values[0] as Float
        val g = values[1] as Float
        val b = values[2] as Float
        val rOn = values[3] as Boolean
        val gOn = values[4] as Boolean
        val bOn = values[5] as Boolean
        ColorState(r, g, b, rOn, gOn, bOn)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ColorState(0f, 0f, 0f, false, false, false)
    )

    //setters, update values
    fun setRed(value: Float) { _red.tryEmit(value) }

    fun setGreen(value: Float) { _green.tryEmit(value) }

    fun setBlue(value: Float) { _blue.tryEmit(value) }

    fun setRedOn(on: Boolean) { _redOn.tryEmit(on) }

    fun setGreenOn(on: Boolean) { _greenOn.tryEmit(on) }

    fun setBlueOn(on: Boolean) { _blueOn.tryEmit(on) }

    fun toColorData() : ColorData = ColorData(
        red = _red.value,
        green = _green.value,
        blue = _blue.value,
        redOn = _redOn.value,
        greenOn = _greenOn.value,
        blueOn = _blueOn.value,
    )

    fun apply(data: ColorData) {
        _red.value = data.red
        _green.value = data.green
        _blue.value = data.blue
        _redOn.value = data.redOn
        _greenOn.value = data.greenOn
        _blueOn.value = data.blueOn
    }
}
