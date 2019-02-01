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

package com.jagoancoding.planyoursemester.ui.overview

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.jagoancoding.planyoursemester.App
import com.jagoancoding.planyoursemester.R
import com.jagoancoding.planyoursemester.db.Event
import com.jagoancoding.planyoursemester.db.Exam
import com.jagoancoding.planyoursemester.db.Homework
import com.jagoancoding.planyoursemester.db.Reminder
import com.jagoancoding.planyoursemester.db.Subject
import com.jagoancoding.planyoursemester.model.DateItem
import com.jagoancoding.planyoursemester.util.DateUtil
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.overview_fragment.rv_overview
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

class OverviewFragment : Fragment() {

    companion object {
        fun newInstance() = OverviewFragment()
        val startDate = LocalDate.now().minusDays(App.DAYS_PASSED)
        val endDate = startDate.plusDays(App.DAYS_DISPLAYED_IN_OVERVIEW)
    }

    private val disposable = CompositeDisposable()
    private lateinit var viewModel: OverviewViewModel
    private lateinit var exams: List<Exam>
    private lateinit var homeworks: List<Homework>
    private lateinit var events: List<Event>
    private lateinit var reminders: List<Reminder>
    private lateinit var dateItems: List<DateItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.overview_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders
            .of(this)
            .get(OverviewViewModel::class.java)

        dateItems = ArrayList()

        rv_overview.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = DateAdapter(dateItems)
        }

        // Populate dateItems list

        disposable.add(
            viewModel.getExams()
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe({ exams ->
                    exams.forEach { exam ->
                        getSubjectOfExam(exam)
                    }
                }, { error ->
                    Log.e(
                        "OverviewFragment",
                        "Unable to fetch exam names, $error"
                    )
                })
        )
        viewModel.addDemoData()
    }

    private fun getSubjectOfExam(exam: Exam) {
        disposable.add(
            viewModel.getSubject(exam.subjectId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe({ subject ->
                    populateDateItem(exam, subject)
                }, { error ->
                    Log.e(
                        "OverviewFragment",
                        "Unable to fetch subject, $error"
                    )
                })
        )
    }

    private fun populateDateItem(exam: Exam, subject: Subject?) {
        val date =
        val startDateTime = DateUtil.getDateTime(exam.startDate)
        val endDateTime = DateUtil.getDateTime(exam.endDate)
        dateItems
    }

}
