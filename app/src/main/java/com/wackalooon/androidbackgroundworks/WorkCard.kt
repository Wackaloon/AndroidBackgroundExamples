package com.wackalooon.androidbackgroundworks

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.work.Operation
import kotlinx.android.synthetic.main.work_card.view.*
import java.util.concurrent.Executor

class WorkCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.work_card, this)
    }

    fun setWork(operation: Operation, lifecycleOwner: LifecycleOwner) {
        tag_title.text = operation.toString()
        operation.state.observe(lifecycleOwner, Observer {
            status_value.text = it.toString()
        })
        val listener = Runnable {
            result_value.post {
                result_value.text = "Work is done"
            }
        }
        // will be used to launch callback
        val executor = Executor { runnable -> runnable?.run() }
        // listen for result, it's not the worker output! it's a terminal state of the operation
        operation.result.addListener(listener, executor)
    }
}
