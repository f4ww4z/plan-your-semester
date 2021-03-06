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

package com.jagoancoding.planyoursemester

import android.app.Application
import android.os.Build
import com.jagoancoding.planyoursemester.util.DataUtil
import com.jagoancoding.planyoursemester.util.Notifier
import com.jakewharton.threetenabp.AndroidThreeTen

class App : Application() {

    companion object {
        const val DAYS_SINCE_PASSED: Long = 730L
        const val DAYS_PASSED: Long = 365L
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        AppRepository.init(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notifier.createDefaultNotificationChannel(this)
        }
    }
}