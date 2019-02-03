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
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HomeworkDao {

    @Query("SELECT * FROM homework")
    fun getHomeworks(): LiveData<List<Homework>>

    @Query("SELECT * FROM homework WHERE homework_id = :id")
    fun getHomeworkById(id: Long): LiveData<Homework>

    @Query("SELECT name FROM homework")
    fun getHomeworkNames(): LiveData<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHomework(homework: Homework)
}