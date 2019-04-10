package com.wackalooon.androidbackgroundworks.workmanager

import android.content.Context
import androidx.work.*

private const val WORK_TAG = "WeirdWorkRequest"
private const val WORK_INPUT_KEY = "${WORK_TAG}InputKey"
const val WORK_RESULT_KEY = "${WORK_TAG}OutputKey"

class WeirdWorkRequest(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    companion object {
        fun createWorkRequest(inputData: String): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<WeirdWorkRequest>()
                .setInputData(workDataOf(WORK_INPUT_KEY to inputData))
                .addTag(WORK_TAG)
                .build()
        }
    }

    override fun doWork(): Result {
        val input = inputData.getString(WORK_INPUT_KEY)
        requireNotNull(input)
        return Result.success(getResultDataFor("WE DID IT = $input"))
    }

    private fun getResultDataFor(result: String): Data {
        return workDataOf(WORK_RESULT_KEY to result)
    }
}