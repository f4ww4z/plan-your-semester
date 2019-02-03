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
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset

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

    fun getSubject(name: String) = AppRepository.getSubject(name)

    val exams: LiveData<List<Exam>>
        get() = AppRepository.getExams()

    val homeworks: LiveData<List<Homework>>
        get() = AppRepository.getHomeworks()

    val events: LiveData<List<Event>>
        get() = AppRepository.getEvents()

    val reminders: LiveData<List<Reminder>>
        get() = AppRepository.getReminders()

    //TODO: Add validation to all addOrUpdate methods e.g. startDate < endDate

    fun addOrUpdateSubject(name: String, color: String) {
        val subject = Subject(name = name, color = color)
        AppRepository.insertSubject(subject)
    }

    fun addOrUpdateExam(
        name: String,
        subjectName: String,
        startDate: Long,
        endDate: Long
    ) {
        val exam = Exam(
            name = name,
            subjectName = subjectName,
            startDate = startDate,
            endDate = endDate
        )
        AppRepository.insertExam(exam)
    }

    fun addOrUpdateHomework(
        name: String,
        subjectName: String,
        dueDate: Long,
        description: String,
        isDone: Boolean
    ) {
        val homework = Homework(
            name = name,
            subjectName = subjectName,
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
        val startDate1: Long =
            LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()
        val endDate1: Long =
            LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.UTC)
                .toEpochMilli()
        val startDate2: Long =
            LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC)
                .toEpochMilli()
        val endDate2: Long =
            LocalDateTime.now().plusDays(1).plusHours(2)
                .toInstant(ZoneOffset.UTC)
                .toEpochMilli()
        //TODO: Covert this to a test
        addOrUpdateSubject("Maths", "blue")
        addOrUpdateSubject("Science", "green")
        addOrUpdateSubject("Music", "red")
        addOrUpdateSubject("Culture", "orange")
        addOrUpdateSubject("Bler", "pink")
        addOrUpdateExam(
            "Maths test", "Maths",
            startDate1,
            endDate1
        )
        addOrUpdateExam(
            "Piano daily test", "Music",
            startDate2,
            endDate2
        )
    }
}
