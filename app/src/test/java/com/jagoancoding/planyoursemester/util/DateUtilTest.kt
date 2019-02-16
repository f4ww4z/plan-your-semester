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
import android.content.res.Resources
import androidx.test.core.app.ApplicationProvider
import com.jagoancoding.planyoursemester.AppRepository
import com.jagoancoding.planyoursemester.model.DateItem
import com.jagoancoding.planyoursemester.util.DateUtil.findDatePositionInList
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId

@RunWith(RobolectricTestRunner::class)
open class DateUtilTest {

    lateinit var mResources: Resources

    private lateinit var date15_02_2019: LocalDate
    private var time2: String = "02:40"
    private var epoch2: Long = 1550313640000L
    private var expectedDueDate2: String = "Due 02:40"

    private var date3String: String = "15/02/2019"
    private var time3String: String = "22:25"
    private lateinit var time3: LocalTime
    private lateinit var dateTime3: LocalDateTime
    private var dateTime3String: String = "15/02/2019 22:25"
    private var epoch3: Long = 1550240726945L
    private var dateTime4String: String = "15/02/2019 22:27"
    private lateinit var dateTime4: LocalDateTime
    private var time4String: String = "22:27"
    private var epoch4: Long = 1550298420000L
    private var expectedStartEnd3_4 = "06:25 - 22:27"
    private var date5String = "26/01/2019"
    private lateinit var lDate5: LocalDate
    private var epoch5: Long = 1548489600000L

    private lateinit var date1Start: LocalDate
    private lateinit var date1End: LocalDate
    private lateinit var date1InTheMiddle: LocalDate

    @Before
    fun setup() {
        date15_02_2019 = LocalDate.of(2019, 2, 15)
        dateTime3 = LocalDateTime.of(2019, 2, 15, 22, 25)
        time3 = LocalTime.of(22, 25)
        dateTime4 = LocalDateTime.of(2019, 2, 15, 22, 27)
        lDate5 = LocalDate.of(2019, 1, 26)

        date1Start = LocalDate.of(2009, 1, 1)
        date1End = LocalDate.of(2010, 1, 1)
        date1InTheMiddle = LocalDate.of(2009, 6, 25)

        mResources =
            ApplicationProvider.getApplicationContext<Context>().resources

        AppRepository.zoneId = ZoneId.of("America/Los_Angeles")
    }

    @Test
    fun getDayOfWeek_ShouldReturn15_When15_02_2019() {
        val actual = DateUtil.getDayOfWeek(date15_02_2019)
        Assert.assertEquals("15", actual)
    }

    @Test
    fun getDayNameFromDate_ShouldReturnFri_When15_02_2019() {
        val actual = DateUtil.getDayNameFromDate(date15_02_2019)
        Assert.assertEquals("Fri", actual)
    }

    @Test
    fun getFormattedTime_ShouldReturn16_05_GivenEpochMilli() {
        val actual = DateUtil.getFormattedTime(epoch2)
        Assert.assertEquals(time2, actual)
    }

    @Test
    fun getHomeworkDueTime_ShouldReturnCorrectFormat_GivenEpochMilli() {
        val actual = DateUtil.getHomeworkDueTime(epoch2, mResources)
        Assert.assertEquals(expectedDueDate2, actual)
    }

    @Test
    fun getTimeStartEnd_ShouldReturnCorrectFormat_GivenTwoEpochMillis() {
        val actual = DateUtil.getTimeStartEnd(epoch3, epoch4, mResources)
        Assert.assertEquals(expectedStartEnd3_4, actual)
    }

    @Test
    fun getDate_ShouldReturnDate_GivenEpochMilli() {
        val actual = DateUtil.getDate(epoch5)
        Assert.assertTrue(actual.isEqual(lDate5))
    }

    @Test
    fun getDateTime_ShouldReturnDateTime_GivenEpochMilli() {
        val actual = DateUtil.getDateTime(epoch4)
        Assert.assertTrue(actual.isEqual(dateTime4))
    }

    @Test
    fun toEpochMili_ShouldReturnCorrectEpochMilli_GivenLocalDate() {
        val actual = DateUtil.toEpochMili(lDate5)
        Assert.assertEquals(epoch5, actual)
    }

    @Test
    fun toEpochMili_ShouldReturnCorrectEpochMilli_GivenDateTimeInString() {
        val actual = DateUtil.toEpochMili(dateTime4String, ViewUtil.DATE_TIME)
        Assert.assertEquals(epoch4, actual)
    }

    @Test
    fun toEpochMili_ShouldReturn0_WhenIncorrectFormat() {
        val actual = DateUtil.toEpochMili(dateTime4String, 11)
        Assert.assertEquals(0L, actual)
    }

    @Test
    fun parseDate_ShouldReturnCorrectLocalDate_GivenString() {
        val actual = DateUtil.parseDate(date5String)
        Assert.assertTrue(actual.isEqual(lDate5))
    }

    @Test
    fun parseTime_ShouldReturnCorrectLocalTime_GivenString() {
        val actual = DateUtil.parseTime(time3String)
        Assert.assertEquals(time3, actual)
    }

    @Test
    fun formatDateWithTime_ShouldReturnCorrectFormat_GivenLocalDateTime() {
        val actual = DateUtil.formatDateWithTime(dateTime3)
        Assert.assertEquals(dateTime3String, actual)
    }

    @Test
    fun formatDate_ShouldReturnCorrectFormat_GivenLocalDate() {
        val actual = DateUtil.formatDate(
            dateTime3.year, dateTime3.monthValue, dateTime3.dayOfMonth
        )
        Assert.assertEquals(date3String, actual)
    }

    @Test
    fun formatTime_ShouldReturnCorrectFormat_GivenLocalTime() {
        val actual = DateUtil.formatTime(dateTime4.hour, dateTime4.minute)
        Assert.assertEquals(time4String, actual)
    }

    @Test
    fun findDatePositionInList_ReturnIndexOfDateItemList_WhenLocalDateIs15_02_2019() {
        val expectedDateItems: ArrayList<DateItem> = ArrayList()
        var currentDate = date1Start
        while (currentDate.isBefore(date1End)) {
            val dateItem = DateItem(currentDate, ArrayList())
            expectedDateItems.add(dateItem)
            currentDate = currentDate.plusDays(1)
        }

        val actualIndex =
            expectedDateItems.findDatePositionInList(date1InTheMiddle)
        Assert.assertEquals(175, actualIndex)
    }
}