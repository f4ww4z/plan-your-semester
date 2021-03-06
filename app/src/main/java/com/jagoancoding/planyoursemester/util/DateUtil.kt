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
import com.jagoancoding.planyoursemester.AppRepository
import com.jagoancoding.planyoursemester.R
import com.jagoancoding.planyoursemester.model.DateItem
import com.jagoancoding.planyoursemester.model.ListItem
import com.jagoancoding.planyoursemester.model.PlanItem
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

object DateUtil {

    private const val DATE_FORMAT_STANDARD = "dd/MM/yyyy"
    private const val TIME_FORMAT_STANDARD = "HH:mm"
    private const val DATE_FORMAT_DAY = "EEE"
    private const val DATE_TIME_FORMAT_STANDARD = "dd/MM/yyyy HH:mm"
    private const val MONTH_YEAR_FORMAT = "LLLL yyyy"

    fun getDayOfWeek(date: LocalDate): String = date.dayOfMonth.toString()

    fun getDayNameFromDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_DAY)
            .withZone(AppRepository.zoneId)
        return formatter.format(date)
    }

    fun getFormattedTime(date: Long): String {
        val localDate: LocalDateTime =
            Instant.ofEpochMilli(date)
                .atZone(AppRepository.zoneId).toLocalDateTime()
        val dateTimeFormatter =
            DateTimeFormatter.ofPattern(TIME_FORMAT_STANDARD)
                .withZone(AppRepository.zoneId)
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
        Instant.ofEpochMilli(epochMillis).atZone(AppRepository.zoneId)
            .toLocalDate()

    fun getTime(epochMillis: Long): LocalTime =
        Instant.ofEpochMilli(epochMillis).atZone(AppRepository.zoneId)
            .toLocalTime()

    fun getDateTime(epochMillis: Long): LocalDateTime = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(epochMillis), AppRepository.zoneId
    )

    fun toEpochMili(date: LocalDate): Long =
        LocalDateTime.of(date, LocalTime.MIDNIGHT)
            .atZone(AppRepository.zoneId).toInstant().toEpochMilli()

    fun toEpochMili(date: String, format: Int): Long = when (format) {
        ViewUtil.DATE -> {
            LocalDateTime.of(
                parseDate(date),
                LocalTime.MIDNIGHT
            ).atZone(AppRepository.zoneId).toInstant().toEpochMilli()
        }
        ViewUtil.TIME -> {
            LocalDateTime.of(
                LocalDate.now(ZoneId.systemDefault()),
                parseTime(date)
            ).atZone(AppRepository.zoneId).toInstant().toEpochMilli()
        }
        ViewUtil.DATE_TIME -> {
            parseDateTime(date).atZone(AppRepository.zoneId).toInstant()
                .toEpochMilli()
        }
        else -> 0L
    }

    fun toEpochMilli(dateTime: LocalDateTime): Long =
        dateTime.atZone(AppRepository.zoneId).toInstant().toEpochMilli()

    fun parseDateTime(date: String): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_STANDARD)
        return LocalDateTime.parse(date, formatter)
    }

    fun parseDate(date: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_STANDARD)
            .withZone(AppRepository.zoneId)
        return LocalDate.parse(date, formatter)
    }

    fun parseTime(time: String): LocalTime {
        val formatter = DateTimeFormatter.ofPattern(TIME_FORMAT_STANDARD)
            .withZone(AppRepository.zoneId)
        return LocalTime.parse(time, formatter)
    }

    fun formatDateWithTime(dateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_STANDARD)
            .withZone(AppRepository.zoneId)
        return dateTime.format(formatter)
    }

    fun formatDate(year: Int, monthOfYear: Int, dayOfMonth: Int): String {
        val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_STANDARD)
            .withZone(AppRepository.zoneId)
        return LocalDate.of(year, monthOfYear, dayOfMonth).format(formatter)
    }

    fun formatTime(hour: Int, minute: Int): String {
        val formatter = DateTimeFormatter.ofPattern(TIME_FORMAT_STANDARD)
            .withZone(AppRepository.zoneId)
        return LocalTime.of(hour, minute).format(formatter)
    }

    fun List<ListItem>.findDatePositionInList(date: LocalDate): Int =
        this.indexOfFirst {
            it.getType() == ListItem.TYPE_DATE &&
                    (it as DateItem).date.isEqual(date)
        }

    fun getFormattedMonthAndYear(month: Int, year: Int): String {
        val date: LocalDate = LocalDate.of(year, month, 1)
        val formatter = DateTimeFormatter.ofPattern(MONTH_YEAR_FORMAT)
        return formatter.format(date)
    }

    fun getEpoch(planItem: PlanItem): Long {
        with(planItem) {
            return when (itemType) {
                PlanItem.TYPE_EXAM -> {
                    startDate!!
                }
                PlanItem.TYPE_HOMEWORK -> {
                    date!!
                }
                PlanItem.TYPE_EVENT -> {
                    startDate!!
                }
                else -> {
                    planItem.date!!
                }
            }
        }
    }
}