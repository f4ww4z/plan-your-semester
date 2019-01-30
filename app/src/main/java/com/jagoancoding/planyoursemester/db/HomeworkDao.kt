package com.jagoancoding.planyoursemester.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable

@Dao
interface HomeworkDao {

    @Query("SELECT * FROM homework")
    fun getHomeworks(): Flowable<List<Homework>>

    @Query("SELECT * FROM homework WHERE homework_id = :id")
    fun getHomeworkById(id: Long): Flowable<Homework>

    @Query("SELECT name FROM homework")
    fun getHomeworkNames(): Flowable<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHomework(homework: Homework)
}