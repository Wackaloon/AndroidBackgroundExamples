package com.wackalooon.androidbackgroundworks.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope


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
            return OneTimeWorkRequestBuilder<CoroutineWorkRequest>()
                .setInputData(workDataOf(WORK_INPUT_KEY to inputData))
                .addTag(WORK_TAG)
                .build()
        }
    }

    override suspend fun doWork(): Result = coroutineScope  {
        val input = inputData.getString(WORK_INPUT_KEY)

        requireNotNull(input) { "Launch worker only with {@link #createWorkRequest(String)}" }

        val inputFromExpectedPreviousJob = inputData.getString(WeirdWorkRequest.WORK_RESULT_KEY)

        val job = async {
            calculateDataSynchronously(input, inputFromExpectedPreviousJob)
        }
        val result = job.await()
        return@coroutineScope Result.success(getResultDataFor(result))
    }

    private fun calculateDataSynchronously(input: String, optionaParam:String?): String {
        val resultBuilder = StringBuilder().append("Coroutine worker finished with input = $input")
        if (!optionaParam.isNullOrEmpty()) {
            resultBuilder.append(" ")
            resultBuilder.append("Bonus: we've used previous worker result = $optionaParam")
        }
        return resultBuilder.toString()
    }

    private fun getResultDataFor(result: String): Data {
        return workDataOf(WORK_RESULT_KEY to result)
    }
}