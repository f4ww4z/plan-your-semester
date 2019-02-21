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

package com.jagoancoding.planyoursemester.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.jagoancoding.planyoursemester.AppRepository
import com.jagoancoding.planyoursemester.TestUtil
import com.jagoancoding.planyoursemester.model.DateItem
import com.jagoancoding.planyoursemester.model.ListItem
import com.jagoancoding.planyoursemester.util.DataUtil.observeOnce
import com.jagoancoding.planyoursemester.util.DateUtil
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.threeten.bp.ZoneId

@RunWith(RobolectricTestRunner::class)
open class MainViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    lateinit var viewModel: MainViewModel

    lateinit var defaultZoneId: ZoneId

    @Before
    fun setup() {
        defaultZoneId = AppRepository.zoneId
        AppRepository.zoneId = TestUtil.testZoneId

        AppRepository.startDate = TestUtil.date1Start
        AppRepository.endDate = TestUtil.date1End

        viewModel = MainViewModel()
    }

    @Test
    fun viewModelShouldHaveInitialDateItemsPlusDividersWhenLoadedForTheFirstTime() {
        val actual = viewModel.listItems
        val expected = TestUtil.generateSampleDateItemList(
            TestUtil.date1Start, TestUtil.date1End
        )
        actual.observeOnce(Observer { dateItems ->
            Assert.assertEquals(expected.size + 12, dateItems!!.size)
        })
    }

    @Test
    fun initialDateItemsShouldHave365DateItemsWhenGiven2009To2010() {

        val expected = TestUtil.generateSampleDateItemList(
            TestUtil.date1Start, TestUtil.date1End
        )
        var actual =
            viewModel.initialListItems(TestUtil.date1Start, TestUtil.date1End)
        actual = actual.filter { it.getType() == ListItem.TYPE_DATE }
        Assert.assertEquals(expected.size, actual.size)
    }

    @Test
    fun subjectInstanceShouldBe4When4PlansAreOfMathsSubject() {
        // reset subject instances
        AppRepository.subjectInstances = HashMap()

        viewModel.countSubjectUsage(
            TestUtil.subjectIds(),
            TestUtil.generateDateItemListWithSubjectCount(
                TestUtil.date2Start, TestUtil.date2End, "Maths", 4
            )
        )
        Assert.assertEquals(4, AppRepository.subjectInstances["Maths"])
    }

    @Test
    fun subjectInstanceShouldBe0WhenPlansAreAllEventsOrReminders() {
        // reset subject instances
        AppRepository.subjectInstances = HashMap()

        viewModel.countSubjectUsage(
            TestUtil.subjectIds(),
            TestUtil.generateDateItemListWithSubjectCount(
                TestUtil.date2Start, TestUtil.date2End, "Random", 4
            )
        )
        Assert.assertEquals(0, AppRepository.subjectInstances["Maths"])
    }

    @Test
    fun subjectInstanceShouldBeNullOrZeroWhenNoPlanItemsOfScienceSubject() {
        // reset subject instances
        AppRepository.subjectInstances = HashMap()

        viewModel.countSubjectUsage(
            TestUtil.subjectIds(),
            TestUtil.generateDateItemListWithSubjectCount(
                TestUtil.date1Start, TestUtil.date1End, "Science", 50
            )
        )
        Assert.assertTrue(
            AppRepository.subjectInstances["Science"] == null ||
                    AppRepository.subjectInstances["Science"] == 0
        )
    }

    @Test
    fun planItemShouldBeDisplayedWhenAdded() {
        // Reset date items
        viewModel.resetData()

        // Add a new event and check if it is in the listItems list
        val p = TestUtil.planItemEvent1
        viewModel.displayPlan(p)

        viewModel.listItems.observeOnce(Observer { listItems ->
            val dateItems =
                listItems.filter { it.getType() == ListItem.TYPE_DATE }

            val dateItemToBeAddedToIndex =
                dateItems.indexOfFirst {
                    (it as DateItem).date.isEqual(DateUtil.getDate(p.startDate!!))
                }
            val planList =
                (dateItems[dateItemToBeAddedToIndex] as DateItem).planItems

            Assert.assertTrue(planList.contains(p))
        })
    }

    @Test
    fun displayedPlanItemShouldBeUpdated() {
        // Reset date items
        viewModel.resetData()

        // Test a Reminder
        val p = TestUtil.planItemReminder1
        viewModel.displayPlan(p)

        viewModel.listItems.observeOnce(Observer { listItems ->
            val dateItems =
                listItems.filter { it.getType() == ListItem.TYPE_DATE }

            val updatedDateItemIndex =
                dateItems.indexOfFirst {
                    (it as DateItem).date.isEqual(DateUtil.getDate(p.date!!))
                }
            Assert.assertNotEquals(-1, updatedDateItemIndex)

            val planList =
                (dateItems[updatedDateItemIndex] as DateItem).planItems

            Assert.assertTrue(planList.contains(p))
        })
    }

    @Test
    fun planItemShouldBeRemovedFromViewGivenAnId() {
        // Reset date items
        viewModel.resetData()

        // Add a new event
        val p = TestUtil.planItemEvent1
        viewModel.displayPlan(p)

        // Remove the event
        viewModel.removePlanItemFromView(DateUtil.getDate(p.startDate!!), p.id)

        viewModel.listItems.observeOnce(Observer { listItems ->
            val dateItems =
                listItems.filter { it.getType() == ListItem.TYPE_DATE }

            val deletedDateItemIndex =
                dateItems.indexOfFirst {
                    (it as DateItem).date.isEqual(DateUtil.getDate(p.startDate!!))
                }
            Assert.assertNotEquals(-1, deletedDateItemIndex)

            val planList =
                (dateItems[deletedDateItemIndex] as DateItem).planItems
            Assert.assertFalse(planList.contains(p))
        })
    }

    @Test
    fun dataShouldBeValidWhenValidHomeworkIsGiven() {
        // reset subject instances
        AppRepository.subjectInstances = HashMap()

        AppRepository.subjectInstances["Maths"] = 1

        val actual = TestUtil.planItemMathsHomework
        Assert.assertTrue(
            viewModel.validateData(
                actual.itemType,
                actual.name,
                "",
                "",
                DateUtil.getFormattedTime(actual.date!!),
                actual.subject!!.name
            )
        )
    }

    @After
    fun finish() {
        AppRepository.zoneId = defaultZoneId
    }
}