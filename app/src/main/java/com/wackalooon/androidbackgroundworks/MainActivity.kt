package com.wackalooon.androidbackgroundworks

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wackalooon.androidbackgroundworks.alarms.AlarmActivity
import com.wackalooon.androidbackgroundworks.workmanager.WorkManagerActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
//        startActivity(WorkManagerActivity.createIntent(this))
        startActivity(AlarmActivity.createIntent(this))
    }
}
