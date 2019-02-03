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
import com.jagoancoding.planyoursemester.db.Exam
import com.jagoancoding.planyoursemester.db.Homework
import com.jagoancoding.planyoursemester.db.Reminder
import com.jagoancoding.planyoursemester.db.Subject
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

    fun getHomeworks(): LiveData<List<Homework>> =
        db.homeworkDao().getHomeworks()

    fun getEvents(): LiveData<List<Event>> = db.eventDao().getEvents()

    fun getReminders(): LiveData<List<Reminder>> =
        db.reminderDao().getReminders()

    fun insertSubject(subject: Subject) {
        DaoAsyncTask { db.subjectDao().insertSubject(subject) }.execute()
    }

    fun insertExam(exam: Exam) {
        DaoAsyncTask { db.examDao().insertExam(exam) }.execute()
    }

    //TODO: Replace keyword to 'Assignment'
    fun insertHomework(homework: Homework) {
        DaoAsyncTask { db.homeworkDao().insertHomework(homework) }.execute()
    }

    fun insertEvent(event: Event) {
        DaoAsyncTask { db.eventDao().insertEvent(event) }.execute()
    }

    fun insertReminder(reminder: Reminder) {
        DaoAsyncTask { db.reminderDao().insertReminder(reminder) }.execute()
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

    class DaoAsyncTask(private val daoMethod: () -> Unit) :
        AsyncTask<Any, Void?, Void?>() {

        override fun doInBackground(vararg params: Any): Void? {
            daoMethod
            return null
        }
    }
}