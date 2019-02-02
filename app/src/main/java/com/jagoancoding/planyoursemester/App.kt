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
import com.jakewharton.threetenabp.AndroidThreeTen

class App : Application() {

    companion object {
        const val DAYS_DISPLAYED_IN_OVERVIEW: Long = 730L
        const val DAYS_PASSED: Long = 1L
    }

    override fun onCreate() {
        super.onCreate()
        AppRepository.init(this)
        AndroidThreeTen.init(this)
    }
}