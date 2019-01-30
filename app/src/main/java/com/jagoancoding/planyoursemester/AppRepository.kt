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
    private lateinit var subjectSource: SubjectDao

    fun init(application: Application) {
        db = AppDatabase.getInstance(application.applicationContext)
        subjectSource = db.subjectDao()
    }

    fun getSubjects(): Flowable<List<Subject>> = subjectSource.getSubjects()

    fun getSubjectNames(): Flowable<List<String>> =
        subjectSource.getSubjectNames()

    fun insertSubject(subject: Subject) {
        Completable.fromAction {
            subjectSource.insertSubject(subject)
        }.subscribeOn(Schedulers.io()).subscribe()
    }
}