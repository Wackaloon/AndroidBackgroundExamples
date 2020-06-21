package com.wackalooon.androidbackgroundworks.workmanager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.wackalooon.androidbackgroundworks.R
import com.wackalooon.androidbackgroundworks.WorkObserver
import kotlinx.android.synthetic.main.activity_work_manager.*

const val UNIQUE_WORK_NAME = "UniqueWorkName"

class WorkManagerActivity : AppCompatActivity() {

    private val context = this

    // create work requests
    private val workRequestCommon = CommonWorkerExample.createWorkRequest(inputData = "One")
    private val workRequestCoroutines = CoroutineWorkerExample.createWorkRequest(inputData = "Two")
    private val workRequestRx = RxWorkerExample.createWorkRequest(inputData = "Three")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_work_manager)

        work_manager_start_btn.onClick {
            visibility = View.GONE
            work_manager_stop_btn.visibility = View.VISIBLE
            launchWorkers()
        }
        work_manager_stop_btn.onClick {
            visibility = View.GONE
            work_manager_start_btn.visibility = View.VISIBLE
            cancelWorkers()
        }
        // note, it will observe *existing* job details on each subsequent startup
        // as job is unique and was launched already, hence can have some state from start
        WorkObserver.observe(UNIQUE_WORK_NAME, this, this)

        // observe progress of a particular work request, doesn't matter when it will be launched
        worker_1.setWorker(workRequestCommon.id, this)
        worker_2.setWorker(workRequestCoroutines.id, this)
        worker_3.setWorker(RxWorkerExample.WORK_TAG, this)

    }

    private fun launchWorkers() {
        // enqueue workers in required order
        val enqueuedOperation = WorkManager.getInstance(context)
                // begin unique allows us to track work status, it's not necessary otherwise
                .beginUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.REPLACE, workRequestCommon)
                // chain requests
                .then(workRequestCoroutines)
                .then(workRequestRx)

                .enqueue()

        operation.setOperation(enqueuedOperation, this)
    }

    private fun cancelWorkers() {
        WorkManager.getInstance(context)
                .cancelUniqueWork(UNIQUE_WORK_NAME)
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
                .cancelAllWorkByTag(CommonWorkerExample.WORK_TAG)

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
