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
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.io.File

object DataUtil {

    private const val TAG = "DataUtil"

    private const val PREFERENCES_DEFAULT = "DEFAULT_PREFERENCES"
    private const val NOTIF_ID_COUNTER = "NOTIFICATION_ID_COUNTER"

    private const val MAP_SPLITTER = ","
    private const val MAP_INT_LONG_FORMAT = "%d$MAP_SPLITTER%d"

    lateinit var prefs: SharedPreferences

    fun init(applicationContext: Context) {
        prefs = applicationContext.getSharedPreferences(
            PREFERENCES_DEFAULT, Context.MODE_PRIVATE
        )

        setNotifIdCounter(Notifier.BASE_NOTIFICATION_ID_COUNT)
    }

    fun getNotifIdCounter(): Int {
        val counter = prefs.getInt(NOTIF_ID_COUNTER, 0)

        // Increase id by 1
        prefs.edit().apply {
            putInt(NOTIF_ID_COUNTER, counter + 1)
        }.apply()

        return counter
    }

    private fun setNotifIdCounter(number: Int) {
        prefs.edit().apply {
            putInt(NOTIF_ID_COUNTER, number)
        }.apply()
    }

    fun getNotificationsMap(context: Context): HashMap<Int, Long> {
        val file = File(context.filesDir, Notifier.NOTIFICATIONS_MAP_FILE)

        // If file doesn't exist, create it
        if (!file.exists()) {
            file.bufferedWriter().use { it.write("") }
            return hashMapOf()
        }

        val br = file.bufferedReader()
        val data = br.readLines()
        br.close()

        return toIntLongMap(data)
    }

    fun setNotificationOfId(id: Int, epoch: Long, context: Context) {
        val updatedNotificationsMap = getNotificationsMap(context)
        updatedNotificationsMap[id] = epoch
        val data = updatedNotificationsMap.toMappedString()

        val fos = context.openFileOutput(
            Notifier.NOTIFICATIONS_MAP_FILE, Context.MODE_PRIVATE
        )!!
        fos.bufferedWriter().use { out ->
            data.forEach {
                out.write(it)
                out.newLine()
            }
        }
    }

    fun newNotificationId(context: Context): Int {
        val notifications = getNotificationsMap(context)
        if (notifications.isNullOrEmpty()) {
            return Notifier.BASE_NOTIFICATION_ID_COUNT
        }
        val latestId = notifications.keys.max()!!
        return latestId + 1
    }

    private fun HashMap<Int, Long>.toMappedString(): List<String> {
        val data = mutableListOf<String>()
        forEach {
            val line = MAP_INT_LONG_FORMAT.format(it.key, it.value)
            data.add(line)
        }
        return data
    }

    private fun toIntLongMap(data: List<String>): HashMap<Int, Long> {
        if (data.isNullOrEmpty() || data[0].isEmpty()) {
            return hashMapOf()
        }

        val map = hashMapOf<Int, Long>()
        data.forEach {
            val parts = it.split(MAP_SPLITTER)
            val key = parts[0].toInt()
            val value = parts[1].toLong()
            map[key] = value
        }
        return map
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
}