package com.wackalooon.androidbackgroundworks.workmanager.workers

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker.Result
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.workDataOf
import com.wackalooon.androidbackgroundworks.workmanager.workers.CoroutineWorkerExample
import com.wackalooon.androidbackgroundworks.workmanager.workers.RxWorkerExample
import org.junit.Before
import org.junit.Test

private val RX_WORKER_EXPECTED_RESULT = Result.success(
    workDataOf(
        RxWorkerExample.WORK_RESULT_KEY to "TestRxnull"
    )
)

private val RX_WORKER_INPUT_DATA = workDataOf(
    RxWorkerExample.WORK_INPUT_KEY to arrayOf("Test")
)

private val RX_WORKER_EXPECTED_COMBINED_RESULT = Result.success(
    workDataOf(
        RxWorkerExample.WORK_RESULT_KEY to "TestRxTwo"
    )
)

private val RX_WORKER_COMBINED_INPUT_DATA = workDataOf(
    RxWorkerExample.WORK_INPUT_KEY to "Test",
    CoroutineWorkerExample.WORK_RESULT_KEY to "Two"
)

class RxWorkerTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun rxWorkerWithoutOptionalParamProducesRawOutput() {
        val worker = TestListenableWorkerBuilder<RxWorkerExample>(
            context = context,
            inputData = RX_WORKER_INPUT_DATA
        ).build()

        val result = worker.createWork().test()
        result.assertResult(RX_WORKER_EXPECTED_RESULT)
    }

    @Test
    fun rxWorkerWithOptionalParamProducesCorrectOutput() {
        val worker = TestListenableWorkerBuilder<RxWorkerExample>(
            context = context,
            inputData = RX_WORKER_COMBINED_INPUT_DATA
        ).build()

        val result = worker.createWork().test()
        result.assertResult(RX_WORKER_EXPECTED_COMBINED_RESULT)
    }
}
