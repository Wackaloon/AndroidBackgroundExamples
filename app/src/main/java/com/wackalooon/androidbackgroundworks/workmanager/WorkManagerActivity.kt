package com.wackalooon.androidbackgroundworks.workmanager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.Operation
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.wackalooon.androidbackgroundworks.R
import kotlinx.android.synthetic.main.activity_work_manager.*
import java.util.UUID
import java.util.concurrent.Executor

private const val UNIQUE_WORK_NAME = "UniqueWorkName"

class WorkManagerActivity : AppCompatActivity() {

    private val context = this

    // create workers
    val workRequestCommon = CommonWorkRequest.createWorkRequest(inputData = "One")
    val workRequestCoroutines = CoroutineWorkRequest.createWorkRequest(inputData = "Two")
    val workRequestRx = RxWorkRequest.createWorkRequest(inputData = "Three")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_work_manager)
        setClickListener()
        work_manager_status.movementMethod = ScrollingMovementMethod()
        // note, it will observe *existing* job details on each subsequent startup
        // as job is unique and was launched already, hence have some state
        observeWorkState(UNIQUE_WORK_NAME, RxWorkRequest.WORK_TAG, RxWorkRequest.WORK_RESULT_KEY)
        // observe progress of a particular work request, doesn't matter when it will be launched
        observeWorkerProgress(workRequestCoroutines.id)
    }

    private fun setClickListener() {
        work_manager_start_btn.text = "Launch works"
        work_manager_start_btn.setOnClickListener {
            work_manager_status.text = null
            importantStuff()
            work_manager_start_btn.text = "Cancel works"
            work_manager_start_btn.setOnClickListener {
                WorkManager.getInstance(context)
                    .cancelUniqueWork(UNIQUE_WORK_NAME)
                setClickListener()
            }
        }

    }

    private fun importantStuff() {
        setText("Started")

        // enqueue workers in required order
        val work = WorkManager.getInstance(context)
            // begin unique allows us to track work status, it's not necessary otherwise
            .beginUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.REPLACE, workRequestCommon)
            // chain requests
            .then(workRequestCoroutines)
            .then(workRequestRx)
            .enqueue()

        observeWorkState(work)
        listenToWorkResult(work)
    }

    private fun observeWorkState(work: Operation) {
        val owner = this
        // observe state of resulted work
        work.state.observe(owner, Observer { state ->
            setText("WorkChain.state.observe $state")
        })
    }

    private fun listenToWorkResult(work: Operation) {
        // kind of callback
        val listener = Runnable { setText("WorkChain.result.addListener: Finished!") }
        // will be used to launch callback
        val executor = Executor { runnable -> runnable?.run() }
        // listen for result
        work.result.addListener(listener, executor)
    }

    private fun observeWorkerProgress(id: UUID) {
        // observe particular worker state
        val owner = this
        WorkManager.getInstance(context)
            .getWorkInfoByIdLiveData(id)
            .observe(owner, Observer { workInfo: WorkInfo? ->
                val progress = workInfo?.progress?.getInt("Progress", 0)
                setText("WorkInfo.progress $progress")
            })
    }

    private fun observeWorkState(name: String, tag: String, resultKey: String) {
        // observe particular worker state
        val owner = this
        WorkManager.getInstance(context)
            .getWorkInfosForUniqueWorkLiveData(name)
            .observe(owner, Observer { workInfoList ->
                workInfoList.forEach { workInfo ->
                    val tag = workInfo.tags.minBy { it.length }!!
                    setText("$tag Work state: ${workInfo.state.name}")

                    if (workInfo.state != WorkInfo.State.SUCCEEDED) {
                        return@forEach
                    }
                    val successOutputData = workInfo.outputData

                    val output = successOutputData.getString(tag + "OutputKey")

                    setText("$tag Work output: $output")

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

    private fun setText(text: String) = runOnUiThread {
        val newText = "${work_manager_status.text}\n$text"
        work_manager_status.text = newText
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, WorkManagerActivity::class.java)
        }
    }
}