package com.wackalooon.androidbackgroundworks.workmanager.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.work.Operation
import androidx.work.WorkInfo
import com.wackalooon.androidbackgroundworks.R
import kotlinx.android.synthetic.main.work_card.view.*
import java.util.concurrent.Executor

private const val WORKERS_LAUNCHED = "Workers are launched"

class OperationCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.work_card, this)
    }

    fun setOperation(operation: Operation, lifecycleOwner: LifecycleOwner) {
        tag_title.text = operation.javaClass.simpleName
        operation.state.observe(lifecycleOwner, createObserver())
        // listen for result, it's not the worker output! it's a terminal state of the operation
        operation.result.addListener(createCompleteListener(), createPlainExecutor())
    }

    private fun createObserver(): Observer<Operation.State> {
        return Observer { state ->
            status_value.text = state.toString()
        }
    }

    private fun createCompleteListener(): Runnable {
        return Runnable {
            // switch to main thread, who known where it will be called from
            result_value.post {
                result_value.text = WORKERS_LAUNCHED
            }
        }
    }

    private fun createPlainExecutor(): Executor = Executor { runnable -> runnable?.run() }
}
