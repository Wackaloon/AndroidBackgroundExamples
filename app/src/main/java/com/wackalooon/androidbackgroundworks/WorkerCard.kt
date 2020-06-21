package com.wackalooon.androidbackgroundworks

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.android.synthetic.main.worker_card.view.*
import java.util.UUID

class WorkerCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.worker_card, this)
    }

    fun setWorker(id: UUID, lifecycleOwner: LifecycleOwner) {
        WorkManager.getInstance(context)
                .getWorkInfoByIdLiveData(id)
                .observe(lifecycleOwner, Observer { workInfo: WorkInfo? ->
                    val tag = workInfo?.tags?.minBy { it.length }
                    tag_title.text = tag
                    val progress = workInfo?.progress?.getInt("Progress", 0)
                    progress_value.text = "$progress%"
                    status_value.text = workInfo?.state.toString()
                    result_value.text = workInfo?.outputData?.getString(tag + "OutputKey")
                    run_attempt_value.text = workInfo?.runAttemptCount.toString()
                })
    }

    fun setWorker(tag: String, lifecycleOwner: LifecycleOwner) {
        WorkManager.getInstance(context)
                .getWorkInfosByTagLiveData(tag)
                .observe(lifecycleOwner, Observer { workInfoList: List<WorkInfo> ->
                    tag_title.text = tag
                    workInfoList.forEach { workInfo ->
                        progress_value.text = "Has ${workInfoList.size} workers"
                        status_value.text = workInfo.state.toString()
                        result_value.text = workInfo.outputData.getString(tag + "OutputKey")
                        run_attempt_value.text = workInfo.runAttemptCount.toString()
                    }
                })
    }
}
