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

import android.graphics.Color
import com.jagoancoding.planyoursemester.db.Subject
import com.jagoancoding.planyoursemester.model.DateItem
import com.jagoancoding.planyoursemester.model.PlanItem
import com.jagoancoding.planyoursemester.util.DateUtil
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

object TestUtil {

    private var idCounter: Long = 1001
        get() = field++

    // Test data
    val testZoneId = ZoneId.of("America/Los_Angeles")
    val maths = Subject("Maths", Color.GREEN)
    val music = Subject("Music", Color.BLACK)
    val science = Subject("Science", Color.WHITE)
    val english = Subject("English", Color.GRAY)

    val startDate1: LocalDateTime = LocalDateTime.of(2019, 2, 16, 6, 0)
    val endDate1: LocalDateTime = LocalDateTime.of(2019, 2, 16, 9, 0)
    val startDate2: LocalDateTime = LocalDateTime.of(2011, 1, 1, 14, 0)
    val endDate2: LocalDateTime = LocalDateTime.of(2011, 1, 1, 16, 30)
    val startDate3: LocalDateTime = LocalDateTime.of(2009, 7, 14, 9, 15)
    val endDate3: LocalDateTime = LocalDateTime.of(2000, 7, 14, 10, 45)
    val date4: LocalDateTime = LocalDateTime.of(2009, 8, 9, 18, 0)

    var date1Start: LocalDate = LocalDate.of(2009, 1, 1)
    var date1End: LocalDate = LocalDate.of(2010, 1, 1)
    var date2Start: LocalDate = LocalDate.of(2017, 1, 1)
    var date2End: LocalDate = LocalDate.of(2017, 4, 1)

    val planItemMathsExam =
        PlanItem(
            PlanItem.TYPE_EXAM,
            idCounter,
            maths,
            "Maths exam 1",
            null,
            null,
            DateUtil.toEpochMili(startDate1.toLocalDate()),
            DateUtil.toEpochMili(endDate1.toLocalDate()),
            null
        )

    val planItemMusicExam =
        PlanItem(
            PlanItem.TYPE_EXAM,
            idCounter,
            music,
            "Piano daily test",
            null,
            null,
            DateUtil.toEpochMili(startDate2.toLocalDate()),
            DateUtil.toEpochMili(endDate2.toLocalDate()),
            null
        )

    val planItemMathsHomework =
        PlanItem(
            PlanItem.TYPE_HOMEWORK,
            idCounter,
            maths,
            "Algebra sheet 2",
            "Need help from friends",
            DateUtil.toEpochMili(date4.toLocalDate()),
            null,
            null,
            false
        )

    val planItemEvent1 =
        PlanItem(
            PlanItem.TYPE_EVENT,
            idCounter,
            null,
            "Party",
            "Get friends together",
            null,
            DateUtil.toEpochMili(startDate3.toLocalDate()),
            DateUtil.toEpochMili(endDate3.toLocalDate()),
            null
        )

    val planItemReminder1 =
        PlanItem(
            PlanItem.TYPE_REMINDER,
            idCounter,
            null,
            "Buy eggs",
            null,
            DateUtil.toEpochMili(date4.toLocalDate()),
            null,
            null,
            true
        )

    fun generateSampleDateItemList(
        start: LocalDate, end: LocalDate
    ): List<DateItem> {
        val expectedDateItems: ArrayList<DateItem> = ArrayList()
        var currentDate = start

        while (currentDate.isBefore(end.minusDays(1))) {

            val dateItem = DateItem(currentDate, generateSamplePlanItems(5))
            expectedDateItems.add(dateItem)
            currentDate = currentDate.plusDays(1)
        }
        return expectedDateItems
    }

    fun generateDateItemListWithSubjectCount(
        start: LocalDate, end: LocalDate, subjectName: String, count: Int
    ): List<DateItem> {
        val expectedDateItems: ArrayList<DateItem> = ArrayList()
        var currentDate = start

        for (i in 1..count) {

            val dateItem = DateItem(
                currentDate, generatePlanItemsOfSubject(subjectName, 1)
            )
            expectedDateItems.add(dateItem)
            currentDate = currentDate.plusDays(1)
        }
        while (currentDate.isBefore(end.minusDays(1))) {

            val dateItem =
                DateItem(currentDate, generatePlanItemsOfSubject("random", 2))
            expectedDateItems.add(dateItem)
            currentDate = currentDate.plusDays(1)
        }
        return expectedDateItems
    }

    private fun generateSamplePlanItems(size: Int): ArrayList<PlanItem> {
        val generatedPlanItems: ArrayList<PlanItem> = ArrayList()

        for (i in 1..(size - 1)) {

            val planItem = when (i % 5) {
                0 -> planItemMathsExam
                1 -> planItemMusicExam
                2 -> planItemMathsHomework
                3 -> planItemEvent1
                else -> planItemReminder1
            }
            generatedPlanItems.add(planItem)
        }
        return generatedPlanItems
    }

    private fun generatePlanItemsOfSubject(
        subjectName: String, count: Int
    ): ArrayList<PlanItem> {
        val generatedPlanItems: ArrayList<PlanItem> = ArrayList()
        for (i in 1..count) {
            when (subjectName.toLowerCase()) {
                "maths" -> generatedPlanItems.add(planItemMathsExam)
                "music" -> generatedPlanItems.add(planItemMusicExam)
                else -> generatedPlanItems.add(planItemEvent1)
            }
        }
        return generatedPlanItems
    }

    fun subjectIds() = listOf(maths, music, science, english).map { it.name }
}