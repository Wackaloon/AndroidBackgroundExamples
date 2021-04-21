package com.wackalooon.androidbackgroundworks.workmanager.workers

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

/**
 * Randomly fails, when succeeds passes worker name plus argument to the result.
 */
class CoroutineWorkerExample(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val dispatcher = Dispatchers.IO

    companion object {
        const val WORK_TAG = "CoroutineWorkRequest"
        const val WORK_RESULT_KEY = "${WORK_TAG}OutputKey"
        private const val WORK_INPUT_KEY = "${WORK_TAG}InputKey"

        fun createWorkRequest(inputData: String): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<CoroutineWorkerExample>()
                    .setInputData(workDataOf(WORK_INPUT_KEY to inputData))
                    .addTag(WORK_TAG)
                    .build()
        }
    }

    override suspend fun doWork(): Result = coroutineScope {
        setProgress(1)
        val input = inputData.getString(WORK_INPUT_KEY)!!
        val inputFromExpectedPreviousJob = inputData.getString(CommonWorkerExample.WORK_RESULT_KEY)
        setProgress(50)
        val result: String = calculateData(input, inputFromExpectedPreviousJob)
        setProgress(99)
        return@coroutineScope if (Random.nextBoolean()) {
            result.toResult()
        } else {
            Result.failure()
        }
    }

    private suspend fun setProgress(
        progressPercent: Int
    ) {
        delay(1000)
        setProgress(workDataOf("Progress" to progressPercent))
    }

    private suspend fun calculateData(
        input: String,
        optionalParam: String?
    ): String = withContext(dispatcher) {
        val resultBuilder = StringBuilder().append(input + "Coroutine" + optionalParam)
        delay(3000)
        return@withContext resultBuilder.toString()
    }

    private fun String.toResult(): Result {
        return Result.success(workDataOf(WORK_RESULT_KEY to this))
    }
}
