package com.wackalooon.androidbackgroundworks.workmanager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.Operation
import androidx.work.WorkManager
import com.wackalooon.androidbackgroundworks.R
import com.wackalooon.androidbackgroundworks.workmanager.utils.WorkObserver
import com.wackalooon.androidbackgroundworks.workmanager.workers.CommonWorkerExample
import com.wackalooon.androidbackgroundworks.workmanager.workers.CoroutineWorkerExample
import com.wackalooon.androidbackgroundworks.workmanager.workers.IntegerWorkerExample
import com.wackalooon.androidbackgroundworks.workmanager.workers.RxWorkerExample
import kotlinx.android.synthetic.main.activity_work_manager.*

const val UNIQUE_WORK_NAME = "UniqueWorkName"

class WorkManagerActivity : AppCompatActivity() {

    private val context = this

    // create work requests
    private val workRequestCommon = CommonWorkerExample.createWorkRequest(inputData = "One")
    private val workRequestCoroutines = CoroutineWorkerExample.createWorkRequest(inputData = "Two")
    private val workRequestRx = RxWorkerExample.createWorkRequest(inputData = "Three")

    private val intergerWorkerExample = IntegerWorkerExample.createWorkRequest(1)
    private val intergerWorkerExample2 = IntegerWorkerExample.createWorkRequest(5)
    private val intergerWorkerExample3 = IntegerWorkerExample.createWorkRequest(7)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_manager)
        setupStartStopButtons()
        // note, it will observe *existing* job details on each subsequent startup
        // as job is unique and was launched already, hence can have some state from start
        WorkObserver.observe(UNIQUE_WORK_NAME, this, this)

        // observe progress of a particular work request, doesn't matter when it will be launched
        worker_1.setWorker(workRequestCommon.id, this)
        worker_2.setWorker(workRequestCoroutines.id, this)
        worker_3.setWorker(RxWorkerExample.WORK_TAG, this)
    }

    private fun setupStartStopButtons() {
        work_manager_start_btn.setOnClickListener {
            work_manager_start_btn.isVisible = false
            work_manager_stop_btn.isVisible = true
            val enqueuedOperation = launchWorkers()
            operation_card.setOperation(enqueuedOperation, this)
        }
        work_manager_stop_btn.setOnClickListener {
            work_manager_start_btn.isVisible = true
            work_manager_stop_btn.isVisible = false
            cancelWorkers()
        }
    }

    private fun launchWorkers(): Operation {
        val listOfWorkers = listOf(
            intergerWorkerExample,
            intergerWorkerExample2,
            intergerWorkerExample3)
        // enqueue workers in required order
        return WorkManager.getInstance(context)
            // begin unique allows us to track work status, it's not necessary otherwise
            .beginUniqueWork(
                UNIQUE_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                listOfWorkers)
            // chain requests
            .then(workRequestCommon)
            .then(workRequestCoroutines)
            .then(workRequestRx)

            .enqueue()
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

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, WorkManagerActivity::class.java)
        }
    }
}
