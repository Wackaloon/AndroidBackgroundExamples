package com.wackalooon.androidbackgroundworks.workmanager

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import io.reactivex.Single


class RxWorkerExample(
    appContext: Context,
    workerParams: WorkerParameters
) : RxWorker(appContext, workerParams) {

    companion object {
        const val WORK_TAG = "RxWorkRequest"
        const val WORK_RESULT_KEY = "${WORK_TAG}OutputKey"

        @VisibleForTesting
        const val WORK_INPUT_KEY = "${WORK_TAG}InputKey"

        fun createWorkRequest(inputData: String): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<RxWorkerExample>()
                    .setInputData(workDataOf(WORK_INPUT_KEY to inputData))
                    .addTag(WORK_TAG)
                    .build()
        }
    }

    override fun createWork(): Single<Result> = Single.fromCallable {
        val input = inputData.getString(WORK_INPUT_KEY)

        val inputFromExpectedPreviousJob = inputData.getString(CoroutineWorkerExample.WORK_RESULT_KEY)

        requireNotNull(input) { "Launch worker only with {@link #createWorkRequest(String)}" }

        val result = calculateDataSynchronously(input, inputFromExpectedPreviousJob)

        Thread.sleep(5000)
        Result.success(getResultDataFor(result))
    }

    private fun calculateDataSynchronously(input: String, optionalParam: String?): String {
        val resultBuilder = StringBuilder().append(input + "Rx" + optionalParam)
        return resultBuilder.toString()
    }

    private fun getResultDataFor(result: String): Data {
        return workDataOf(WORK_RESULT_KEY to result)
    }
}
