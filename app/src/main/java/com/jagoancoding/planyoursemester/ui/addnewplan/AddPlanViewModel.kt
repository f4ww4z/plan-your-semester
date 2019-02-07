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

package com.jagoancoding.planyoursemester.ui.addnewplan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jagoancoding.planyoursemester.model.PlanItem

class AddPlanViewModel : ViewModel() {

    var planItemType: Int = 0
    var minimumDate: Long = 0
    var maximumDate: Long = 0

    fun validateData(
        type: Int,
        name: String = "",
        desc: String = "",
        startDate: String = "",
        endDate: String = "",
        date: String = "",
        subject: String = ""
    ): Boolean {
        when (type) {
            PlanItem.TYPE_EXAM -> {
                if (name.isNotBlank()
                    || startDate.isNotBlank()
                    || endDate.isNotBlank()
                    || subject.isNotBlank()
                ) {

                }
            }
            PlanItem.TYPE_HOMEWORK -> {

            }
            PlanItem.TYPE_EVENT -> {

            }
            PlanItem.TYPE_REMINDER -> {

            }
        }

        //TODO: Finish validation
    }
}