package com.jagoancoding.planyoursemester

import android.app.Application
import com.jagoancoding.planyoursemester.db.AppDatabase
import com.jagoancoding.planyoursemester.db.Subject
import com.jagoancoding.planyoursemester.db.SubjectDao
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
}