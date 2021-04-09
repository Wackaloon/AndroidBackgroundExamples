package com.wackalooon.androidbackgroundworks.workmanager.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.coroutineScope

/**
 * Just passes integer from argument to the result.
 */
class IntegerWorkerExample(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_TAG = "CoroutineWorkRequest"
        const val WORK_RESULT_KEY = "${WORK_TAG}OutputKey"
        private const val WORK_INPUT_KEY = "${WORK_TAG}InputKey"

        fun createWorkRequest(inputData: Int): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<IntegerWorkerExample>()
                    .setInputData(workDataOf(WORK_INPUT_KEY to inputData))
                    .addTag(WORK_TAG)
                    .build()
        }
    }

    override suspend fun doWork(): Result = coroutineScope {
       return@coroutineScope Result.success(workDataOf(WORK_RESULT_KEY to inputData.getInt(WORK_INPUT_KEY, 0)))
    }

}
