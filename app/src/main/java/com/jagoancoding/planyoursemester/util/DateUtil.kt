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
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

object DateUtil {

    private const val DATE_FORMAT_STANDARD = "dd/MM/yyyy"
    private const val TIME_FORMAT_STANDARD = "hh:mm"
    private const val DATE_FORMAT_DAY = "EEE"
    private const val DATE_TIME_FORMAT_STANDARD = "dd/MM/yyyy hh:mm"

    fun getDayOfWeek(date: LocalDate): String = date.dayOfMonth.toString()

    fun getDayNameFromDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_DAY)
        return formatter.format(date)
    }

    fun getFormattedTime(date: Long): String {
        val localDate: LocalDateTime =
            Instant.ofEpochMilli(date)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        val dateTimeFormatter =
            DateTimeFormatter.ofPattern(TIME_FORMAT_STANDARD)
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

    fun getDate(epochMillis: Long): LocalDate =
        Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault())
            .toLocalDate()

    fun getDateTime(epochMillis: Long): LocalDateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault()
    )

    fun toEpochMili(date: LocalDate): Long =
        LocalDateTime.of(
            date,
            LocalTime.MIDNIGHT
        ).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    fun toEpochMili(date: String): Long =
        LocalDateTime.of(
            DateUtil.parseDateTime(date).toLocalDate(),
            LocalTime.MIDNIGHT
        ).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    fun parseDateTime(date: String): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_STANDARD)
        return LocalDateTime.parse(date, formatter)
    }

    fun formatDateWithTime(dateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern(TIME_FORMAT_STANDARD)
        return dateTime.format(formatter)
    }

    fun formatDate(year: Int, monthOfYear: Int, dayOfMonth: Int): String {
        val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_STANDARD)
        return LocalDate.of(year, monthOfYear, dayOfMonth).format(formatter)
    }

    fun formatTime(hour: Int, minute: Int): String {
        val formatter = DateTimeFormatter.ofPattern(TIME_FORMAT_STANDARD)
        return LocalTime.of(hour, minute).format(formatter)
    }
}