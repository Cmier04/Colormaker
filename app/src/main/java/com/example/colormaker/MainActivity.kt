package com.example.colormaker

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
import android.widget.Switch
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
//delete if needed
import android.util.Log

/*---------------------N O T E S------------------------
     - add persistance = when app closes out, should save data,
     - make sure it can also save data when switch is turned off
 */

class MainActivity : AppCompatActivity() {

    private lateinit var colorView: View
    private val maxColor = 100

    var isProgrammaticChange = false

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private data class ColorController(
        val switch: Switch,
        val seekBar: SeekBar,
        val editText: EditText,
        var prevValue: Float = 0.0f
    )

    private lateinit var red: ColorController
    private lateinit var green: ColorController
    private lateinit var blue: ColorController

    //reset function
    @SuppressLint("SetTextI18n")
    private fun reset() {
        listOf(red, green, blue).forEach {
            it.switch.isChecked = false
            it.prevValue = 0.0f
            it.seekBar.isEnabled = false
            it.editText.isEnabled = false
            it.seekBar.progress = (0.0f * maxColor).toInt()
            it.editText.setText("0.00")
        }
        updateColor()
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //assign each variable to their id; altered to save line space and condense amount of variables
        colorView = findViewById(R.id.colorView)
        red = ColorController(
            findViewById(R.id.Redswitch),
            findViewById(R.id.RedseekBar),
            findViewById(R.id.redText)
        )
        green = ColorController(
            findViewById(R.id.Greenswitch),
            findViewById(R.id.GreenseekBar),
            findViewById(R.id.greenText)
        )
        blue = ColorController(
            findViewById(R.id.Blueswitch),
            findViewById(R.id.BlueseekBar),
            findViewById(R.id.blueText)
        )

        //ColorController, set up with colors and reset button
        setupColorController(red)
        setupColorController(green)
        setupColorController(blue)

        //load data to prevent constant reset
        loadData()

        //set up reset button which resets the data inputted into program
        findViewById<Button>(R.id.resetButton).setOnClickListener {
            reset()
            saveData()
        }
    }

    //update the color by connecting red switch with seekbar using isChecked
    private fun updateColor() {
        fun getValue(control: ColorController): Float =
            if (control.switch.isChecked) control.prevValue else 0.0f

        val r = (getValue(red) * 255).toInt()
        val g = (getValue(green) * 255).toInt()
        val b = (getValue(blue) * 255).toInt()
        colorView.setBackgroundColor(Color.rgb(r, g, b))
    }

    //define setupColorController function
    @SuppressLint("SetTextI18n")
    private fun setupColorController(color: ColorController) {
        color.editText.filters = arrayOf(DecimalInputFilter())
        color.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val value = progress / maxColor.toFloat()
                    color.prevValue = value
                    color.editText.setText("%.2f".format(value))
                    updateColor()
                }
            }

            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        //Edit Text setup
        color.editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                //val string = s.toString()
                if (isProgrammaticChange) return
                val value = s.toString().toFloatOrNull()
                if (value != null && value in 0.0..1.0) {
                    val progress = (value * maxColor).toInt()
                    color.seekBar.progress = progress
                    color.prevValue = value
                    updateColor()
                } else {
                    showToast("Please enter a value between 0.0 and 1.0")
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        /*
        //checking if switch is active
        color.switch.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                color.seekBar.isEnabled = false
                color.editText.isEnabled = false
                color.editText.setText("0.0")
                color.seekBar.progress = 0
            } else {
                color.seekBar.isEnabled = true
                color.editText.isEnabled = true
                color.editText.setText("%.2f".format(color.prevValue))
                color.seekBar.progress = (color.prevValue * maxColor).toInt()
            }
            updateColor()
        }
    }
 */

        color.switch.setOnCheckedChangeListener { _, isChecked ->
            color.seekBar.isEnabled = isChecked
            color.editText.isEnabled = isChecked
            if (isChecked) {
                color.editText.setText("%.2f".format(color.prevValue))
                color.seekBar.progress = (color.prevValue * maxColor).toInt()
            } else {
                color.editText.setText("0.00")
                color.seekBar.progress = 0
            }
            updateColor()
        }
    }
    //save data function which saves color, number, and other data
    private fun saveData() {
        val prefs = getSharedPreferences("color_prefs", MODE_PRIVATE)
        val editor = prefs.edit()

        listOf("red" to red, "green" to green, "blue" to blue).forEach { (name, ctrl) ->
            editor.putBoolean("${name}_switch", ctrl.switch.isChecked)
            editor.putFloat("${name}_value", ctrl.prevValue)
        }
        editor.apply()
    }

    //load data function which loads the saved data after app is reactivated
    @SuppressLint("SetTextI18n")
    private fun loadData() {
        val prefs = getSharedPreferences("color_prefs", MODE_PRIVATE)

        listOf("red" to red, "green" to green, "blue" to blue).forEach { (name, ctrl) ->
            val isOn = prefs.getBoolean("${name}_switch", false)
            val value = prefs.getFloat("${name}_value", 0.0f)

            ctrl.switch.isChecked = isOn
            ctrl.prevValue = value

            ctrl.seekBar.isEnabled = isOn
            ctrl.editText.isEnabled = isOn
            ctrl.seekBar.progress = (value * maxColor).toInt()
            //change if needed
            isProgrammaticChange = true
            ctrl.editText.setText("%.2f".format(if(isOn) value else 0.0f))
            //other deletable change
            isProgrammaticChange = false
            //delete if needed
            Log.d("ColorMaker", "Loaded red: $value, switch: $isOn")
        }
        updateColor()
    }

    //save when app is paused
    override fun onPause() {
        super.onPause()
        saveData()
    }
}