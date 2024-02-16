package edu.uw.ischool.xyou.awty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import kotlin.reflect.typeOf

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private var started = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up the start/stop button
        val startStopButton = findViewById<Button>(R.id.start_stop_button)
        if (started) startStopButton.text = getString(R.string.stop_button)
        else startStopButton.text = getString(R.string.start_button)

        startStopButton.setOnClickListener {
            // get user input
            val phoneNumber = findViewById<EditText>(R.id.phone_number_input).text.toString()
            val message = findViewById<EditText>(R.id.message_input).text.toString()
            val time = findViewById<EditText>(R.id.time_input).text.toString()

            if (isValidInput(phoneNumber, message, time)) {
                Log.i(TAG, "In this branch")
                started = !started
                if (started) {
                    // Start the service
                    startStopButton.text = getString(R.string.stop_button)
                    val intent = Intent(this, AWTYService::class.java).apply {
                        putExtra("phone_number", phoneNumber.toString())
                        putExtra("message", message.toString())
                        putExtra("time", time.toString())
                    }
                    startService(intent)
                } else {
                    // Stop the service
                    startStopButton.text = getString(R.string.start_button)
                    val intent = Intent(this, AWTYService::class.java)
                    stopService(intent)
                }
            } else {
                Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidInput(phoneNumber: String, message: String, time: String): Boolean {
        return phoneNumber.length == 10 && message != "" && time != ""
    }
}