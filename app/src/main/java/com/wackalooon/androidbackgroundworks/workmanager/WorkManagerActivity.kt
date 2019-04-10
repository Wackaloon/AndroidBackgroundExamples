package com.wackalooon.androidbackgroundworks.workmanager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.work.WorkManager
import com.wackalooon.androidbackgroundworks.LifecycleAwareActivity
import com.wackalooon.androidbackgroundworks.R
import kotlinx.android.synthetic.main.activity_work_manager.*

class WorkManagerActivity : LifecycleAwareActivity() {
    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, WorkManagerActivity::class.java)
        }
    }

    private val timeOfStart = lazy { System.currentTimeMillis() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_work_manager)

        work_manager_start_btn.setOnClickListener {
            importantStuff()
        }
    }

    private fun importantStuff() {
        setText("Started")
        val workRequest = WeirdWorkRequest.createWorkRequest("do it!")

        val work = WorkManager.getInstance().enqueue(workRequest)

        work.state.observe(this, Observer { state ->
            setText(state.toString())
        })
        work.result.addListener(
            { setText("Finished!") },
            { command -> command?.run() })
    }

    private fun setText(text: String) {
        val newText = "${work_manager_status.text}\ntime: ${getTime()} text: $text"
        work_manager_status.text = newText
    }

    private fun getTime(): Long {
        return System.currentTimeMillis() - timeOfStart.value
    }
}