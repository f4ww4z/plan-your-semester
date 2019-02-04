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

    fun getSubjects(): LiveData<List<Subject>> = db.subjectDao().getSubjects()

    fun getSubject(name: String): LiveData<Subject> =
        db.subjectDao().getSubjectByName(name)

    fun getExams(): LiveData<List<Exam>> = db.examDao().getExams()

    fun getExamWithSubject(id: String): LiveData<ExamWithSubject> =
        db.examDao().getExamWithSubject(id)

    fun getHomeworks() = db.homeworkDao().getHomeworks()

    fun getHomeworkWithSubject(id: String): LiveData<HomeworkWithSubject> =
        db.homeworkDao().getHomeworkWithSubject(id)

    fun getEvents() = db.eventDao().getEvents()

    fun getEventById(id: String): LiveData<Event> =
        db.eventDao().getEventById(id)

    fun getReminders(): LiveData<List<Reminder>> =
        db.reminderDao().getReminders()

    fun getReminderById(id: String) = db.reminderDao().getReminderById(id)

    fun insertSubject(subject: Subject) {
        InsertSubjectAsyncTask(db.subjectDao()).execute(subject)
    }

    fun insertExam(exam: Exam) {
        InsertExamAsyncTask(db.examDao()).execute(exam)
    }

    //TODO: Replace keyword to 'Assignment'
    fun insertHomework(homework: Homework) {
        InsertHomeworkAsyncTask(db.homeworkDao()).execute(homework)
    }

    fun insertEvent(event: Event) {
        InsertEventAsyncTask(db.eventDao()).execute(event)
    }

    fun insertReminder(reminder: Reminder) {
        InsertReminderAsyncTask(db.reminderDao()).execute(reminder)
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

    class InsertSubjectAsyncTask(private val dao: SubjectDao) :
        AsyncTask<Subject, Void, Void>() {
        override fun doInBackground(vararg params: Subject): Void? {
            dao.insertSubject(params[0])
            return null
        }
    }

    class InsertExamAsyncTask(private val dao: ExamDao) :
        AsyncTask<Exam, Void, Void>() {
        override fun doInBackground(vararg params: Exam): Void? {
            dao.insertExam(params[0])
            return null
        }
    }

    class InsertHomeworkAsyncTask(private val dao: HomeworkDao) :
        AsyncTask<Homework, Void, Void>() {
        override fun doInBackground(vararg params: Homework): Void? {
            dao.insertHomework(params[0])
            return null
        }
    }

    class InsertEventAsyncTask(private val dao: EventDao) :
        AsyncTask<Event, Void, Void>() {
        override fun doInBackground(vararg params: Event): Void? {
            dao.insertEvent(params[0])
            return null
        }
    }

    class InsertReminderAsyncTask(private val dao: ReminderDao) :
        AsyncTask<Reminder, Void, Void>() {
        override fun doInBackground(vararg params: Reminder): Void? {
            dao.insertReminder(params[0])
            return null
        }
    }
}