package com.example.colormaker

//import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
//import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

/*import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat*/

class MainActivity : AppCompatActivity() {

    private lateinit var decreaseButton: Button
    private lateinit var increaseButton: Button
    private lateinit var counterView: TextView

    private var counter = -99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.decreaseButton = this.findViewById(R.id.DecreaseButton)
        increaseButton = findViewById(R.id.IncreaseButton)
        counterView = findViewById(R.id.counter)

        counter = counterView.text.toString().toInt()

        //@SuppressLint("SetTextI18n")
        fun updateCounterView() {
            counterView.text = "$counter"
        }

        updateCounterView()

        decreaseButton.setOnClickListener( { view: View ->
            println("decrease button clicked")
            counter -= 1
            updateCounterView()
        } )

        increaseButton.setOnClickListener {
            val counterVal = 
            val textOfButton = button.text
            println("$textOfButton button clicked")
            counter -= 1
            updateCounterView()
        }
    }
//
//    private fun x() {
//        var x1 = 1
//        y()
//        z(30)
//        a()
//    }
//
//    private fun a() {
//        val a1 = 5
//        val a2 = 10
//        for (n in 0..a1) {
//            val nn = n * 10
//            println("$nn")
//            for (m in 0..a2) {
//                println("the value of m is ${m}")
//            }
//        }
//    }
//
//    private fun z(p: Int): String {
//        val z1 = 2
//        println("I am in z and the value of my variable is ${z1 + p}")
//        return "done"
//    }
//
//    private fun y() {
//        println("I am in y")
//        val f = z(20)
//        println(f)
//    }
}
