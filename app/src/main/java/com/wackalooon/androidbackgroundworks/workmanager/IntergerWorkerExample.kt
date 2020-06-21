package com.wackalooon.androidbackgroundworks.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.random.Random


class IntergerWorkerExample(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_TAG = "CoroutineWorkRequest"
        const val WORK_RESULT_KEY = "${WORK_TAG}OutputKey"
        private const val WORK_INPUT_KEY = "${WORK_TAG}InputKey"

        fun createWorkRequest(inputData: Int): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<IntergerWorkerExample>()
                    .setInputData(workDataOf(WORK_INPUT_KEY to inputData))
                    .addTag(WORK_TAG)
                    .build()
        }
    }

    override suspend fun doWork(): Result = coroutineScope {
       return@coroutineScope Result.success(workDataOf(WORK_RESULT_KEY to inputData.getInt(WORK_INPUT_KEY, 0)))
    }

}
