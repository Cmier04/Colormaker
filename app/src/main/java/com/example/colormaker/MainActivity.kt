package com.example.colormaker

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import android.widget.*
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.Flow

import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore

import android.widget.Switch
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
/*---------------------N O T E S------------------------
   - implement persistence properly using OnPause(), OnCreate() OnResume()
*/
class MainActivity : AppCompatActivity() {

    private lateinit var colorView: View
    private val maxColor = 100

    private val viewModel: ColorViewModel by viewModels()

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private data class ColorController(
        val switch: Switch,
        val seekBar: SeekBar,
        val editText: EditText,
        var prevValue: Float = 0f
    )

    private lateinit var red: ColorController
    private lateinit var green: ColorController
    private lateinit var blue: ColorController

    private var isLoadingPrefs = false

//    //reset function
//    @SuppressLint("SetTextI18n")
//    private fun reset() {
//        listOf(red, green, blue).forEach {
//            it.switch.isChecked = false
//            it.prevValue = 0.0f
//            it.seekBar.isEnabled = false
//            it.editText.isEnabled = false
//            it.seekBar.progress = (0.0f * maxColor).toInt()
//            it.editText.setText("0.00")
//        }
//        updateColor()
//    }
//    private fun showToast(message: String) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//    }

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

        //replaces if (saveInstanceState) and onSaveInstanceState
        lifecycleScope.launch {
            ColorDataStore.load(applicationContext).collect { data ->
                viewModel.setRed(data.red)
                viewModel.setGreen(data.green)
                viewModel.setBlue(data.blue)
                viewModel.setRedOn(data.redOn)
                viewModel.setGreenOn(data.greenOn)
                viewModel.setBlueOn(data.blueOn)
            }
        }
        lifecycleScope.launch {
            viewModel.colorState.collectLatest { state ->
                val r = state.red
                val g = state.green
                val b = state.blue
                val rOn = state.redOn
                val gOn = state.greenOn
                val bOn = state.blueOn
                //update UI
                updateUIFromState(r, g, b, rOn, gOn, bOn)
            }
        }
        findViewById<Button>(R.id.resetButton).setOnClickListener { resetAll() }

        //added later
        lifecycleScope.launch {
            ColorDataStore.load(applicationContext).collect { data ->
                viewModel.apply(data)
            }
        }

        lifecycleScope.launch {
            viewModel.colorState
                .debounce(500)
                .collectLatest { state ->
                    ColorDataStore.save(applicationContext, viewModel.toColorData())
                }
        }
    }

    //set up Toast for error messages
    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    //update the color by connecting red switch with seekbar using isChecked
    private fun updateColor(r: Float, g: Float, b: Float) {
        val redVal = (r * 255).toInt()
        val greenVal = (g * 255).toInt()
        val blueVal = (b * 255).toInt()
        colorView.setBackgroundColor(Color.rgb(redVal, greenVal, blueVal))
    }

    private fun setupColorController(ctrl: ColorController) {
        ctrl.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && ctrl.switch.isChecked) {
                    val value = progress / maxColor.toFloat()
                    ctrl.prevValue = value
                    isLoadingPrefs = true
                    ctrl.editText.setText("%.2f".format(value))
                    isLoadingPrefs = false

                    when (ctrl) {
                        red -> viewModel.setRed(value)
                        green -> viewModel.setGreen(value)
                        blue -> viewModel.setBlue(value)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        ctrl.editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isLoadingPrefs || !ctrl.switch.isChecked) return
                val value = s.toString().toFloatOrNull()
                if (value != null && value in 0.0..1.0) {
                    val progress = (value * maxColor).toInt()
                    ctrl.seekBar.progress = progress
                    ctrl.prevValue = value

                    when (ctrl) {
                        red -> viewModel.setRed(value)
                        green -> viewModel.setGreen(value)
                        blue -> viewModel.setBlue(value)
                    }
                } else {
                    showToast("Enter a value between 0.0 and 1.0")
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        //change values to 0.00 when switch is off and enable when switch is on
        ctrl.switch.setOnCheckedChangeListener { _, isChecked ->
            //disable/enable the seekBar and EditText
            ctrl.seekBar.isEnabled = isChecked
            ctrl.editText.isEnabled = isChecked

            isLoadingPrefs = true
            if (isChecked) {
                ctrl.editText.setText("%.2f".format(ctrl.prevValue))
                ctrl.seekBar.progress = (ctrl.prevValue * maxColor).toInt()
            } else {
                //when switch is Off, reset values to 0
                ctrl.editText.setText("0.00")
                ctrl.seekBar.progress = 0
            }
            isLoadingPrefs = false

            //update viewModel
            when (ctrl) {
                red -> viewModel.setRedOn(isChecked)
                green -> viewModel.setGreenOn(isChecked)
                blue -> viewModel.setBlueOn(isChecked)
            }
        }
    }

    private fun updateUIFromState(
        redVal: Float, greenVal: Float, blueVal: Float,
        redOn: Boolean, greenOn: Boolean, blueOn: Boolean
    ) {
        isLoadingPrefs = true

        //set switch states
        red.switch.isChecked = redOn
        green.switch.isChecked = greenOn
        blue.switch.isChecked = blueOn

        //restore persisted values on destroy
        red.prevValue = redVal
        green.prevValue = greenVal
        blue.prevValue = blueVal

        val redDisplay = if (redOn) red.prevValue else 0f
        val greenDisplay = if (greenOn) green.prevValue else 0f
        val blueDisplay = if (blueOn) blue.prevValue else 0f

        //seekBar and shows prevValue if switch is on
        red.seekBar.progress = (redDisplay * maxColor).toInt()
        green.seekBar.progress = (greenDisplay * maxColor).toInt()
        blue.seekBar.progress = (blueDisplay * maxColor).toInt()

        //update editText if user is Not typing in it
        if (!red.editText.hasFocus()) red.editText.setText("%.2f".format(redDisplay))
        if (!green.editText.hasFocus()) green.editText.setText("%.2f".format(greenDisplay))
        if (!blue.editText.hasFocus()) blue.editText.setText("%.2f".format(blueDisplay))

        //enables and disables controls
        red.seekBar.isEnabled = redOn
        green.seekBar.isEnabled = greenOn
        blue.seekBar.isEnabled = blueOn

        red.editText.isEnabled = redOn
        green.editText.isEnabled = greenOn
        blue.editText.isEnabled = blueOn

        //update colorView
        updateColor(redDisplay, greenDisplay, blueDisplay)
        isLoadingPrefs = false
    }

    private fun resetAll() {
        viewModel.setRed(0f)
        viewModel.setGreen(0f)
        viewModel.setBlue(0f)
        viewModel.setRedOn(false)
        viewModel.setGreenOn(false)
        viewModel.setBlueOn(false)
        showToast("Reset to default.")
        lifecycleScope.launch {
            ColorDataStore.save(applicationContext, viewModel.toColorData())
        }
    }

    //save when app is paused
    override fun onPause() {
        super.onPause()
        lifecycleScope.launch {
            ColorDataStore.save(applicationContext, viewModel.toColorData())
        }
    }
}