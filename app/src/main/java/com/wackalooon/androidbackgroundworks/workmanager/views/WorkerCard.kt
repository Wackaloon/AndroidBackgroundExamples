package com.wackalooon.androidbackgroundworks.workmanager.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.wackalooon.androidbackgroundworks.R
import kotlinx.android.synthetic.main.worker_card.view.*
import java.util.UUID

private const val PROGRESS_KEY = "Progress"
private const val PROGRESS_DEFAULT = 0

class WorkerCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.worker_card, this)
    }

    /**
     * Observe worker with unique UUID of the worker.
     */
    fun setWorker(id: UUID, lifecycleOwner: LifecycleOwner) {
        WorkManager.getInstance(context)
            .getWorkInfoByIdLiveData(id)
            .observe(lifecycleOwner, Observer { workInfo: WorkInfo? ->
                val tag = getTag(workInfo)
                setTitle(tag)
                setProgress(calculateProgressText(workInfo))
                handleWorkInfo(tag, workInfo)
            })
    }

    private fun getTag(workInfo: WorkInfo?): String? {
        // hack, we are interested in only one worker and it has tag that is smallest
        return workInfo?.tags?.minByOrNull { it.length }
    }

    private fun calculateProgressText(workInfo: WorkInfo?): String {
        val progress = workInfo?.progress?.getInt(PROGRESS_KEY, PROGRESS_DEFAULT)
        return "$progress%"
    }

    private fun setTitle(title: String?){
        tag_title.text = title
    }

    private fun setProgress(progress: String?) {
        progress_value.text = progress
    }

    /**
     * Observe all workers with particular TAG.
     */
    fun setWorker(tag: String, lifecycleOwner: LifecycleOwner) {
        setTitle(tag)
        WorkManager.getInstance(context)
            .getWorkInfosByTagLiveData(tag)
            .observe(lifecycleOwner, Observer { workInfoList: List<WorkInfo> ->
                setProgress("Has ${workInfoList.size} workers")
                workInfoList.forEach { workInfo ->
                    handleWorkInfo(tag, workInfo)
                }
            })
    }

    private fun handleWorkInfo(tag: String?, workInfo: WorkInfo?) {
        status_value.text = workInfo?.state.toString()
        result_value.text = workInfo?.outputData?.getString(tag + "OutputKey")
        run_attempt_value.text = workInfo?.runAttemptCount.toString()
    }
}
