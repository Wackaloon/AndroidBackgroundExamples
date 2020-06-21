package com.wackalooon.androidbackgroundworks

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker.Result
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.workDataOf
import com.wackalooon.androidbackgroundworks.workmanager.CoroutineWorkerExample
import com.wackalooon.androidbackgroundworks.workmanager.RxWorkerExample
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class RxWorkerTest {
    private lateinit var context: Context
    private lateinit var executor: Executor

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        executor = Executors.newSingleThreadExecutor()
    }

    @Test
    fun rxWorkerWithoutOptionalParamProducesRawOutput() {
        val worker = TestListenableWorkerBuilder<RxWorkerExample>(
                context = context,
                inputData = workDataOf(RxWorkerExample.WORK_INPUT_KEY to "Test")
        ).build()

        val result = worker.createWork().test()
        result.assertResult(Result.success(workDataOf(RxWorkerExample.WORK_RESULT_KEY to "TestRxnull")))
    }

    @Test
    fun rxWorkerWithOptionalParamProducesCorrectOutput() {
        val worker = TestListenableWorkerBuilder<RxWorkerExample>(
                context = context,
                inputData = workDataOf(
                        RxWorkerExample.WORK_INPUT_KEY to "Test",
                        CoroutineWorkerExample.WORK_RESULT_KEY to "Two")
        ).build()

        val result = worker.createWork().test()
        result.assertResult(Result.success(workDataOf(RxWorkerExample.WORK_RESULT_KEY to "TestRxTwo")))
    }
}
