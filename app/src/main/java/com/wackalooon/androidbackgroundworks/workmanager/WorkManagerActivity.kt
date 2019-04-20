package com.wackalooon.androidbackgroundworks.workmanager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.wackalooon.androidbackgroundworks.R
import kotlinx.android.synthetic.main.activity_work_manager.*

class WorkManagerActivity : AppCompatActivity() {
    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, WorkManagerActivity::class.java)
        }
    }

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
        val workRequest = WeirdWorkRequest.createWorkRequest("Common")

        val workRequestCoroutines = CoroutineWorkRequest.createWorkRequest("Coroutine")

        val workRequestRx = RxWorkRequest.createWorkRequest("Reactive jet")

        // enqueue them in required order
        val work = WorkManager.getInstance()
            //begin unique allows us to track work status later, it's not necessary otherwise
            .beginUniqueWork(RxWorkRequest.WORK_TAG, ExistingWorkPolicy.REPLACE, workRequest)
            .then(workRequestCoroutines)
            .then(workRequestRx)
            .enqueue()

        // observe state of resulted work
        work.state.observe(this, Observer { state ->
            setText("work.state.observe $state")
        })

        // listen for result
        work.result.addListener(
            { setText("work.result.addListener: Finished!") },
            { command -> command?.run() })

        // observe particular worker state
        WorkManager.getInstance()
            .getWorkInfosForUniqueWorkLiveData(RxWorkRequest.WORK_TAG)
            .observe(this, rxWorkerObserver)
    }

    private val rxWorkerObserver = createWorkerObserver(RxWorkRequest.WORK_TAG, RxWorkRequest.WORK_RESULT_KEY)

    private fun createWorkerObserver(workerTag: String, resultTag: String) = Observer<List<WorkInfo>> { state ->
        if (state.isNullOrEmpty()) {
            setText("state is empty")
        } else {
            // We only care about the final worker output status.
            val workInfo = state.firstOrNull{ it.tags.contains(workerTag)}

            if (workInfo?.state == WorkInfo.State.SUCCEEDED) {

                val successOutputData = workInfo.outputData
                val output = successOutputData.getString(resultTag)

                setText(
                    "work status: ${workInfo.state.name}" +
                            "\n output: $output"
                )
            }
        }
    }

    private fun setText(text: String) = runOnUiThread {
        val newText = "${work_manager_status.text}\n$text"
        work_manager_status.text = newText
    }
}