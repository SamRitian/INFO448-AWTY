package edu.uw.ischool.xyou.awty

import android.app.IntentService
import android.content.Intent
import android.os.Handler
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
class AWTYService : IntentService("AWTYService") {

    private val TAG = "AWTYService"

    private lateinit var mHandler: Handler
    private var isRunning = false
    private var phoneNumber = ""
    private var message = ""
    private var time = 0

    override fun onCreate() {
        super.onCreate()
        mHandler = Handler()
        isRunning = true
        Log.i(TAG, "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // get intent data
        phoneNumber = intent?.getStringExtra("phone_number")!!
        message = intent.getStringExtra("message")!!
        time = intent.getStringExtra("time")!!.toInt()

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onHandleIntent(intent: Intent?) {
        while (isRunning) {
            Log.i(TAG, "Sending SMS to $phoneNumber: $message")
            try {
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(phoneNumber, null, message, null, null)
                Toast.makeText(this, "SMS sent to ${reformatPhoneNumber(phoneNumber)}: $message", Toast.LENGTH_SHORT).show()
                Thread.sleep((time * 1000 * 60).toLong()) // Sleep for 'time' minutes
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            } catch (e: IllegalArgumentException) {
                // Handle case where phoneNumber or message might be invalid
                Log.e(TAG, "SMS sending failed: ", e)
            }
        }
    }

    override fun onDestroy() {
        Log.i(TAG, "Service destroyed")
        isRunning = false
        super.onDestroy()
    }

    // takes in a string of length 10, returns (xxx) xxx-xxxx
    private fun reformatPhoneNumber(phoneNumber: String): String {
        return "(${phoneNumber.substring(0, 3)}) ${phoneNumber.substring(3, 6)}-${phoneNumber.substring(6, 10)}"
    }
}