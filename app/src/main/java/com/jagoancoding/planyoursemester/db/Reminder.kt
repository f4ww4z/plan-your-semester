package com.jagoancoding.planyoursemester.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "reminders")
data class Reminder(
        @PrimaryKey
        @ColumnInfo(name = "reminder_id")
        val id: String = UUID.randomUUID().toString(),
        @ColumnInfo
        var reminder: String,
        @ColumnInfo
        var date: Long,
        @ColumnInfo
        var isDone: Boolean
)