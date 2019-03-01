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
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.io.File
import java.util.UUID

object DataUtil {

    private const val TAG = "DataUtil"

    //TODO: Store plan type in hashmap
    private const val SPLITTER = ","
    private const val MAP_INT_PLAN_EPOCH_FORMAT = "%d$SPLITTER%s"
    private const val MAP_STRING_PLAN_EPOCH_FORMAT = "%s$SPLITTER%s"

    fun getNotificationsMap(context: Context): HashMap<Int, PlanWithType> {
        val data = readFile(Notifier.NOTIFICATIONS_MAP_FILE, context)

        return toIntPlanType(data)
    }

    fun getNotificationWorksMap(context: Context): HashMap<String, PlanWithType> {
        val data =
            readFile(Notifier.NOTIFICATION_WORKERS_MAP_FILE, context)

        return toStringPlanTypeMap(data)
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
        id: Int, planItemId: Long, type: Int, context: Context
    ) {
        val updatedNotificationsMap = getNotificationsMap(context)
        updatedNotificationsMap[id] = PlanWithType(planItemId, type)
        val data = updatedNotificationsMap.toMappedString()

        writeListToFile(Notifier.NOTIFICATIONS_MAP_FILE, data, context)
    }

    fun setNotificationWorkOfId(
        id: String, planItemId: Long, type: Int, context: Context
    ) {
        val currentWorksMap = getNotificationWorksMap(context)
        currentWorksMap[id] = PlanWithType(planItemId, type)
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

    fun findNotificationEntry(
        context: Context, planWithType: PlanWithType
    ): Map.Entry<Int, PlanWithType>? {
        val notifications = DataUtil.getNotificationsMap(context)

        return notifications.maxBy {
            it.value == planWithType
        }
    }

    fun findNotificationWorkerEntry(
        context: Context, planWithType: PlanWithType
    ): Map.Entry<String, PlanWithType>? {
        val notificationWorkers = DataUtil.getNotificationWorksMap(context)

        return notificationWorkers.maxBy {
            it.value == planWithType
        }
    }

    fun findNotificationWorkerById(
        context: Context, notificationWorkId: UUID
    ): Map.Entry<String, PlanWithType>? {
        val notificationWorkers = DataUtil.getNotificationWorksMap(context)

        return notificationWorkers.maxBy {
            it.key == notificationWorkId.toString()
        }
    }

    fun removeNotificationAndWorkFromData(
        context: Context,
        planWithType: PlanWithType
    ) {
        val notifEntry = findNotificationEntry(context, planWithType)
        val notificationId = notifEntry?.key
        removeNotification(notificationId, context)

        val notifWorkEntry = findNotificationWorkerEntry(context, planWithType)
        val notificationWorkId = notifWorkEntry?.key
        removeNotificationWorker(notificationWorkId, context)
    }

    private fun removeNotification(notificationId: Int?, context: Context) {
        val currentNotifMap = getNotificationsMap(context)
        currentNotifMap.remove(notificationId)
        val data = currentNotifMap.toMappedString()

        writeListToFile(Notifier.NOTIFICATIONS_MAP_FILE, data, context)
        Log.i(TAG, "Notif removed: $notificationId")
    }

    private fun removeNotificationWorker(
        notificationWorkId: String?, context: Context
    ) {
        val currentWorkersMap = getNotificationWorksMap(context)
        currentWorkersMap.remove(notificationWorkId)
        val data = currentWorkersMap.toMappedString()

        writeListToFile(Notifier.NOTIFICATION_WORKERS_MAP_FILE, data, context)
        Log.i(TAG, "Notif worker removed: $notificationWorkId")
    }

    private fun <T> HashMap<T, PlanWithType>.toMappedString(): List<String> {
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

    private fun toIntPlanType(data: List<String>): HashMap<Int, PlanWithType> {
        if (data.isNullOrEmpty() || data[0].isEmpty()) {
            return hashMapOf()
        }

        val map = hashMapOf<Int, PlanWithType>()
        data.forEach {
            val key = it.split(SPLITTER)[0].toInt()

            map[key] = getPlanWithType(it)
        }
        return map
    }

    private fun toStringPlanTypeMap(data: List<String>): HashMap<String, PlanWithType> {
        if (data.isNullOrEmpty() || data[0].isEmpty()) {
            return hashMapOf()
        }

        val map = hashMapOf<String, PlanWithType>()
        data.forEach {
            val key = it.split(SPLITTER)[0]

            map[key] = getPlanWithType(it)
        }
        return map
    }

    private fun getPlanWithType(string: String): PlanWithType {
        val parts = string.split(SPLITTER)
        val id = parts[1].toLong()
        val type = parts[2].toInt()
        return PlanWithType(id, type)
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

    data class PlanWithType(val planItemId: Long, val type: Int) {

        companion object {
            private const val splitter = "%d$SPLITTER%d"
        }

        override fun toString(): String = splitter.format(planItemId, type)
    }
}