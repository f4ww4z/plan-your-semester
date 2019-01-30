package com.jagoancoding.planyoursemester.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable

@Dao
interface ExamDao {

    @Query("SELECT * FROM exams")
    fun getExams(): Flowable<List<Exam>>

    @Query("SELECT * FROM exams WHERE exam_id = :id")
    fun getExamById(id: Long): Flowable<Exam>

    @Query("SELECT name FROM exams")
    fun getExamNames(): Flowable<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExam(exam: Exam)
}