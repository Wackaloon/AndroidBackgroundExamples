package com.wackalooon.androidbackgroundworks.workmanager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
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
        // create workers
        val workRequest = WeirdWorkRequest.createWorkRequest("do it!")

        val workRequestCoroutines = CoroutineWorkRequest.createWorkRequest("do it via coroutines!!")

        // enqueue them in required order
        val work = WorkManager.getInstance()
            .beginWith(workRequest)
            .then(workRequestCoroutines)
            .enqueue()

        // observe state of resulted work
        work.state.observe(this, Observer { state ->
            setText(state.toString())
        })

        // listen for result
        work.result.addListener(
            { setText("Finished!") },
            { command -> command?.run() })

        // observe particular worker state
        WorkManager.getInstance()
            .getWorkInfosByTagLiveData(WeirdWorkRequest.WORK_TAG)
            .observe(this, observer)

        WorkManager.getInstance()
            .getWorkInfosByTagLiveData(CoroutineWorkRequest.WORK_TAG)
            .observe(this, observer)
    }

    private val observer = Observer<List<WorkInfo>> { state ->
        if (state.isNullOrEmpty()) {
            setText("Empty")
        } else {
            // We only care about the one output status.
            // Every continuation has only one worker tagged TAG_OUTPUT
            val workInfo = state[0]

            setText(workInfo.tags.toString())

            when (workInfo.state) {
                WorkInfo.State.ENQUEUED,
                WorkInfo.State.RUNNING,
                WorkInfo.State.FAILED,
                WorkInfo.State.BLOCKED,
                WorkInfo.State.CANCELLED -> setText(workInfo.state.name)

                WorkInfo.State.SUCCEEDED -> {
                    setText(workInfo.state.name)

                    val successOutputData = workInfo.outputData
                    val firstValue = successOutputData.getString(WeirdWorkRequest.WORK_RESULT_KEY)
                    val secondValue = successOutputData.getInt(CoroutineWorkRequest.WORK_RESULT_KEY, -1)

                    setText("Output $firstValue - $secondValue")
                }
            }
        }
    }

    private fun setText(text: String) {
        val newText = "${work_manager_status.text}\ntime: ${getTime()} text: $text"
        work_manager_status.text = newText
    }

    private fun getTime(): Long {
        return System.currentTimeMillis() - timeOfStart.value
    }
}