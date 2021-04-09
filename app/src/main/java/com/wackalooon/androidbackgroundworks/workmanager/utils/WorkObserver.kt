package com.wackalooon.androidbackgroundworks.workmanager.utils

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager

object WorkObserver {

    fun observe(uniqueWorkName: String, lifecycleOwner: LifecycleOwner, context: Context) {
        WorkManager.getInstance(context)
                .getWorkInfosForUniqueWorkLiveData(uniqueWorkName)
                .observe(lifecycleOwner, Observer { workInfoList ->
                    workInfoList.forEach { workInfo ->
                        // each worker have long default tag and my custom short one, select short one
                        val tag = workInfo.tags.minByOrNull { it.length }!!

                        if (workInfo.state != WorkInfo.State.SUCCEEDED) {
                            return@forEach
                        }
                        val successOutputData = workInfo.outputData
                        // in this example all workers have Tag + "OutputKey" as output key
                        val output = successOutputData.getString(tag + "OutputKey")

                        Toast.makeText(context, "Worker: $tag is done with $output", Toast.LENGTH_LONG).show()
                    }
                })
    }
}
