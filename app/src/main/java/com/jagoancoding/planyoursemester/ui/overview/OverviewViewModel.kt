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

package com.jagoancoding.planyoursemester.ui.overview

import androidx.lifecycle.ViewModel
import com.jagoancoding.planyoursemester.AppRepository
import com.jagoancoding.planyoursemester.db.Subject

class OverviewViewModel : ViewModel() {

    fun getSubjects() = AppRepository.getSubjects()

    fun getSubjectNames() = AppRepository.getSubjectNames()

    fun addOrUpdateSubject(name: String, color: String) {
        val subject = Subject(name = name, color = color)
        AppRepository.insertSubject(subject)
    }

    fun addDemoData() {
        addOrUpdateSubject("Maths", "blue")
        addOrUpdateSubject("Science", "green")
        addOrUpdateSubject("Music", "red")
        addOrUpdateSubject("Culture", "orange")
        addOrUpdateSubject("Bler", "pink")
    }
}
