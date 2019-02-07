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

package com.jagoancoding.planyoursemester.model

import android.content.res.Resources
import com.jagoancoding.planyoursemester.db.Subject
import com.jagoancoding.planyoursemester.util.DateUtil

class PlanItem(
    val itemType: Int,
    val subject: Subject? = null,
    var name: String,
    var description: String? = null,
    var date: Long? = null,
    var startDate: Long? = null,
    var endDate: Long? = null,
    var isDone: Boolean? = null
) {
    companion object {
        const val TYPE_EXAM = 0
        const val TYPE_HOMEWORK = 1
        const val TYPE_EVENT = 2
        const val TYPE_REMINDER = 3
    }

    fun getDateToDisplay(r: Resources): String =
        when (itemType) {
            TYPE_EXAM -> DateUtil.getTimeStartEnd(startDate!!, endDate!!, r)
            TYPE_HOMEWORK -> DateUtil.getHomeworkDueTime(date!!, r)
            TYPE_EVENT -> DateUtil.getTimeStartEnd(startDate!!, endDate!!, r)
            TYPE_REMINDER -> DateUtil.getFormattedTime(date!!)
            else -> DateUtil.getFormattedTime(date ?: startDate!!)
        }
}