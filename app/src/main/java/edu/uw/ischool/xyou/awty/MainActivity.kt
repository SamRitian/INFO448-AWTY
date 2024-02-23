package edu.uw.ischool.xyou.awty

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlin.reflect.typeOf

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_SEND_SMS = 123
    }

    private val TAG = "MainActivity"
    private var started = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startStopButton: Button = findViewById(R.id.start_stop_button)
        startStopButton.setOnClickListener {
            if (!started) {
                checkForSmsPermission()
            } else {
                stopService()
            }
        }
    }

    private fun checkForSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS), PERMISSION_SEND_SMS)
        } else {
            startService()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_SEND_SMS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startService()
                } else {
                    Toast.makeText(this, "Permission denied to send SMS", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startService() {
        val phoneNumberInput: EditText = findViewById(R.id.phone_number_input)
        val messageInput: EditText = findViewById(R.id.message_input)
        val timeInput: EditText = findViewById(R.id.time_input)

        val phoneNumber = phoneNumberInput.text.toString()
        val message = messageInput.text.toString()
        val timeInterval = timeInput.text.toString()

        if (phoneNumber.isNotEmpty() && message.isNotEmpty() && timeInterval.isNotEmpty()) {
            val intent = Intent(this, AWTYService::class.java).apply {
                putExtra("phone_number", phoneNumber)
                putExtra("message", message)
                putExtra("time", timeInterval)
            }
            startService(intent)
            started = true
            findViewById<Button>(R.id.start_stop_button).text = getString(R.string.stop_button)
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopService() {
        val intent = Intent(this, AWTYService::class.java)
        stopService(intent)
        started = false
        findViewById<Button>(R.id.start_stop_button).text = getString(R.string.start_button)
    }
}