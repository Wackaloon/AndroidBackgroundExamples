package com.wackalooon.androidbackgroundworks.workmanager

import android.content.Context
import androidx.work.*


class WeirdWorkRequest(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    companion object {
        const val WORK_TAG = "WeirdWorkRequest"
        const val WORK_RESULT_KEY = "${WORK_TAG}OutputKey"
        private const val WORK_INPUT_KEY = "${WORK_TAG}InputKey"

        fun createWorkRequest(inputData: String): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<WeirdWorkRequest>()
                .setInputData(workDataOf(WORK_INPUT_KEY to inputData))
                .addTag(WORK_TAG)
                .build()
        }
    }

    override fun doWork(): Result {
        val input = inputData.getString(WORK_INPUT_KEY)

        requireNotNull(input) { "Launch worker only with {@link #createWorkRequest(String)}" }

        return Result.success(getResultDataFor("Weird request finished from input = $input"))
    }

    private fun getResultDataFor(result: String): Data {
        return workDataOf(WORK_RESULT_KEY to result)
    }
}