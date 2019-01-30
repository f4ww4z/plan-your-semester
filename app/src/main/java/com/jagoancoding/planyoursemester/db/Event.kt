package com.jagoancoding.planyoursemester.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "events")
data class Event(
        @PrimaryKey
        @ColumnInfo(name = "event_id")
        val id: String = UUID.randomUUID().toString(),
        @ColumnInfo
        var name: String,
        @ColumnInfo
        var startDate: Long,
        @ColumnInfo
        var endDate: Long,
        @ColumnInfo
        var description: String
)