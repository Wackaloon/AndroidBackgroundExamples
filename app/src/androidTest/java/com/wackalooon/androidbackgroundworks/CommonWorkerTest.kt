package com.wackalooon.androidbackgroundworks

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker.Result
import androidx.work.testing.TestWorkerBuilder
import androidx.work.workDataOf
import com.wackalooon.androidbackgroundworks.workmanager.workers.CommonWorkerExample
import com.wackalooon.androidbackgroundworks.workmanager.workers.retry
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executor
import java.util.concurrent.Executors

private val COMMON_WORKER_EXPECTED_RESULT = Result.success(
    workDataOf(
        CommonWorkerExample.WORK_RESULT_KEY to "[Test]Commonnull"
    )
)

private val COMMON_WORKER_INPUT_DATA = workDataOf(
    CommonWorkerExample.WORK_INPUT_KEY to arrayOf("Test")
)

private val COMMON_WORKER_INVALID_INPUT_DATA = workDataOf(
    "random_string" to arrayOf("Test")
)

class CommonWorkerTest {
    private lateinit var context: Context
    private lateinit var executor: Executor

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        executor = Executors.newSingleThreadExecutor()
    }

    @Test
    fun commonWorkerProduceTwoRetries() {
        // given
        retry = 1
        val worker = TestWorkerBuilder<CommonWorkerExample>(
            context = context,
            executor = executor,
            inputData = COMMON_WORKER_INPUT_DATA
        ).build()
        // when
        val result1 = worker.doWork()
        val result2 = worker.doWork()
        // then
        assertEquals(Result.retry(), result1)
        assertEquals(Result.retry(), result2)
    }

    @Test
    fun commonWorkerProduceResultOnThirdTry() {
        // given
        retry = 1
        val worker = TestWorkerBuilder<CommonWorkerExample>(
            context = context,
            executor = executor,
            inputData = COMMON_WORKER_INPUT_DATA
        ).build()
        // when
        worker.doWork()
        worker.doWork()
        val result = worker.doWork()
        // then
        assertEquals(COMMON_WORKER_EXPECTED_RESULT, result)
    }

    @Test(expected = IllegalArgumentException::class)
    fun commonWorkerThrowsExceptionForWrongInput() {
        // given
        retry = 1
        val worker = TestWorkerBuilder<CommonWorkerExample>(
            context = context,
            executor = executor,
            inputData = COMMON_WORKER_INVALID_INPUT_DATA
        ).build()
        // when
        worker.doWork()
        // then exception should occur
    }
}
