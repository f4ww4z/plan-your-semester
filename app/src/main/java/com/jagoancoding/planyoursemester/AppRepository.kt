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
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import androidx.lifecycle.LiveData
import com.jagoancoding.planyoursemester.db.AppDatabase
import com.jagoancoding.planyoursemester.db.Event
import com.jagoancoding.planyoursemester.db.Exam
import com.jagoancoding.planyoursemester.db.ExamWithSubject
import com.jagoancoding.planyoursemester.db.Homework
import com.jagoancoding.planyoursemester.db.HomeworkWithSubject
import com.jagoancoding.planyoursemester.db.Reminder
import com.jagoancoding.planyoursemester.db.Subject
import com.jagoancoding.planyoursemester.util.DateUtil
import com.jagoancoding.planyoursemester.util.ViewUtil.getColorByResId
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

/**
 * Enables injection of data sources
 */
object AppRepository {

    private const val TAG = "AppRepository"
    // Database backend
    private lateinit var db: AppDatabase

    fun init(application: Application) {
        db = AppDatabase.getInstance(application.applicationContext)
    }

    var zoneId: ZoneId = ZoneId.systemDefault()
    var currentDateTime: ZonedDateTime = ZonedDateTime.now(zoneId)

    var today: LocalDate = currentDateTime.toLocalDate()
    var startDate: LocalDate = today.minusDays(App.DAYS_PASSED)
    var endDate: LocalDate = startDate.plusDays(App.DAYS_SINCE_PASSED)

    var minimumDate: Long = DateUtil.toEpochMili(startDate)
    var maximumDate: Long = DateUtil.toEpochMili(endDate)

    var subjectInstances = HashMap<String, Int>()

    /**
     * Keep track of the total exams and homework for the subject
     * @param subjectName subject
     */
    fun incrementSubjectCountBy(subjectName: String, value: Int) {
        val count = subjectInstances[subjectName]
        if (count != null) {
            subjectInstances[subjectName] = count + value
        } else {
            subjectInstances[subjectName] = value
        }
        Log.i(TAG, "Subject Instances: $subjectInstances")
    }

    /**
     * Decreaase the number of times the subject is used in DateAdapter
     * @param subjectName subject
     */
    fun decreaseSubjectCount(subjectName: String) {
        val count = subjectInstances[subjectName]
        if (count != null && count > 0) {
            subjectInstances[subjectName] = count - 1
        }
        Log.i(TAG, "Subject Instances: $subjectInstances")
    }

    fun defaultSubjectColor(context: Context): Int =
        context.getColorByResId(R.color.colorAccent)

    fun getSubjectNames(): LiveData<List<String>> =
        db.subjectDao().getSubjectNames()

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

    fun deleteSubject(subjectId: String) {
        RunInBackground().execute({ db.subjectDao().deleteSubject(subjectId) })
    }

    fun deleteExam(id: Long) {
        RunInBackground().execute({ db.examDao().deleteExam(id) })
    }

    fun deleteHomework(id: Long) {
        RunInBackground().execute({ db.homeworkDao().deleteHomework(id) })
    }

    fun deleteEvent(id: Long) {
        RunInBackground().execute({ db.eventDao().deleteEvent(id) })
    }

    fun deleteReminder(id: Long) {
        RunInBackground().execute({ db.reminderDao().deleteReminder(id) })
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