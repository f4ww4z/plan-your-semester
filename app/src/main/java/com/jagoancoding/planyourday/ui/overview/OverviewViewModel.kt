/*
 * Copyright 2019 Maharaj Fawwaz Almuqaddim Yusran
 *
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

package com.jagoancoding.planyourday.ui.overview

import androidx.lifecycle.ViewModel
import com.jagoancoding.planyourday.db.Subject
import com.jagoancoding.planyourday.db.SubjectDao
import io.reactivex.Completable
import io.reactivex.Flowable

class OverviewViewModel(private val subjectSource: SubjectDao) : ViewModel() {

    fun getSubjects(): Flowable<List<Subject>> = subjectSource.getSubjects()

    fun getSubjectNames(): Flowable<List<String>> =
        subjectSource.getSubjectNames()

    fun addOrUpdateSubject(name: String, color: String): Completable {
        val subject = Subject(name, color)
        return subjectSource.insertSubject()
    }
}
