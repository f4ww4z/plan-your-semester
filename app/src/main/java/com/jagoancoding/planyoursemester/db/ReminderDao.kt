package com.jagoancoding.planyoursemester.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders")
    fun getReminders(): Flowable<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE reminder_id = :id")
    fun getReminderById(id: Long): Flowable<Reminder>

    @Query("SELECT reminder FROM reminders")
    fun getReminderNames(): Flowable<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReminder(event: Reminder)
}