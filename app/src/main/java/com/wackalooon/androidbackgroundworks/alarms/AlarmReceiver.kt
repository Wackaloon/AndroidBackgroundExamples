package com.wackalooon.androidbackgroundworks.alarms

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.wackalooon.androidbackgroundworks.MainActivity
import com.wackalooon.androidbackgroundworks.R

private const val CHANNEL_ID: String = "id_of_channel"

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
        if (intent.action != ALARM_ACTION) {
            // not out case, ignore
            return
        }
        val text = "Alarm triggered with input: ${intent.extractInput()}"
        context.createNotificationChannel(CHANNEL_ID)
        context.getNotificationManager()
            .notify(text.hashCode(), createNotification(context, text))
    }

    private fun createNotification(context: Context, text: String): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Alarm")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setFullScreenIntent(context.getPendingIntent(MainActivity::class.java), true)
            .build()
    }

    private fun Context.getPendingIntent(classz: Class<out Activity>): PendingIntent {
        val fullScreenIntent = Intent(this, classz)
        return PendingIntent.getActivity(
            this, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun Context.createNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val descriptionText = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = descriptionText
            val notificationManager = getNotificationManager()
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun Context.getNotificationManager(): NotificationManager {
        return getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun Intent.extractInput(): String {
        return getStringExtra(ALARM_INPUT_KEY) ?: "Empty"
    }

}

