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

package com.jagoancoding.planyoursemester.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ExamDao {

    @Query("SELECT * FROM exams")
    fun getExams(): LiveData<List<Exam>>

    @Query("SELECT * FROM exams WHERE exam_id = :id")
    fun getExamById(id: Long): LiveData<Exam>

    @Query("SELECT name FROM exams")
    fun getExamNames(): LiveData<List<String>>

    @Query(
        """SELECT exam_id, subject_name, color, name, startDate, endDate
        FROM exams INNER JOIN subjects
        WHERE subject_name = s_name AND exam_id = :id"""
    )
    fun getExamWithSubject(id: Long): LiveData<ExamWithSubject>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExam(exam: Exam): Long

    @Update(onConflict = OnConflictStrategy.ABORT)
    fun updateExam(exam: Exam)

    @Query("""DELETE * FROM exams WHERE exam_id = :id""")
    fun deleteExam(id: Long)

    @Delete
    fun deleteExams(vararg exam: Exam)
}