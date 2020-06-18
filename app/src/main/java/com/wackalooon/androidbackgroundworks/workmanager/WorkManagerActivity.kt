package com.wackalooon.androidbackgroundworks.workmanager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.*
import com.wackalooon.androidbackgroundworks.R
import kotlinx.android.synthetic.main.activity_work_manager.*
import java.util.*
import java.util.concurrent.Executor

private const val UNIQUE_WORK_NAME = "UniqueWorkName"

class WorkManagerActivity : AppCompatActivity() {

    private val context = this

    // create work requests
    private val workRequestCommon = CommonWorkRequest.createWorkRequest(inputData = "One")
    private val workRequestCoroutines = CoroutineWorkRequest.createWorkRequest(inputData = "Two")
    private val workRequestRx = RxWorkRequest.createWorkRequest(inputData = "Three")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_work_manager)
        work_manager_status.movementMethod = ScrollingMovementMethod()

        work_manager_start_btn.onClick {
            work_manager_stop_btn.visibility = View.VISIBLE
            work_manager_start_btn.visibility = View.GONE
            resetStatusLog()
            launchWorkers()
        }
        work_manager_stop_btn.onClick {
            work_manager_stop_btn.visibility = View.GONE
            work_manager_start_btn.visibility = View.VISIBLE
            cancelWorkers()
        }
        // note, it will observe *existing* job details on each subsequent startup
        // as job is unique and was launched already, hence can have some state from start
        observeWorkState(UNIQUE_WORK_NAME)

        // observe progress of a particular work request, doesn't matter when it will be launched
        observeWorkerProgress(workRequestCoroutines.id)
    }

    private fun cancelWorkers() {
        WorkManager.getInstance(context)
            .cancelUniqueWork(UNIQUE_WORK_NAME)
    }

    private fun launchWorkers() {
        addToStatusLog("Started")

        // enqueue workers in required order
        val operation = WorkManager.getInstance(context)
            // begin unique allows us to track work status, it's not necessary otherwise
            .beginUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.REPLACE, workRequestCommon)
            // chain requests
            .then(workRequestCoroutines)
            .then(workRequestRx)
            .enqueue()

        observeWorkState(operation)

        listenToWorkResult(operation)
    }

    private fun observeWorkState(operation: Operation) {
        val owner = this
        // observe state of resulted work
        operation.state.observe(owner, Observer { state ->
            addToStatusLog("WorkChain.state.observe $state")
        })
    }

    private fun listenToWorkResult(operation: Operation) {
        // kind of callback
        val listener = Runnable { addToStatusLog("WorkChain.result.addListener: Finished!") }
        // will be used to launch callback
        val executor = Executor { runnable -> runnable?.run() }
        // listen for result
        operation.result.addListener(listener, executor)
    }

    private fun observeWorkerProgress(id: UUID) {
        // observe particular worker state
        val owner = this
        WorkManager.getInstance(context)
            .getWorkInfoByIdLiveData(id)
            .observe(owner, Observer { workInfo: WorkInfo? ->
                val progress = workInfo?.progress?.getInt("Progress", 0)
                addToStatusLog("WorkInfo $id progress $progress")
                addToStatusLog("WorkInfo $id state ${workInfo?.state}")
            })
    }

    private fun observeWorkState(uniqueWorkName: String) {
        val owner = this
        WorkManager.getInstance(context)
            .getWorkInfosForUniqueWorkLiveData(uniqueWorkName)
            .observe(owner, Observer { workInfoList ->
                workInfoList.forEach { workInfo ->
                    // each worker have long default tag and my custom short one, select short one
                    val tag = workInfo.tags.minBy { it.length }!!

                    addToStatusLog("$tag Work state: ${workInfo.state.name}")

                    if (workInfo.state != WorkInfo.State.SUCCEEDED) {
                        return@forEach
                    }
                    val successOutputData = workInfo.outputData
                    // in this example all workers have Tag + "OutputKey" as output key
                    val output = successOutputData.getString(tag + "OutputKey")

                    addToStatusLog("$tag Work output: $output")
                }
            })
    }

    // example of all methods for canceling jobs
    private fun cancelWork(workRequest: OneTimeWorkRequest) {
        WorkManager.getInstance(context)
            .cancelAllWork()

        WorkManager.getInstance(context)
            .cancelUniqueWork("Unique work name")

        WorkManager.getInstance(context)
            .cancelWorkById(workRequest.id)

        WorkManager.getInstance(context)
            .cancelAllWorkByTag(CommonWorkRequest.WORK_TAG)

    }

    private fun addToStatusLog(text: String) = runOnUiThread {
        val newText = "${work_manager_status.text}\n$text"
        work_manager_status.text = newText
    }

    private fun resetStatusLog() {
        work_manager_status.text = null
    }

    private fun Button.onClick(action: Button.() -> Unit) {
        this.setOnClickListener {
            action()
        }
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, WorkManagerActivity::class.java)
        }
    }
}
