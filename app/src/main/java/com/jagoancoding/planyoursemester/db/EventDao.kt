package com.jagoancoding.planyoursemester.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable

@Dao
interface EventDao {

    @Query("SELECT * FROM events")
    fun getEvents(): Flowable<List<Event>>

    @Query("SELECT * FROM events WHERE event_id = :id")
    fun getEventById(id: Long): Flowable<Event>

    @Query("SELECT name FROM events")
    fun getEventNames(): Flowable<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvent(event: Event)
}