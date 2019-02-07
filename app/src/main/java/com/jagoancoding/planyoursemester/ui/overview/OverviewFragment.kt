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

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jagoancoding.planyoursemester.App
import com.jagoancoding.planyoursemester.R
import com.jagoancoding.planyoursemester.db.Event
import com.jagoancoding.planyoursemester.db.Exam
import com.jagoancoding.planyoursemester.db.Homework
import com.jagoancoding.planyoursemester.db.Reminder
import com.jagoancoding.planyoursemester.model.PlanItem
import com.jagoancoding.planyoursemester.ui.addnewplan.AddPlanFragment
import com.jagoancoding.planyoursemester.util.DateUtil
import com.jagoancoding.planyoursemester.util.ToastUtil.showShortToast
import com.jagoancoding.planyoursemester.util.ViewUtil.getColorByResId
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList

class OverviewFragment : Fragment(),
    RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener<Int> {

    companion object {
        fun newInstance() = OverviewFragment()
    }

    private lateinit var viewModel: OverviewViewModel

    private var rfal: RapidFloatingActionLayout? = null
    private var rfab: RapidFloatingActionButton? = null
    private var overviewRV: RecyclerView? = null
    private lateinit var dateAdapter: DateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

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

        val toolbar: Toolbar? = activity?.findViewById(R.id.overview_toolbar)
        toolbar?.menu?.clear()

        overviewRV = view?.findViewById(R.id.rv_overview)
        dateAdapter = DateAdapter(ArrayList())

        overviewRV?.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = dateAdapter
        }?.scrollToToday()

        // Set up extended FAB
        with(view) {
            rfal = this?.findViewById(R.id.rfal_overview)
            rfab = this?.findViewById(R.id.rfab_add_plan)
        }

        val rfabItems = getRfabItems(context!!)
        val rfaContent = RapidFloatingActionContentLabelList(context)
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this)
        rfaContent.items = rfabItems
        RapidFloatingActionHelper(context!!, rfal, rfab, rfaContent)
            .build()

        // Set recylerview adapter's data to updated data
        viewModel.dateItems.observe(this, Observer {
            (overviewRV?.adapter as DateAdapter).setData(it)
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

    override fun onDestroyView() {
        super.onDestroyView()
        overviewRV?.adapter = null
    }

    override fun onRFACItemIconClick(position: Int, item: RFACLabelItem<Int>?) {
        showShortToast("Icon $position clicked")

        val minDate = DateUtil.toEpochMili(viewModel.startDate)
        val maxDate = DateUtil.toEpochMili(viewModel.endDate)

        val bundle = Bundle().apply {
            putInt(AddPlanFragment.PLAN_ITEM_TYPE, position)
            putLong(AddPlanFragment.MiNIMUM_DATE, minDate)
            putLong(AddPlanFragment.MAXIMUM_DATE, maxDate)
        }
        view?.findNavController()?.navigate(R.id.addPlanFragment, bundle)
    }

    override fun onRFACItemLabelClick(
        position: Int,
        item: RFACLabelItem<Int>?
    ) {
        showShortToast("Label ${item?.label} clicked")
    }

    private fun getRfabItems(context: Context): List<RFACLabelItem<Int>> =
        arrayListOf(
            RFACLabelItem<Int>()
                .setLabel(getString(R.string.exam_label))
                .setResId(R.drawable.ic_class_white_24dp)
                .setIconNormalColor(
                    context.getColorByResId(R.color.colorIconExam)
                )
                .setIconPressedColor(
                    context.getColorByResId(R.color.colorIconPressedExam)
                )
                .setWrapper(PlanItem.TYPE_EXAM),
            RFACLabelItem<Int>()
                .setLabel(getString(R.string.homework_label))
                .setResId(R.drawable.ic_description_white_24dp)
                .setIconNormalColor(
                    context.getColorByResId(R.color.colorIconHomework)
                )
                .setIconPressedColor(
                    context.getColorByResId(R.color.colorIconPressedHomework)
                )
                .setWrapper(PlanItem.TYPE_HOMEWORK),
            RFACLabelItem<Int>()
                .setLabel(getString(R.string.event_label))
                .setResId(R.drawable.ic_insert_invitation_white_24dp)
                .setIconNormalColor(
                    context.getColorByResId(R.color.colorIconEvent)
                )
                .setIconPressedColor(
                    context.getColorByResId(R.color.colorIconPressedEvent)
                )
                .setWrapper(PlanItem.TYPE_EVENT),
            RFACLabelItem<Int>()
                .setLabel(getString(R.string.reminder_label))
                .setResId(R.drawable.ic_access_time_white_24dp)
                .setIconNormalColor(
                    context.getColorByResId(R.color.colorIconReminder)
                )
                .setIconPressedColor(
                    context.getColorByResId(R.color.colorIconPressedReminder)
                )
                .setWrapper(PlanItem.TYPE_REMINDER)
        )

    private fun RecyclerView.scrollToToday() {
        scrollToPosition(App.DAYS_PASSED.toInt())
    }

    private fun addToView(exam: Exam) {
        viewModel.getExamWithSubject(exam.id).observe(this, Observer {
            val newExam = it.toPlanItem()
            viewModel.displayPlan(newExam)
        })
    }

    private fun addToView(homework: Homework) {
        viewModel.getHomeworkWithSubject(homework.id).observe(this, Observer {
            val newHomework = it.toPlanItem()
            viewModel.displayPlan(newHomework)
        })
    }

    private fun addToView(event: Event) {
        viewModel.event(event.id).observe(this, Observer {
            val newEvent = it.toPlanItem()
            viewModel.displayPlan(newEvent)
        })
    }

    private fun addToView(reminder: Reminder) {
        viewModel.reminder(reminder.id).observe(this, Observer {
            val newReminder = it.toPlanItem()
            viewModel.displayPlan(newReminder)
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
