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

    suspend fun save(context: Context, data: ColorData) {
        context.dataStore.edit { prefs ->
            prefs[RED] = data.red
            prefs[GREEN] = data.green
            prefs[BLUE] = data.blue

            prefs[RED_ON] = data.redOn
            prefs[GREEN_ON] = data.greenOn
            prefs[BLUE_ON] = data.blueOn
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