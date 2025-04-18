package com.example.colormaker

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val switch1: SwitchCompat = findViewById(R.id.switch1)
        switch1.setOnClickListener { _, isChecked ->
            val message = if (isChecked as Boolean) "Switch1: ON" else "Switch1: OFF"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}

private fun SwitchCompat.setOnClickListener(function: (v: View, Any?) -> Unit) {
    TODO("Not yet implemented")
}
