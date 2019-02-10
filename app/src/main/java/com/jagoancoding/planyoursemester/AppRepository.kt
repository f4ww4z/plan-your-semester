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

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.jagoancoding.planyoursemester.db.AppDatabase
import com.jagoancoding.planyoursemester.db.Event
import com.jagoancoding.planyoursemester.db.EventDao
import com.jagoancoding.planyoursemester.db.Exam
import com.jagoancoding.planyoursemester.db.ExamDao
import com.jagoancoding.planyoursemester.db.ExamWithSubject
import com.jagoancoding.planyoursemester.db.Homework
import com.jagoancoding.planyoursemester.db.HomeworkDao
import com.jagoancoding.planyoursemester.db.HomeworkWithSubject
import com.jagoancoding.planyoursemester.db.Reminder
import com.jagoancoding.planyoursemester.db.ReminderDao
import com.jagoancoding.planyoursemester.db.Subject
import com.jagoancoding.planyoursemester.db.SubjectDao
import com.jagoancoding.planyoursemester.util.DateUtil
import org.threeten.bp.LocalDate

/**
 * Enables injection of data sources
 */
object AppRepository {

    // Database backend
    private lateinit var db: AppDatabase

    fun init(application: Application) {
        db = AppDatabase.getInstance(application.applicationContext)
    }

    val today: LocalDate = LocalDate.now()
    val startDate: LocalDate = today.minusDays(App.DAYS_PASSED)
    val endDate: LocalDate = startDate.plusDays(App.DAYS_SINCE_PASSED)

    var minimumDate: Long = DateUtil.toEpochMili(startDate)
    var maximumDate: Long = DateUtil.toEpochMili(endDate)

    fun getSubjects(): LiveData<List<Subject>> = db.subjectDao().getSubjects()

    fun getSubject(name: String): LiveData<Subject> =
        db.subjectDao().getSubjectByName(name)

    fun getExams(): LiveData<List<Exam>> = db.examDao().getExams()

    fun getExamWithSubject(id: Long): LiveData<ExamWithSubject> =
        db.examDao().getExamWithSubject(id)

    fun getHomeworks() = db.homeworkDao().getHomeworks()

    fun getHomeworkWithSubject(id: Long): LiveData<HomeworkWithSubject> =
        db.homeworkDao().getHomeworkWithSubject(id)

    fun getEvents() = db.eventDao().getEvents()

    fun getEventById(id: Long): LiveData<Event> =
        db.eventDao().getEventById(id)

    fun getReminders(): LiveData<List<Reminder>> =
        db.reminderDao().getReminders()

    fun getReminderById(id: Long) = db.reminderDao().getReminderById(id)

    fun insertSubject(subject: Subject) {
        RunInBackground().execute({ db.subjectDao().insertSubject(subject) })
    }

    fun insertExam(exam: Exam) {
        RunInBackground().execute({ db.examDao().insertExam(exam) })
    }

    //TODO: Replace keyword to 'Assignment'
    fun insertHomework(homework: Homework) {
        RunInBackground().execute({ db.homeworkDao().insertHomework(homework) })
    }

    fun insertEvent(event: Event) {
        RunInBackground().execute({ db.eventDao().insertEvent(event) })
    }

    fun insertReminder(reminder: Reminder) {
        RunInBackground().execute({ db.reminderDao().insertReminder(reminder) })
    }

    fun updateExam(exam: Exam) {
        RunInBackground().execute({ db.examDao().updateExam(exam) })
    }

    fun updateHomework(homework: Homework) {
        RunInBackground().execute({ db.homeworkDao().updateHomework(homework) })
    }

    fun updateEvent(event: Event) {
        RunInBackground().execute({ db.eventDao().updateEvent(event) })
    }

    fun updateReminder(reminder: Reminder) {
        RunInBackground().execute({ db.reminderDao().updateReminder(reminder) })
    }

    fun deleteExam(id: Long) {
        db.examDao().deleteExam(id)
    }

    fun deleteHomework(id: Long) {
        db.homeworkDao().deleteHomework(id)
    }

    fun deleteEvent(id: Long) {
        db.eventDao().deleteEvent(id)
    }

    fun deleteReminder(id: Long) {
        db.reminderDao().deleteReminder(id)
    }

    fun datesBetween(start: LocalDate, end: LocalDate): List<LocalDate> {
        val ret = ArrayList<LocalDate>()
        var date = start
        while (date.isBefore(end.minusDays(1))) {
            ret.add(date)
            date = date.plusDays(1)
        }
        return ret
    }

    class RunInBackground : AsyncTask<() -> Unit, Void, Void>() {
        override fun doInBackground(vararg params: (() -> Unit)): Void? {
            params[0]()
            return null
        }
    }
}