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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jagoancoding.planyoursemester.AppRepository
import com.jagoancoding.planyoursemester.db.Event
import com.jagoancoding.planyoursemester.db.Exam
import com.jagoancoding.planyoursemester.db.ExamWithSubject
import com.jagoancoding.planyoursemester.db.Homework
import com.jagoancoding.planyoursemester.db.HomeworkWithSubject
import com.jagoancoding.planyoursemester.db.Reminder
import com.jagoancoding.planyoursemester.db.Subject
import com.jagoancoding.planyoursemester.model.DateItem
import com.jagoancoding.planyoursemester.model.PlanItem
import com.jagoancoding.planyoursemester.util.DateUtil
import com.jagoancoding.planyoursemester.util.DateUtil.findDatePositionInList
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset

class MainViewModel : ViewModel() {

    var currentPlanItemType: Int = 0
    var currentPlanItem: PlanItem? = null
    var scrollToDate: LocalDate? = null

    private var _dateItems = MutableLiveData<List<DateItem>>()
    val dateItems: LiveData<List<DateItem>>
        get() = _dateItems

    init {
        _dateItems.value =
            initialDateItems(AppRepository.startDate, AppRepository.endDate)
    }

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

    fun removePlanItemFromView(date: LocalDate, id: Long) {
        val changedDateItems = _dateItems.value
        val dateItemsIndex: Int? =
            changedDateItems?.findDatePositionInList(date)

        if (dateItemsIndex != null) {
            val changePlanItems: MutableList<PlanItem> =
                changedDateItems[dateItemsIndex].planItems
            changePlanItems.removeAll { it.id == id }
            changedDateItems[dateItemsIndex].planItems = changePlanItems
        }
        _dateItems.value = changedDateItems
    }

