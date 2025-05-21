package com.example.colormaker

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "color_prefs")

object  ColorDataStore {
   private val RED = floatPreferencesKey("red")
    private val GREEN = floatPreferencesKey("green")
    private val BLUE = floatPreferencesKey("blue")

    private val RED_ON = booleanPreferencesKey("red_on")
    private val GREEN_ON = booleanPreferencesKey("green_on")
    private val BLUE_ON = booleanPreferencesKey("blue_on")

    suspend fun save(context: Context, vm:ColorViewModel) {
        context.dataStore.edit { prefs ->
            prefs[RED] = vm.red.value
            prefs[GREEN] = vm.green.value
            prefs[BLUE] = vm.blue.value

            prefs[RED_ON] = vm.redOn.value
            prefs[GREEN_ON] = vm.greenOn.value
            prefs[BLUE_ON] = vm.blueOn.value
        }
    }

    //load preferences to be usable in MainActivity
    fun load(context: Context): Flow<ColorData> {
        return context.dataStore.data.map { prefs ->
            ColorData(
                red = prefs[RED] ?: 0f,
                green = prefs[GREEN] ?: 0f,
                blue = prefs[BLUE] ?: 0f,
                redOn = prefs[RED_ON] ?: false,
                greenOn = prefs[GREEN_ON] ?: false,
                blueOn = prefs[BLUE_ON] ?: false,
            )
        }
    }
}

data class ColorData(
    val red: Float,
    val green: Float,
    val blue: Float,
    val redOn: Boolean,
    val greenOn: Boolean,
    val blueOn: Boolean
)