package com.example.colormaker
import android.text.InputFilter
import android.text.Spanned

class DecimalInputFilter : InputFilter {
    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ) : CharSequence? {
        val result = StringBuilder(dest).insert(dstart, source).toString()
        if (result.isEmpty()) return null

        try {
            val value = result.toFloat()
            val decimalIndex = result.indexOf(".")
            if (value in 0.0..1.0 && (decimalIndex == -1 || result.length - decimalIndex - 1 <= 2)) {
                return null
            }
        } catch (e: NumberFormatException) {
            //reject invalid number
        }
        return ""
    }
}