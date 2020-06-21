package com.wackalooon.androidbackgroundworks

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker.Result
import androidx.work.testing.TestWorkerBuilder
import androidx.work.workDataOf
import com.wackalooon.androidbackgroundworks.workmanager.CommonWorkerExample
import com.wackalooon.androidbackgroundworks.workmanager.retry
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executor
import java.util.concurrent.Executors

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
            inputData = workDataOf(CommonWorkerExample.WORK_INPUT_KEY to arrayOf("Test"))
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
            inputData = workDataOf(CommonWorkerExample.WORK_INPUT_KEY to arrayOf("Test"))
        ).build()
        // when
        worker.doWork()
        worker.doWork()
        val result = worker.doWork()
        // then
        assertEquals(
            Result.success(workDataOf(CommonWorkerExample.WORK_RESULT_KEY to "[Test]Commonnull")),
            result
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun commonWorkerThrowsExceptionForWrongInput() {
        // given
        retry = 1
        val worker = TestWorkerBuilder<CommonWorkerExample>(
            context = context,
            executor = executor,
            inputData = workDataOf("random_key" to "Test")
        ).build()
        // when
        worker.doWork()
        // then exception should occur
    }
}
