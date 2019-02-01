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

package com.jagoancoding.planyoursemester.ui.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jagoancoding.planyoursemester.AppRepository
import com.jagoancoding.planyoursemester.db.Exam
import com.jagoancoding.planyoursemester.db.Subject
import com.jagoancoding.planyoursemester.model.DateItem
import com.jagoancoding.planyoursemester.util.DateUtil
import org.threeten.bp.LocalDate

class OverviewViewModel : ViewModel() {

    private var _dateItems = MutableLiveData<List<DateItem>>()
    val dateItems: LiveData<List<DateItem>>
        get() = _dateItems

    fun initialDateItems(start: LocalDate, end: LocalDate): List<DateItem> {
        val dateItems = ArrayList<DateItem>()
        AppRepository.datesBetween(start, end).forEach { date ->
            val dateItem = DateItem(
                date,
                ArrayList()
            )
            dateItems.add(dateItem)
        }
        return dateItems
    }

    fun populateDateItem(exam: Exam, subject: Subject?) {
        val date = DateUtil.getDate(exam.startDate)

        _dateItems.value?.first { it.date.isEqual(date) }
            ?.planItems?.add(exam.toPlanItem(subject))
    }

    fun getSubject(id: Long) = AppRepository.getSubject(id)

    fun addOrUpdateSubject(name: String, color: String) {
        val subject = Subject(name = name, color = color)
        AppRepository.insertSubject(subject)
    }

    fun getExams() = AppRepository.getExams()

    fun getHomeworks() = AppRepository.getHomeworks()

    fun getEvents() = AppRepository.getEvents()

    fun getReminders() = AppRepository.getReminders()

    fun addDemoData() {
        addOrUpdateSubject("Maths", "blue")
        addOrUpdateSubject("Science", "green")
        addOrUpdateSubject("Music", "red")
        addOrUpdateSubject("Culture", "orange")
        addOrUpdateSubject("Bler", "pink")
    }
}
