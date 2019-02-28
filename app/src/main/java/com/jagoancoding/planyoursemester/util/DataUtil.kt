/*
 * Copyright 2019 Maharaj Fawwaz Almuqaddim Yusran
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jagoancoding.planyoursemester.util

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.io.File

object DataUtil {

    private const val TAG = "DataUtil"

    //TODO: Store plan type in hashmap
    private const val SPLITTER = ","
    private const val MAP_INT_PLAN_EPOCH_FORMAT = "%d$SPLITTER%s"
    private const val MAP_STRING_PLAN_EPOCH_FORMAT = "%s$SPLITTER%s"

    fun getNotificationsMap(context: Context): HashMap<Int, PlanWithEpoch> {
        val data = readFile(Notifier.NOTIFICATIONS_MAP_FILE, context)

        return toIntPlanEpoch(data)
    }

    fun getNotificationWorksMap(context: Context): HashMap<String, PlanWithEpoch> {
        val data =
            readFile(Notifier.NOTIFICATION_WORKERS_MAP_FILE, context)

        return toStringPlanEpochMap(data)
    }

    private fun readFile(filePath: String, context: Context): List<String> {
        val file = File(context.filesDir, filePath)

        // If file doesn't exist, create it
        file.createNewFile()

        val br = file.bufferedReader()
        val data = br.readLines()
        br.close()
        return data
    }

    fun setNotificationOfId(
        id: Int, planEpoch: PlanWithEpoch, context: Context
    ) {
        val updatedNotificationsMap = getNotificationsMap(context)
        updatedNotificationsMap[id] = planEpoch
        val data = updatedNotificationsMap.toMappedString()

        writeListToFile(Notifier.NOTIFICATIONS_MAP_FILE, data, context)
    }

    fun setNotificationWorkOfId(
        id: String, planEpoch: PlanWithEpoch, context: Context
    ) {
        val currentWorksMap = getNotificationWorksMap(context)
        currentWorksMap[id] = planEpoch
        val data = currentWorksMap.toMappedString()

        writeListToFile(Notifier.NOTIFICATION_WORKERS_MAP_FILE, data, context)
    }

    fun newNotificationId(context: Context): Int {
        val notifications = getNotificationsMap(context)
        if (notifications.isNullOrEmpty()) {
            return Notifier.BASE_NOTIFICATION_ID_COUNT
        }
        val latestId = notifications.keys.max()!!
        return latestId + 1
    }

    private fun writeListToFile(
        filePath: String, data: List<String>, context: Context
    ) {
        val fos = context.openFileOutput(
            filePath, Context.MODE_PRIVATE
        )!!
        fos.bufferedWriter().use { out ->
            data.forEach {
                out.write(it)
                out.newLine()
            }
        }
    }

    private fun <T> HashMap<T, PlanWithEpoch>.toMappedString(): List<String> {
        val data = mutableListOf<String>()
        forEach {
            val line =
                if (it.key is Int)
                    MAP_INT_PLAN_EPOCH_FORMAT.format(
                        it.key,
                        it.value.toString()
                    )
                else
                    MAP_STRING_PLAN_EPOCH_FORMAT.format(
                        it.key,
                        it.value.toString()
                    )

            data.add(line)
        }
        return data
    }

    private fun toIntPlanEpoch(data: List<String>): HashMap<Int, PlanWithEpoch> {
        if (data.isNullOrEmpty() || data[0].isEmpty()) {
            return hashMapOf()
        }

        val map = hashMapOf<Int, PlanWithEpoch>()
        data.forEach {
            val key = it.split(SPLITTER)[0].toInt()

            map[key] = getPlanWithEpoch(it)
        }
        return map
    }

    private fun toStringPlanEpochMap(data: List<String>): HashMap<String, PlanWithEpoch> {
        if (data.isNullOrEmpty() || data[0].isEmpty()) {
            return hashMapOf()
        }

        val map = hashMapOf<String, PlanWithEpoch>()
        data.forEach {
            val key = it.split(SPLITTER)[0]

            map[key] = getPlanWithEpoch(it)
        }
        return map
    }

    private fun getPlanWithEpoch(string: String): PlanWithEpoch {
        val parts = string.split(SPLITTER)
        val id = parts[1].toLong()
        val epoch = parts[2].toLong()
        return PlanWithEpoch(id, epoch)
    }

    fun <T> LiveData<T>.observeOnce(observer: Observer<T>) {
        observeForever(object : Observer<T> {
            override fun onChanged(t: T?) {
                if (t != null) {
                    observer.onChanged(t)
                }
                removeObserver(this)
            }
        })
    }

    data class PlanWithEpoch(val planItemId: Long, val epoch: Long) {

        companion object {
            private const val splitter = "%d$SPLITTER%d"
        }

        override fun toString(): String = splitter.format(planItemId, epoch)
    }
}