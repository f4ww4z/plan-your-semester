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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "homework", foreignKeys = [ForeignKey(
        entity = Subject::class,
        parentColumns = arrayOf("s_name"),
        childColumns = arrayOf("subject_name"),
        onDelete = ForeignKey.CASCADE
    )], indices = [Index(value = ["subject_name"], unique = true)]
)
data class Homework(
    @PrimaryKey(autoGenerate = true)
    val homework_id: Long = 0,
    @ColumnInfo(name = "subject_name")
    val subjectName: String,
    @ColumnInfo
    var name: String,
    @ColumnInfo
    var dueDate: Long,
    @ColumnInfo
    var description: String,
    @ColumnInfo
    var isDone: Boolean
)