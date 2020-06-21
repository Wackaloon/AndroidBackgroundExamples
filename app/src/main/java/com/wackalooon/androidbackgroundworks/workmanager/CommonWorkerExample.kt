package com.wackalooon.androidbackgroundworks.workmanager

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.work.*
import java.util.concurrent.TimeUnit

@VisibleForTesting
var retry = 1

class CommonWorkerExample(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    companion object {
        const val WORK_TAG = "CommonWorkRequest"
        const val WORK_RESULT_KEY = "${WORK_TAG}OutputKey"

        @VisibleForTesting
        const val WORK_INPUT_KEY = "${WORK_TAG}InputKey"

        fun createWorkRequest(inputData: String): OneTimeWorkRequest {
            val networkConstraint = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()
            return OneTimeWorkRequestBuilder<CommonWorkerExample>()
                .setConstraints(networkConstraint)
                .setInputData(workDataOf(WORK_INPUT_KEY to inputData))
                .setInputMerger(ArrayCreatingInputMerger::class.java)
                .setInitialDelay(1, TimeUnit.SECONDS)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.SECONDS)
                .addTag(WORK_TAG)
                .build()
        }
    }


    override fun doWork(): Result {
        setProgress(1)
        Thread.sleep(1000)
        val input = inputData.getStringArray(WORK_INPUT_KEY)
        val inputPrevious = inputData.getIntArray(IntergerWorkerExample.WORK_RESULT_KEY)
        requireNotNull(input) { "Launch worker only with {@link #createWorkRequest(String)}" }
        val result = doHeavyOperation(input, inputPrevious)
        setProgress(25)
        Thread.sleep(1000)
        return if (retry % 3 != 0) {
            setProgress(50 + retry++)
            Thread.sleep(1000)
            Result.retry()
        } else {
            setProgress(90)
            Thread.sleep(1000)
            Result.success(result)
        }
    }

    private fun setProgress(
        progressPercent: Int
    ) {
        setProgressAsync(workDataOf("Progress" to progressPercent))
    }

    private fun doHeavyOperation(
        input: Array<String>?,
        inputPrevious: IntArray?
    ): Data {
        return workDataOf(WORK_RESULT_KEY to input?.toList().toString() + "Common" + inputPrevious?.toList().toString())
    }
}
