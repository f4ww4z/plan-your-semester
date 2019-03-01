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

import android.graphics.Color
import android.util.Log
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
import com.jagoancoding.planyoursemester.model.DividerItem
import com.jagoancoding.planyoursemester.model.ListItem
import com.jagoancoding.planyoursemester.model.PlanItem
import com.jagoancoding.planyoursemester.util.DateUtil
import com.jagoancoding.planyoursemester.util.DateUtil.findDatePositionInList
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset

class MainViewModel : ViewModel() {

    companion object {
        const val TAG = "MainViewModel"
    }

    var currentPlanItemType: Int = 0
    var currentPlanItem: PlanItem? = null
    var scrollToDate: Long? = null

    private var _listItems = MutableLiveData<List<ListItem>>()
    val listItems: LiveData<List<ListItem>>
        get() = _listItems

    init {
        _listItems.value =
            initialListItems(AppRepository.startDate, AppRepository.endDate)
    }

    fun initialListItems(start: LocalDate, end: LocalDate): List<ListItem> {
        val listItems = ArrayList<ListItem>()
        AppRepository.datesBetween(start, end).forEach { date ->
            // Insert divider every month
            if (date.dayOfMonth == 1) {
                val divItem = DividerItem(date.monthValue, date.year)
                listItems.add(divItem)
            }

            // Add normal date rows
            val dateItem = DateItem(date, ArrayList())
            listItems.add(dateItem)
        }
        return listItems
    }

    fun removePlanItemFromView(date: LocalDate, id: Long) {
        val changedListItems: MutableList<ListItem>? =
            _listItems.value?.toMutableList()
        val dateItemsIndex: Int? =
            changedListItems?.findDatePositionInList(date)

        if (dateItemsIndex != null) {
            val dateItemToChange = changedListItems[dateItemsIndex] as DateItem
            val changePlanItems: MutableList<PlanItem> =
                dateItemToChange.planItems
            changePlanItems.removeAll { it.id == id }

            dateItemToChange.planItems = changePlanItems
            changedListItems[dateItemsIndex] = dateItemToChange
        }
        _listItems.value = changedListItems
    }

    fun displayPlan(plan: PlanItem) {
        if (plan.name.isBlank()) {
            return
        }

        val listItems = _listItems.value

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

        val dateItemToUpdateIndex = listItems!!.findDatePositionInList(
            LocalDate.of(dateTime.year, dateTime.month, dateTime.dayOfMonth)
        )

        val planList = (listItems[dateItemToUpdateIndex] as DateItem).planItems
        // Find the plan item to update
        // If item is found, update it, else create a new plan item and add it
        val planToUpdateIndex = planList.indexOfFirst { it.id == plan.id }
        if (planToUpdateIndex != -1) {
            planList[planToUpdateIndex] = plan
        } else {
            planList.add(plan)
        }

        planList.sortBy { it.date ?: it.startDate }

        (listItems[dateItemToUpdateIndex] as DateItem).planItems = planList

        _listItems.value = listItems
    }

    fun countSubjectUsage(subjectIds: List<String>, listItems: List<ListItem>) {

        var dateItems = listItems.filter { it.getType() == ListItem.TYPE_DATE }
        dateItems = dateItems.toDateItems()

        // LOL KOTLIN MAP ROCKS!!!1
        val subjUsages = dateItems
            .flatMap { it.planItems }
            .map { it.subject }
            .map { it?.name }

        subjectIds.forEach { subjectId ->
            val subjectCount = subjUsages.count { it == subjectId }
            AppRepository.subjectInstances[subjectId] = subjectCount
        }

        Log.i(TAG, "Subject instances: ${AppRepository.subjectInstances}")
    }

    fun resetData() {
        _listItems.value =
            initialListItems(AppRepository.startDate, AppRepository.endDate)
    }

    fun List<ListItem>.toDateItems(): List<DateItem> {
        val items = ArrayList<DateItem>()
        forEach {
            items.add(it as DateItem)
        }
        return items
    }

    fun getSubject(name: String) = AppRepository.getSubject(name)

    fun subjectNames() = AppRepository.getSubjectNames()

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

    fun addSubject(name: String, color: Int) {
        val subject = Subject(name = name, color = color)
        AppRepository.insertSubject(subject)
    }

    fun deleteSubject(subjectId: String) {
        AppRepository.deleteSubject(subjectId)
    }

    fun addExam(
        name: String, subjectName: String, startDate: Long, endDate: Long
    ) {
        val exam = Exam(
            name = name,
            subjectName = subjectName,
            startDate = startDate,
            endDate = endDate
        ).apply {
            currentPlanItem = ExamWithSubject(
                exam_id,
                subjectName,
                currentPlanItem?.subject?.color
                    ?: 0, // this color is NOT the real subject color
                name,
                startDate,
                endDate
            ).toPlanItem()
        }

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
        ).apply {
            currentPlanItem = ExamWithSubject(
                exam_id,
                subjectName,
                currentPlanItem?.subject?.color ?: 0,
                name,
                startDate,
                endDate
            ).toPlanItem()
        }

        AppRepository.updateExam(exam)
        //TODO: Update notification
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
        ).apply {
            currentPlanItem = HomeworkWithSubject(
                homework_id,
                subjectName,
                currentPlanItem?.subject?.color ?: 0,
                name,
                dueDate,
                description,
                isDone
            ).toPlanItem()
        }

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
        ).apply {
            currentPlanItem = HomeworkWithSubject(
                homework_id,
                subjectName,
                currentPlanItem?.subject?.color ?: 0,
                name,
                dueDate,
                description,
                isDone
            ).toPlanItem()
        }

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
        currentPlanItem = event.toPlanItem()

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
        currentPlanItem = event.toPlanItem()

        AppRepository.updateEvent(event)
    }

    fun deleteEvent(date: LocalDate, id: Long) {
        AppRepository.deleteEvent(id)
        removePlanItemFromView(date, id)
    }

    fun addReminder(reminder: String, date: Long, isDone: Boolean) {
        val r = Reminder(reminder = reminder, date = date, isDone = isDone)
        currentPlanItem = r.toPlanItem()

        AppRepository.insertReminder(r)
    }

    fun updateReminder(
        id: Long,
        reminder: String,
        date: Long,
        isDone: Boolean
    ) {
        val r = Reminder(
            reminder_id = id, reminder = reminder, date = date, isDone = isDone
        )
        currentPlanItem = r.toPlanItem()

        AppRepository.updateReminder(r)
    }

    fun deleteReminder(date: LocalDate, id: Long) {
        AppRepository.deleteReminder(id)
        removePlanItemFromView(date, id)
    }

    fun validateData(
        type: Int,
        name: String = "",
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
                        && subject.isNotBlank()
                        && AppRepository.subjectInstances.keys.contains(subject))
            }
            PlanItem.TYPE_HOMEWORK -> {
                return (name.isNotBlank()
                        && dateTime.isNotBlank()
                        && subject.isNotBlank()
                        && AppRepository.subjectInstances.keys.contains(subject))
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
        addSubject("Maths", Color.BLACK)
        addSubject("Science", Color.BLUE)
        addSubject("Music", Color.BLACK)
        addSubject("Culture", Color.BLACK)
        addSubject("Bler", Color.BLACK)
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
