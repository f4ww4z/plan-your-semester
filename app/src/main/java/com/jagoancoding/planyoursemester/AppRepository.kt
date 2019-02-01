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
import com.jagoancoding.planyoursemester.db.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers

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

    fun getSubjectNames(): Flowable<List<String>> =
            db.subjectDao().getSubjectNames()

    fun insertSubject(subject: Subject) {
        Completable.fromAction {
            db.subjectDao().insertSubject(subject)
        }.subscribeOn(Schedulers.io()).subscribe()
    }

    fun getExams(): Flowable<List<Exam>> = db.examDao().getExams()

    fun getHomeworks(): Flowable<List<Homework>> = db.homeworkDao().getHomeworks()

    fun getEvents(): Flowable<List<Event>> = db.eventDao().getEvents()

    fun getReminders(): Flowable<List<Reminder>> = db.reminderDao().getReminders()
}