    fun displayPlan(plan: PlanItem) {
        if (plan.name.isBlank()) {
            return
        }

        val dateItems = _dateItems.value

        // Check the type of plan, which determines if either date or startDate
        // should be used
        val dateTime: LocalDateTime =
            if (plan.itemType == PlanItem.TYPE_REMINDER ||
                plan.itemType == PlanItem.TYPE_HOMEWORK
            ) {
                DateUtil.getDateTime(plan.date!!)
            } else {
                DateUtil.getDateTime(plan.startDate!!)
            }

        val dateItemToUpdateIndex = dateItems!!.findDatePositionInList(
            LocalDate.of(
                dateTime.year,
                dateTime.month,
                dateTime.dayOfMonth
            )
        )

        val planList = dateItems[dateItemToUpdateIndex].planItems
        // Find the plan item to update
        // If item is found, update it, else create a new plan item and add it
        val planToUpdateIndex = planList.indexOfFirst { it.name == plan.name }
        if (planToUpdateIndex != -1) {
            planList[planToUpdateIndex] = plan
        } else {
            planList.add(plan)
        }

        dateItems[dateItemToUpdateIndex].planItems = planList

        _dateItems.value = dateItems
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

    fun getExamWithSubject(id: Long): LiveData<ExamWithSubject> =
        AppRepository.getExamWithSubject(id)

    fun getHomeworkWithSubject(id: Long): LiveData<HomeworkWithSubject> =
        AppRepository.getHomeworkWithSubject(id)

    fun event(id: Long): LiveData<Event> = AppRepository.getEventById(id)

    fun reminder(id: Long): LiveData<Reminder> =
        AppRepository.getReminderById(id)

//TODO: Add validation to all addOrUpdate methods e.g. startDate < endDate

    fun addSubject(name: String, color: String) {
        val subject = Subject(name = name, color = color)
        AppRepository.insertSubject(subject)
    }

    fun addExam(
        name: String, subjectName: String, startDate: Long, endDate: Long
    ) {
        val exam = Exam(
            name = name,
            subjectName = subjectName,
            startDate = startDate,
            endDate = endDate
        )
        AppRepository.insertExam(exam)
    }

    fun updateExam(
        id: Long,
        name: String,
        subjectName: String,
        startDate: Long,
        endDate: Long
    ) {
        val exam = Exam(
            exam_id = id,
            name = name,
            subjectName = subjectName,
            startDate = startDate,
            endDate = endDate
        )
        AppRepository.updateExam(exam)
    }

    fun deleteExam(date: LocalDate, id: Long) {
        AppRepository.deleteExam(id)
        removePlanItemFromView(date, id)
    }

    fun addHomework(
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

    fun updateHomework(
        id: Long,
        name: String,
        subjectName: String,
        dueDate: Long,
        description: String,
        isDone: Boolean
    ) {
        val homework = Homework(
            homework_id = id,
            name = name,
            subjectName = subjectName,
            dueDate = dueDate,
            description = description,
            isDone = isDone
        )
        AppRepository.updateHomework(homework)
    }

    fun deleteHomework(date: LocalDate, id: Long) {
        AppRepository.deleteHomework(id)
        removePlanItemFromView(date, id)
    }

    fun addEvent(
        name: String, startDate: Long, endDate: Long, description: String
    ) {
        val event = Event(
            name = name,
            startDate = startDate,
            endDate = endDate,
            description = description
        )
        AppRepository.insertEvent(event)
    }

    fun updateEvent(
        id: Long,
        name: String,
        startDate: Long,
        endDate: Long,
        description: String
    ) {
        val event = Event(
            event_id = id,
            name = name,
            startDate = startDate,
            endDate = endDate,
            description = description
        )
        AppRepository.updateEvent(event)
    }

    fun deleteEvent(date: LocalDate, id: Long) {
        AppRepository.deleteEvent(id)
        removePlanItemFromView(date, id)
    }

    fun addReminder(reminder: String, date: Long, isDone: Boolean) {
        val reminderEntity =
            Reminder(reminder = reminder, date = date, isDone = isDone)
        AppRepository.insertReminder(reminderEntity)
    }

    fun updateReminder(
        id: Long,
        reminder: String,
        date: Long,
        isDone: Boolean
    ) {
        val reminderEntity =
            Reminder(
                reminder_id = id,
                reminder = reminder,
                date = date,
                isDone = isDone
            )
        AppRepository.updateReminder(reminderEntity)
    }

    fun deleteReminder(date: LocalDate, id: Long) {
        AppRepository.deleteReminder(id)
        removePlanItemFromView(date, id)
    }

    fun validateData(
        type: Int,
        name: String = "",
        desc: String = "",
        startTime: String = "",
        endTime: String = "",
        dateTime: String = "",
        subject: String = ""
    ): Boolean {
        when (type) {
            PlanItem.TYPE_EXAM -> {
                return (name.isNotBlank()
                        && startTime.isNotBlank()
                        && endTime.isNotBlank()
                        && subject.isNotBlank())
            }
            PlanItem.TYPE_HOMEWORK -> {
                return (name.isNotBlank()
                        && dateTime.isNotBlank()
                        && subject.isNotBlank())
            }
            PlanItem.TYPE_EVENT -> {
                return (name.isNotBlank()
                        && startTime.isNotBlank()
                        && endTime.isNotBlank())
            }
            PlanItem.TYPE_REMINDER -> {
                return (name.isNotBlank() && dateTime.isNotBlank())
            }
        }

        return false
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
        val startDate3: Long =
            LocalDateTime.now().plusDays(2)
                .plusHours(3)
                .toInstant(ZoneOffset.UTC)
                .toEpochMilli()
        val endDate3: Long =
            LocalDateTime.now().plusDays(2)
                .plusHours(6)
                .toInstant(ZoneOffset.UTC)
                .toEpochMilli()
        val date4: Long =
            LocalDateTime.now().plusDays(3)
                .plusHours(7)
                .toInstant(ZoneOffset.UTC)
                .toEpochMilli()
        //TODO: Covert this to a test
        addSubject("Maths", "blue")
        addSubject("Science", "green")
        addSubject("Music", "red")
        addSubject("Culture", "orange")
        addSubject("Bler", "pink")
        addExam(
            "Maths test", "Maths",
            startDate1,
            endDate1
        )
        addExam(
            "Piano daily test", "Music",
            startDate2,
            endDate2
        )
        addExam(
            "Physics mid sem", "Science",
            startDate3,
            endDate3
        )
        addHomework(
            "Algebra sheet 2",
            "Maths",
            startDate2,
            "Need help from John",
            false
        )
        addEvent("Party", startDate3, endDate3, "Get friends together")
        addReminder("Buy eggs", date4, false)
    }
}
