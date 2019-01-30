package com.jagoancoding.planyoursemester.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "homework", foreignKeys = [ForeignKey(
        entity = Subject::class,
        parentColumns = arrayOf("subject_id"),
        childColumns = arrayOf("subject_id"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Homework(
    @PrimaryKey
    @ColumnInfo(name = "homework_id")
    val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "subject_id")
    val subjectId: Long,
    @ColumnInfo
    var name: String,
    @ColumnInfo
    var dueDate: Long,
    @ColumnInfo
    var description: String,
    @ColumnInfo
    var isDone: Boolean
)