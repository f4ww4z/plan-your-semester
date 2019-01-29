package com.jagoancoding.planyourday.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface SubjectDao {

    @Query("SELECT * FROM subjects")
    fun getSubjects(): Flowable<List<Subject>>

    @Query("SELECT name FROM subjects")
    fun getSubjectNames(): Flowable<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubject(): Completable
}