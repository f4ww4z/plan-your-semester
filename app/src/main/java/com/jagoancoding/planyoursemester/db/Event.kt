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
import androidx.room.PrimaryKey
import com.jagoancoding.planyoursemester.model.PlanItem

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val event_id: Long = 0,
    @ColumnInfo
    var name: String,
    @ColumnInfo
    var startDate: Long,
    @ColumnInfo
    var endDate: Long,
    @ColumnInfo
    var description: String
) {
    fun toPlanItem(): PlanItem = PlanItem(
        PlanItem.TYPE_EVENT,
        event_id,
        null,
        name,
        description,
        null,
        startDate,
        endDate
    )
}