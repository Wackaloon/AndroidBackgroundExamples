package com.wackalooon.androidbackgroundworks.alarms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val ALARM_ACTION = "W_ACTION"
        const val ALARM_INPUT_KEY = "W_INPUT"

        fun createIntent(context: Context, message: String): Intent {
            return Intent(context, AlarmReceiver::class.java).apply {
                action = ALARM_ACTION
                putExtra(ALARM_INPUT_KEY, message)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        // Is triggered when alarm goes off, i.e. receiving a system broadcast
        if (intent.action != ALARM_ACTION) {
            // not out case, ignore
            return
        }
        val text = "Alarm triggered with input: ${intent.extractInput()}"
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    private fun Intent.extractInput(): String {
        return getStringExtra(ALARM_INPUT_KEY) ?: "Empty"
    }

}
