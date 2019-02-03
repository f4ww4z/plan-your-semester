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
import com.jagoancoding.planyoursemester.model.PlanItem

data class ExamWithSubject(
    @ColumnInfo(name = "exam_id")
    val id: String,
    @ColumnInfo(name = "subject_name")
    val subjectName: String,
    @ColumnInfo(name = "color")
    val subjectColor: String,
    @ColumnInfo
    var name: String,
    @ColumnInfo
    var startDate: Long,
    @ColumnInfo
    var endDate: Long
) {
    fun toPlanItem() = PlanItem(
        PlanItem.TYPE_EXAM,
        Subject(subjectName, subjectColor),
        name,
        null,
        null,
        startDate,
        endDate,
        null
    )
}