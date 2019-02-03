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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jagoancoding.planyoursemester.App
import com.jagoancoding.planyoursemester.R
import com.jagoancoding.planyoursemester.db.Exam
import com.jagoancoding.planyoursemester.db.Subject
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.overview_fragment.rv_overview

class OverviewFragment : Fragment() {

    companion object {
        fun newInstance() = OverviewFragment()
    }

    private val disposable = CompositeDisposable()
    private lateinit var viewModel: OverviewViewModel

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

        val dateAdapter =
            DateAdapter(
                viewModel.initialDateItems(
                    viewModel.startDate,
                    viewModel.endDate
                )
            )

        rv_overview.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = dateAdapter
        }.scrollToToday()

        // Set recylerview adapter's data to updated data
        viewModel.dateItems.observe(this, Observer {
            (rv_overview.adapter as DateAdapter).setData(it)
        })

        subscribeToExams(
            viewModel.getExams(),
            Schedulers.newThread()
        )

        /*
        val scheduler = Schedulers.newThread()
        disposable.add(
            viewModel.getExams().observeOn(scheduler)
                .subscribe({ exams ->
                    exams.forEach {
                        getSubjectOfExamAndAdd(it, Schedulers.newThread())
                    }
                }, { error ->
                    Log.e(
                        "OverviewFragment",
                        "Unable to fetch list, $error"
                    )
                })
        )
        */

        viewModel.addDemoData()
    }

    private fun RecyclerView.scrollToToday() {
        scrollToPosition(App.DAYS_PASSED.toInt())
    }

    /**
     * General subscription of Flowable exams examList
     * @param examList Flowable exam examList to listen to
     * @param scheduler scheduling units
     */
    private fun subscribeToExams(
        examList: Flowable<List<Exam>>,
        scheduler: Scheduler
    ) {
        disposable.add(
            examList.observeOn(scheduler)
                .subscribe({ exams ->
                    exams.forEach { exam ->
                        getSubjectOfExamAndAdd(exam, Schedulers.newThread())
                    }
                }, { error ->
                    Log.e(
                        "OverviewFragment",
                        "Unable to fetch examList, $error"
                    )
                })
        )
    }

    private fun getSubjectOfExamAndAdd(exam: Exam, scheduler: Scheduler) {
        val subjectFlowable = viewModel.getSubject(exam.name)

        disposable.add(
            subjectFlowable.observeOn(scheduler)
                .subscribe({ subject ->
                    // Found the subject, now add exam to dateItem's planItems
                    viewModel.populateDateItem(exam, subject)
                }, { error ->
                    Log.e(
                        "OverviewFragment",
                        "Unable to fetch item, $error"
                    )
                })
        )
    }

}
