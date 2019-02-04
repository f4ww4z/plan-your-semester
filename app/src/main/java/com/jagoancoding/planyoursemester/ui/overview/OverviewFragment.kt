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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jagoancoding.planyoursemester.App
import com.jagoancoding.planyoursemester.R
import com.jagoancoding.planyoursemester.db.Event
import com.jagoancoding.planyoursemester.db.Exam
import com.jagoancoding.planyoursemester.db.Homework
import com.jagoancoding.planyoursemester.db.Reminder
import kotlinx.android.synthetic.main.overview_fragment.rv_overview

class OverviewFragment : Fragment() {

    companion object {
        fun newInstance() = OverviewFragment()
    }

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

        rv_overview.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = DateAdapter(ArrayList())
        }.scrollToToday()

        // Set recylerview adapter's data to updated data
        viewModel.dateItems.observe(this, Observer {
            (rv_overview.adapter as DateAdapter).setData(it)
        })

        viewModel.exams.observe(this, Observer { exams ->
            exams.forEach { exam ->
                addToView(exam)
            }
        })

        viewModel.homeworks.observe(this, Observer { homeworks ->
            homeworks.forEach { homework ->
                addToView(homework)
            }
        })

        viewModel.events.observe(this, Observer { events ->
            events.forEach { event ->
                addToView(event)
            }
        })

        viewModel.reminders.observe(this, Observer { reminders ->
            reminders.forEach { reminder ->
                addToView(reminder)
            }
        })

        //viewModel.addDemoData()
    }

    private fun RecyclerView.scrollToToday() {
        scrollToPosition(App.DAYS_PASSED.toInt())
    }

    private fun addToView(exam: Exam) {
        viewModel.getExamWithSubject(exam.id).observe(this, Observer {
            val newExam = it.toPlanItem()
            viewModel.addNewPlan(newExam)
        })
    }

    private fun addToView(homework: Homework) {
        viewModel.getHomeworkWithSubject(homework.id).observe(this, Observer {
            val newHomework = it.toPlanItem()
            viewModel.addNewPlan(newHomework)
        })
    }

    private fun addToView(event: Event) {
        viewModel.event(event.id).observe(this, Observer {
            val newEvent = it.toPlanItem()
            viewModel.addNewPlan(newEvent)
        })
    }

    private fun addToView(reminder: Reminder) {
        viewModel.reminder(reminder.id).observe(this, Observer {
            val newReminder = it.toPlanItem()
            viewModel.addNewPlan(newReminder)
        })
    }

    /**
     * Observation of LiveData item
     * Executes a function with updated data as its argument
     * @param toExecute function to execute once data is retrieved
     */
    private fun <T> LiveData<out T>.runOnDataUpdate(toExecute: (T) -> Unit) {
        this.observe(this@OverviewFragment, Observer { obj ->
            toExecute(obj)
        })
    }

    /**
     * Observation of LiveData list of items
     * Executes a function with an updated item in a list as its argument
     * @param toExecute function to execute once data is retrieved
     */
    private fun <T> LiveData<out List<T>>.runOnEachElement(toExecute: (T) -> Unit) {
        this.observe(this@OverviewFragment, Observer { obj ->
            obj.forEach {
                toExecute(it)
            }
        })
    }
}
