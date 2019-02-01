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

import android.content.res.Resources
import com.jagoancoding.planyoursemester.R
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

object DateUtil {

    private const val DATE_FORMAT_STANDARD = "hh:mm"

    fun getDayOfMonthFromDate(date: Long): Int {
        val localDate: LocalDate =
            Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault())
                .toLocalDate()
        return localDate.dayOfMonth
    }

    fun getDayNameFromDate(date: Long, resources: Resources): String {
        val shortDayNames: Array<String> =
            resources.getStringArray(R.array.en_day_name_three_chars)
        return shortDayNames[getDayOfMonthFromDate(date)]
    }

    fun getFormattedTime(date: Long): String {
        val localDate: LocalDate =
            Instant.ofEpochMilli(date)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        val dateTimeFormatter =
            DateTimeFormatter.ofPattern(DATE_FORMAT_STANDARD)
        return localDate.format(dateTimeFormatter)
    }

    fun getHomeworkDueTime(date: Long, r: Resources): String =
        r.getString(R.string.homework_date, getFormattedTime(date))

    fun getTimeStartEnd(startDate: Long, endDate: Long, r: Resources): String =
        r.getString(
            R.string.date_start_end,
            getFormattedTime(startDate),
            getFormattedTime(endDate)
        )
}