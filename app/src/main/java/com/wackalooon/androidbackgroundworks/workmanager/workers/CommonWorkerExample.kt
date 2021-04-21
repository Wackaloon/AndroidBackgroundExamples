package com.wackalooon.androidbackgroundworks.workmanager.workers

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.work.*
import java.util.concurrent.TimeUnit

@VisibleForTesting
var retry = 1

/**
 * Gathers all incoming arguments into array and passes them to the result along with worker name.
 * Retries 3 times each time.
 */
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
        val result = imitateHardWork()
        return if (retry % 3 != 0) {
            setProgress(50 + retry++)
            Result.retry()
        } else {
            setProgress(90)
            Result.success(result)
        }
    }

    private fun imitateHardWork(): Data {
        setProgress(1)
        val workerInput = inputData.getStringArray(WORK_INPUT_KEY)
        val previousWorkersResult = inputData.getIntArray(IntegerWorkerExample.WORK_RESULT_KEY)
        Thread.sleep(2000)
        setProgress(25)
        return doHeavyOperation(workerInput, previousWorkersResult)
    }

    private fun setProgress(
        progressPercent: Int
    ) {
        Thread.sleep(1000)
        setProgressAsync(workDataOf("Progress" to progressPercent))
    }

    private fun doHeavyOperation(
        input: Array<String>?,
        inputPrevious: IntArray?
    ): Data {
        val previousWorkersResult = inputPrevious?.toList().toString()
        val workerInput = input?.toList().toString()
        val output = workerInput + "Common" + previousWorkersResult
        return workDataOf(WORK_RESULT_KEY to output)
    }
}
