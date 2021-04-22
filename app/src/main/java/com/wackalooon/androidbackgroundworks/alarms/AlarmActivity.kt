package com.wackalooon.androidbackgroundworks.alarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wackalooon.androidbackgroundworks.R
import kotlinx.android.synthetic.main.activity_alarm.*

const val ALARM_DELAY_IN_SECOND = 10

class AlarmActivity : AppCompatActivity() {
    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, AlarmActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
        background_alarm_button.setOnClickListener {
            createBackgroundAlarm()
        }
        foreground_alarm_button.setOnClickListener {
            createForegroundAlarm()
        }
    }

    /**
     * Triggers alarm even when app is killed.
     */
    private fun createBackgroundAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // create intent for the broadcast receiver
        // Time that we want alarm to fire in UTC
        val alarmTimeAtUTC = calculateTimeForLaunch()
        // alarm type, use real-time clock that will wake up the device
        val alarmType = AlarmManager.RTC_WAKEUP
        // Set with system Alarm Service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = AlarmReceiver.createIntent(this, "Background exact idle alarm")
            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
            alarmManager.setExactAndAllowWhileIdle(
                alarmType,
                alarmTimeAtUTC,
                pendingIntent
            )
        }
        // schedule exact but with exception of doze modes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = AlarmReceiver.createIntent(this, "Background exact alarm")
            // call getBroadcast, otherwise doesn't work
            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
            alarmManager.setExact(
                alarmType,
                alarmTimeAtUTC,
                pendingIntent
            )
        }
        // after API 19 works as inexact, i.e. alarms will trigger after alarmTimeAtUTC
        val intent = AlarmReceiver.createIntent(this, "Background inexact alarm")
        // call getBroadcast, otherwise doesn't work
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        alarmManager.set(
            alarmType,
            alarmTimeAtUTC,
            pendingIntent
        )
    }

    /**
     * Triggers alarm only when app is alive.
     */
    private fun createForegroundAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Time that we want alarm to fire in UTC
        val alarmTimeAtUTC = calculateTimeForLaunch()
        // alarm type, use real-time clock that will wake up the device
        val alarmType = AlarmManager.RTC_WAKEUP
        // listener tag to find it later
        val tagStr = "TAG"
        // `null` means the callback will be run at the Main thread
        val handler = null
        val context = this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            alarmManager.setExact(
                alarmType,
                alarmTimeAtUTC,
                tagStr,
                object : AlarmManager.OnAlarmListener {
                    override fun onAlarm() {
                        Toast.makeText(context, "Foreground alarm", Toast.LENGTH_LONG).show()
                    }
                }, handler
            )
            val alarmWindow3Minutes = 3 * 60 * 1000L // 3 minutes
            alarmManager.setWindow(
                alarmType,
                alarmTimeAtUTC,
                alarmWindow3Minutes,
                tagStr,
                object : AlarmManager.OnAlarmListener {
                    override fun onAlarm() {
                        Toast.makeText(context, "Foreground alarm window", Toast.LENGTH_LONG).show()
                    }
                }, handler
            )
        }
    }

    /**
     * @return UTC time long value with time set to [ALARM_DELAY_IN_SECOND] seconds in future.
     */
    private fun calculateTimeForLaunch(): Long {
        return System.currentTimeMillis() + ALARM_DELAY_IN_SECOND * 1_000L
    }
}
