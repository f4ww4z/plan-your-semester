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
import com.jagoancoding.planyoursemester.App
import com.jagoancoding.planyoursemester.AppRepository
import com.jagoancoding.planyoursemester.db.Event
import com.jagoancoding.planyoursemester.db.Exam
import com.jagoancoding.planyoursemester.db.Homework
import com.jagoancoding.planyoursemester.db.Reminder
import com.jagoancoding.planyoursemester.db.Subject
import com.jagoancoding.planyoursemester.model.DateItem
import com.jagoancoding.planyoursemester.util.DateUtil
import org.threeten.bp.LocalDate

class OverviewViewModel : ViewModel() {

    val today: LocalDate = LocalDate.now()
    val startDate: LocalDate = today.minusDays(App.DAYS_PASSED)
    val endDate: LocalDate = startDate.plusDays(App.DAYS_SINCE_PASSED)

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

    fun getExams() = AppRepository.getExams()

    fun getHomeworks() = AppRepository.getHomeworks()

    fun getEvents() = AppRepository.getEvents()

    fun getReminders() = AppRepository.getReminders()

    fun addOrUpdateSubject(name: String, color: String) {
        val subject = Subject(name = name, color = color)
        AppRepository.insertSubject(subject)
    }

    fun addOrUpdateExam(
        name: String,
        subjectId: Long,
        startDate: Long,
        endDate: Long
    ) {
        val exam = Exam(
            name = name,
            subjectId = subjectId,
            startDate = startDate,
            endDate = endDate
        )
        AppRepository.insertExam(exam)
    }

    fun addOrUpdateHomework(
        name: String,
        subjectId: Long,
        dueDate: Long,
        description: String,
        isDone: Boolean
    ) {
        val homework = Homework(
            name = name,
            subjectId = subjectId,
            dueDate = dueDate,
            description = description,
            isDone = isDone
        )
        AppRepository.insertHomework(homework)
    }

    fun addOrUpdateEvent(
        name: String,
        startDate: Long,
        endDate: Long,
        description: String
    ) {
        val event = Event(
            name = name,
            startDate = startDate,
            endDate = endDate,
            description = description
        )
        AppRepository.insertEvent(event)
    }

    fun addOrUpdateReminder(reminder: String, date: Long, isDone: Boolean) {
        val reminderEntity =
            Reminder(reminder = reminder, date = date, isDone = isDone)
        AppRepository.insertReminder(reminderEntity)
    }

    fun addDemoData() {
        /*
        addOrUpdateSubject("Maths", "blue")
        addOrUpdateSubject("Science", "green")
        addOrUpdateSubject("Music", "red")
        addOrUpdateSubject("Culture", "orange")
        addOrUpdateSubject("Bler", "pink")
        */

    }
}
