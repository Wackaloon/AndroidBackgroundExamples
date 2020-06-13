package com.wackalooon.androidbackgroundworks.workmanager

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit


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
                .addTag(WORK_TAG)
                .build()
        }
    }

    override fun doWork(): Result {
        val input = inputData.getString(WORK_INPUT_KEY)
        requireNotNull(input) { "Launch worker only with {@link #createWorkRequest(String)}" }
        val result = doHeavyOperation(input)
        return Result.success(result)
    }

    private fun doHeavyOperation(input: String): Data {
        return workDataOf(WORK_RESULT_KEY to input + "Common")
    }
}