package com.jagoancoding.planyoursemester.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "exams", foreignKeys = [ForeignKey(
        entity = Subject::class,
        parentColumns = arrayOf("subject_id"),
        childColumns = arrayOf("subject_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Exam(
    @PrimaryKey
    @ColumnInfo(name = "exam_id")
    val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "subject_id")
    val subjectId: Long,
    @ColumnInfo
    var name: String,
    @ColumnInfo
    var date: Long
)