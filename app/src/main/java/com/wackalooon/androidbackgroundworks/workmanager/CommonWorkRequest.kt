package com.wackalooon.androidbackgroundworks.workmanager

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

private var retry = 1

class CommonWorkRequest(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    companion object {
        const val WORK_TAG = "CommonWorkRequest"
        const val WORK_RESULT_KEY = "${WORK_TAG}OutputKey"
        private const val WORK_INPUT_KEY = "${WORK_TAG}InputKey"

        fun createWorkRequest(inputData: String): OneTimeWorkRequest {
            val networkConstraint = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            return OneTimeWorkRequestBuilder<CommonWorkRequest>()
                .setConstraints(networkConstraint)
                .setInputData(workDataOf(WORK_INPUT_KEY to inputData))
                .setInitialDelay(1, TimeUnit.SECONDS)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.SECONDS)
                .addTag(WORK_TAG)
                .build()
        }
    }


    override fun doWork(): Result {
        setProgress(1)
        Thread.sleep(1000)
        val input = inputData.getString(WORK_INPUT_KEY)
        requireNotNull(input) { "Launch worker only with {@link #createWorkRequest(String)}" }
        val result = doHeavyOperation(input)
        setProgress(25)
        Thread.sleep(1000)
        return if (retry % 3 != 0) {
            setProgress(50+retry++)
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
    ){
        setProgressAsync(workDataOf("Progress" to progressPercent))
    }

    private fun doHeavyOperation(input: String): Data {
        return workDataOf(WORK_RESULT_KEY to input + "Common")
    }
}
