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
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jagoancoding.planyoursemester.App
import com.jagoancoding.planyoursemester.AppRepository
import com.jagoancoding.planyoursemester.R
import com.jagoancoding.planyoursemester.db.Event
import com.jagoancoding.planyoursemester.db.Exam
import com.jagoancoding.planyoursemester.db.Homework
import com.jagoancoding.planyoursemester.db.Reminder
import com.jagoancoding.planyoursemester.model.PlanItem
import com.jagoancoding.planyoursemester.ui.MainViewModel
import com.jagoancoding.planyoursemester.ui.addnewplan.AddPlanFragment
import com.jagoancoding.planyoursemester.util.DataUtil.observeOnce
import com.jagoancoding.planyoursemester.util.DateUtil
import com.jagoancoding.planyoursemester.util.DateUtil.findDatePositionInList
import com.jagoancoding.planyoursemester.util.ToastUtil.showLongToast
import com.jagoancoding.planyoursemester.util.ViewUtil.getColorByResId
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList
import org.threeten.bp.LocalDate

class OverviewFragment : Fragment(),
    RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener<Int>,
    AddSubjectDialog.DialogClickListener,
    RemoveSubjectDialog.DialogClickListener {

    companion object {
        const val TAG = "OverviewFragment"
        const val KEY_SCROLL_TO_DATE = "SCROLL TO DATE"
        fun newInstance() = OverviewFragment()
    }

    private lateinit var viewModel: MainViewModel

    private var toolbar: Toolbar? = null
    private var rfal: RapidFloatingActionLayout? = null
    private var rfab: RapidFloatingActionButton? = null
    private var overviewRV: RecyclerView? = null
    private lateinit var dateAdapter: DateAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.overview_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders
            .of(activity!!)
            .get(MainViewModel::class.java)

        setupAppBar()

        overviewRV = view?.findViewById(R.id.rv_overview)
        dateAdapter = DateAdapter(ArrayList())

        // Get the date to scroll to in the Overview screen
        val scrollToDateEpoch = arguments?.getLong(KEY_SCROLL_TO_DATE)
        val scrollToDate =
            if (scrollToDateEpoch == null)
                null
            else
                DateUtil.getDate(scrollToDateEpoch)

        overviewRV?.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = dateAdapter

            scrollToDate(scrollToDate ?: AppRepository.today)
        }

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
        viewModel.listItems.observe(this, Observer { listItems ->
            (overviewRV!!.adapter as DateAdapter).setData(listItems)

            viewModel.subjectNames()
                .observe(this, Observer { subjectNames ->
                    viewModel.countSubjectUsage(subjectNames, listItems)
                })
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

    private fun setupAppBar() {
        toolbar = activity?.findViewById(R.id.overview_toolbar)
        toolbar?.menu?.clear()
        toolbar?.inflateMenu(R.menu.overview_menu)
        toolbar?.setOnMenuItemClickListener {
            view?.clearFocus()

            when (it.itemId) {
                R.id.action_add_subject -> {
                    val addSubjectDialog = AddSubjectDialog().apply {
                        listener = this@OverviewFragment
                    }
                    addSubjectDialog.show(fragmentManager!!, "AddSubjectDialog")

                    true
                }
                R.id.action_rm_subject -> {
                    viewModel.subjectNames().observeOnce(Observer { names ->

                        if (names.isNullOrEmpty()) {
                            context?.showLongToast(R.string.no_subjects_found)
                        } else {
                            val dialog = RemoveSubjectDialog().apply {
                                listener = this@OverviewFragment
                                subjectNames = names.toTypedArray()
                            }
                            dialog.show(
                                fragmentManager!!, "RemoveSubjectDialog"
                            )
                        }
                    })
                    true
                }
                else -> super.onOptionsItemSelected(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        overviewRV?.adapter = null
    }

    override fun onRFACItemIconClick(position: Int, item: RFACLabelItem<Int>?) {
        // context?.showShortToast("Icon $position clicked")

        // Pass subject names first
        viewModel.subjectNames().observeOnce(Observer { subjectNames ->

            // Check if more than 1 subject is registered when clicking Exam
            // or Assignment
            if (subjectNames.isNullOrEmpty() &&
                (position == PlanItem.TYPE_EXAM || position == PlanItem.TYPE_HOMEWORK)
            ) {
                // Show an error message
                context?.showLongToast(R.string.subjects_empty)
            } else {
                val bundle = Bundle().apply {
                    putInt(AddPlanFragment.PLAN_ITEM_TYPE, position)
                    putLong(
                        AddPlanFragment.MiNIMUM_DATE, AppRepository.minimumDate
                    )
                    putLong(
                        AddPlanFragment.MAXIMUM_DATE, AppRepository.maximumDate
                    )
                    putStringArrayList(
                        AddPlanFragment.SUBJECT_NAMES_COL,
                        ArrayList<String>(subjectNames)
                    )
                }
                view?.findNavController()
                    ?.navigate(R.id.addPlanFragment, bundle)
            }
        })
    }

    override fun onRFACItemLabelClick(
        position: Int,
        item: RFACLabelItem<Int>?
    ) {
        // context?.showShortToast("Label ${item?.label} clicked")

        // Same behaviour when clicking label as clicking icon
        onRFACItemIconClick(position, item)
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

    private fun RecyclerView.scrollToDate(date: LocalDate) {
        viewModel.listItems.observeOnce(Observer {
            val position = it?.findDatePositionInList(date)

            if (position != null) {
                this.scrollToPosition(position)
            }
        })
    }

    override fun onSubjectChosen(subjectName: String, color: Int) {
        viewModel.addSubject(subjectName, color)
        context?.showLongToast(
            getString(
                R.string.success_subj_add,
                subjectName
            )
        )
    }

    override fun onSubjectToRemoveSelected(subjectId: String) {
        if (AppRepository.subjectInstances[subjectId] == null ||
            AppRepository.subjectInstances[subjectId]!! == 0
        ) {
            viewModel.deleteSubject(subjectId)
            AppRepository.subjectInstances.remove(subjectId)
            context?.showLongToast(
                getString(R.string.success_subj_rm, subjectId)
            )
        } else {
            context?.showLongToast(getString(R.string.fail_subj_rm, subjectId))
        }
    }

    private fun addToView(exam: Exam) {
        viewModel.getExamWithSubject(exam.exam_id).observeOnce(Observer {
            val newExam = it.toPlanItem()
            viewModel.displayPlan(newExam)
        })
    }

    private fun addToView(homework: Homework) {
        viewModel.getHomeworkWithSubject(homework.homework_id).observeOnce(
            Observer {
                val newHomework = it.toPlanItem()
                viewModel.displayPlan(newHomework)
            })
    }

    private fun addToView(event: Event) {
        viewModel.event(event.event_id).observeOnce(Observer {
            val newEvent = it.toPlanItem()
            viewModel.displayPlan(newEvent)
        })
    }

    private fun addToView(reminder: Reminder) {
        viewModel.reminder(reminder.reminder_id).observeOnce(Observer {
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
