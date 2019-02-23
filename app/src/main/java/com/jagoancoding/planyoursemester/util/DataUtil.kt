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

object DataUtil {

    private const val PREFERENCES_DEFAULT = "DEFAULT_PREFERENCES"
    private const val NOTIF_ID_COUNTER = "NOTIFICATION_ID_COUNTER"

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