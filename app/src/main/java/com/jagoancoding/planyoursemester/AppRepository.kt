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
import android.util.Log
import com.jagoancoding.planyoursemester.db.AppDatabase
import com.jagoancoding.planyoursemester.db.Event
import com.jagoancoding.planyoursemester.db.Exam
import com.jagoancoding.planyoursemester.db.Homework
import com.jagoancoding.planyoursemester.db.Reminder
import com.jagoancoding.planyoursemester.db.Subject
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
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

    fun getSubjects(): Flowable<List<Subject>> = db.subjectDao().getSubjects()

    fun getSubject(id: Long): Flowable<Subject> =
        db.subjectDao().getSubjectById(id)

    fun getSubjectNames(): Flowable<List<String>> =
        db.subjectDao().getSubjectNames()

    fun getExams(): Flowable<List<Exam>> = db.examDao().getExams()

    fun getHomeworks(): Flowable<List<Homework>> =
        db.homeworkDao().getHomeworks()

    fun getEvents(): Flowable<List<Event>> = db.eventDao().getEvents()

    fun getReminders(): Flowable<List<Reminder>> =
        db.reminderDao().getReminders()

    fun insertSubject(subject: Subject) {
        insert { db.subjectDao().insertSubject(subject) }
    }

    fun insertExam(exam: Exam) {
        insert { db.examDao().insertExam(exam) }
    }

    fun insertHomework(homework: Homework) {
        insert { db.homeworkDao().insertHomework(homework) }
    }

    fun insertEvent(event: Event) {
        insert { db.eventDao().insertEvent(event) }
    }

    fun insertReminder(reminder: Reminder) {
        insert { db.reminderDao().insertReminder(reminder) }
    }

    fun insert(f: () -> Unit) {
        Completable.fromAction {
            f()
        }.subscribeOn(Schedulers.io()).subscribe({}, { error ->
            Log.e("AppRepository", "Unable to insert entity, $error")
        }).dispose()
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
}