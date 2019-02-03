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
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jagoancoding.planyoursemester.App
import com.jagoancoding.planyoursemester.R
import com.jagoancoding.planyoursemester.db.Exam
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

        val dateAdapter = DateAdapter(
            viewModel.initialDateItems(viewModel.startDate, viewModel.endDate)
        )

        rv_overview.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = dateAdapter
        }.scrollToToday()

        // Set recylerview adapter's data to updated data
        viewModel.dateItems.observe(this, Observer {
            (rv_overview.adapter as DateAdapter).setData(it)
        })

        viewModel.exams.observe(this, Observer { exams ->
            exams.forEach { exam ->
                getSubjectOfExamAndAdd(exam)
            }
        })

        viewModel.addDemoData()
    }

    private fun RecyclerView.scrollToToday() {
        scrollToPosition(App.DAYS_PASSED.toInt())
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

    private fun getSubjectOfExamAndAdd(exam: Exam) {
        val subjectToGet = viewModel.getSubject(exam.name)

        subjectToGet.observe(this@OverviewFragment, Observer { subject ->
            if (subject == null) {
                Log.w("OverviewFragment", "Subject of exam is null")
            } else {
                viewModel.populateDateItem(exam, subject)
            }
        })
    }
}
