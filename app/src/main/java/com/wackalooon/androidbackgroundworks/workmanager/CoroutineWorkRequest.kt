package com.wackalooon.androidbackgroundworks.workmanager

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.*


class CoroutineWorkRequest(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override val coroutineContext = Dispatchers.IO

    companion object {
        const val WORK_TAG = "CoroutineWorkRequest"
        const val WORK_RESULT_KEY = "${WORK_TAG}OutputKey"
        private const val WORK_INPUT_KEY = "${WORK_TAG}InputKey"

        fun createWorkRequest(inputData: String): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<WeirdWorkRequest>()
                .setInputData(workDataOf(WORK_INPUT_KEY to inputData))
                .addTag(WORK_TAG)
                .build()
        }
    }

    override suspend fun doWork(): Result = coroutineScope  {
        val input = inputData.getString(WORK_INPUT_KEY)
        requireNotNull(input)
        val job = async {
            calculateDataSynchronously(input)
        }
        val result = job.await()
        return@coroutineScope Result.success(getResultDataFor(result))
    }

    private fun calculateDataSynchronously(input: String): String {
        return "WE DID IT VIA COROUTINES = $input"
    }

    private fun getResultDataFor(result: String): Data {
        return workDataOf(WORK_RESULT_KEY to result)
    }
}