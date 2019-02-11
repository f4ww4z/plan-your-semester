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

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter
import com.google.android.material.textfield.TextInputLayout
import com.jagoancoding.planyoursemester.AppRepository
import com.jagoancoding.planyoursemester.R
import com.jagoancoding.planyoursemester.model.PlanItem
import com.jagoancoding.planyoursemester.ui.MainViewModel
import com.jagoancoding.planyoursemester.util.DateUtil
import com.jagoancoding.planyoursemester.util.ToastUtil.showLongToast
import com.jagoancoding.planyoursemester.util.ViewUtil
import com.jagoancoding.planyoursemester.util.ViewUtil.checkIfEmptyAndGetText
import org.threeten.bp.LocalDate

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AddPlanFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AddPlanFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AddPlanFragment : Fragment(), Toolbar.OnMenuItemClickListener {

    companion object {
        const val PLAN_ITEM_TYPE = "PLAN_ITEM_TYPE"
        const val PLAN_ITEM_OBJECT = "PLAN_ITEM_OBJ"
        const val MiNIMUM_DATE = "MINIMUM_DATE"
        const val MAXIMUM_DATE = "MAXIMUM_DATE"
        const val INSERT_STATE = "OnInsert"
        const val UPDATE_STATE = "OnUpdate"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param planItemType Plan Item Type (can be of 4 values)
         * @param planItem Plan item to load
         * @return A new instance of fragment AddPlanFragment.
         */
        @JvmStatic
        fun newInstance(planItemType: Int, planItem: PlanItem) =
            AddPlanFragment().apply {
                arguments = Bundle().apply {
                    putInt(PLAN_ITEM_TYPE, planItemType)
                    putSerializable(PLAN_ITEM_OBJECT, planItem)
                }
            }
    }

    private lateinit var vm: MainViewModel

    private var listener: OnFragmentInteractionListener? = null

    private lateinit var nameTIL: TextInputLayout
    private lateinit var descTIL: TextInputLayout
    private lateinit var startDateET: EditText
    private lateinit var endDateET: EditText
    private lateinit var dateTIL: TextInputLayout
    private lateinit var subjectTIL: TextInputLayout
    private lateinit var isDoneCB: CheckBox

    private lateinit var state: String
    private var isValidated = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vm = ViewModelProviders
            .of(activity!!)
            .get(MainViewModel::class.java)

        arguments?.apply {
            vm.currentPlanItemType = getInt(PLAN_ITEM_TYPE)
            vm.currentPlanItem = getSerializable(PLAN_ITEM_OBJECT) as PlanItem?
        }
        state = if (vm.currentPlanItem == null) INSERT_STATE else UPDATE_STATE

        // Inflate the layout for this fragment
        val view =
            inflater.inflate(
                R.layout.fragment_add_plan,
                container,
                false
            ) as ConstraintLayout

        setupAppBar()

        // Reference views
        with(view) {
            nameTIL = findViewById(R.id.til_plan_name)
            descTIL = findViewById(R.id.til_plan_desc)
            dateTIL = findViewById(R.id.til_plan_date)
            startDateET = findViewById(R.id.et_plan_start_date)
            endDateET = findViewById(R.id.et_plan_end_date)
            subjectTIL = findViewById(R.id.til_plan_subject)
            isDoneCB = findViewById(R.id.cb_done)
        }

        setupViews(fragmentManager!!)

        return view
    }

    override fun onMenuItemClick(item: MenuItem) = when (item.itemId) {
        R.id.action_add_or_update_plan -> {
            validateInput()
            if (isValidated) {
                view!!.clearFocus()
                val navController = view!!.findNavController()
                navController.navigate(R.id.overviewFragment)
            }
            true
        }
        R.id.action_delete_plan -> {
            if (context != null && state == UPDATE_STATE) {
                showPlanDeleteDialog(context!!, vm.currentPlanItem!!)
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    /**
     * Set up the custom activity Toolbar
     */
    private fun setupAppBar() {
        val toolbar: Toolbar? = activity?.findViewById(R.id.overview_toolbar)
        toolbar?.menu?.clear()

        if (state == INSERT_STATE) {
            toolbar?.title = when (vm.currentPlanItemType) {
                PlanItem.TYPE_EXAM -> getString(
                    R.string.add_new, getString(R.string.exam_label)
                )
                PlanItem.TYPE_HOMEWORK -> getString(
                    R.string.add_new, getString(R.string.homework_label)
                )
                PlanItem.TYPE_EVENT -> getString(
                    R.string.add_new, getString(R.string.event_label)
                )
                PlanItem.TYPE_REMINDER -> getString(
                    R.string.add_new, getString(R.string.reminder_label)
                )
                else -> getString(R.string.add_new, "")
            }
        } else {
            toolbar?.title = when (vm.currentPlanItemType) {
                PlanItem.TYPE_EXAM -> getString(
                    R.string.update_plan, getString(R.string.exam_label)
                )
                PlanItem.TYPE_HOMEWORK -> getString(
                    R.string.update_plan, getString(R.string.homework_label)
                )
                PlanItem.TYPE_EVENT -> getString(
                    R.string.update_plan, getString(R.string.event_label)
                )
                PlanItem.TYPE_REMINDER -> getString(
                    R.string.update_plan, getString(R.string.reminder_label)
                )
                else -> getString(R.string.add_new, "")
            }
        }

        if (vm.currentPlanItem != null) {
            vm.scrollToDate = DateUtil.getDate(
                vm.currentPlanItem!!.date
                    ?: vm.currentPlanItem!!.startDate!!
            )
        }

        toolbar?.inflateMenu(R.menu.add_plan_menu)
        toolbar?.setOnMenuItemClickListener(this)
    }


    private fun showPlanDeleteDialog(context: Context, plan: PlanItem) {
        with(plan) {
            val title = context.getString(
                R.string.dialog_remove_plan_title, when (itemType) {
                    PlanItem.TYPE_EXAM -> context.getString(R.string.exam_label)
                    PlanItem.TYPE_HOMEWORK -> context.getString(R.string.homework_label)
                    PlanItem.TYPE_EVENT -> context.getString(R.string.event_label)
                    PlanItem.TYPE_REMINDER -> context.getString(R.string.reminder_label)
                    else -> context.getString(R.string.plan)
                }
            )
            val message =
                context.getString(R.string.dialog_remove_plan_mes, name)

            val confirmDeleteDialog = AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    when (itemType) {
                        PlanItem.TYPE_EXAM -> vm.deleteExam(
                            DateUtil.getDate(startDate!!), id
                        )
                        PlanItem.TYPE_HOMEWORK -> vm.deleteHomework(
                            DateUtil.getDate(date!!), id
                        )
                        PlanItem.TYPE_EVENT -> vm.deleteEvent(
                            DateUtil.getDate(startDate!!), id
                        )
                        PlanItem.TYPE_REMINDER -> vm.deleteReminder(
                            DateUtil.getDate(date!!), id
                        )
                    }

                    // Go back to overview fragment
                    view!!.clearFocus()
                    val navController = view!!.findNavController()
                    navController.navigate(R.id.overviewFragment)
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
            confirmDeleteDialog.show()
        }
    }

    private fun setupViews(fm: FragmentManager) {
        val minDateForPicker =
            MonthAdapter.CalendarDay(AppRepository.minimumDate)
        val maxDateForPicker =
            MonthAdapter.CalendarDay(AppRepository.maximumDate)

        val preselectedDate = if (state == INSERT_STATE) {
            AppRepository.today
        } else {
            fillUpViewsWhenUpdatingPlan(vm.currentPlanItem!!)
            DateUtil.getDate(
                vm.currentPlanItem?.date ?: vm.currentPlanItem?.startDate!!
            )
        }

        when (vm.currentPlanItemType) {
            PlanItem.TYPE_EXAM -> {

                descTIL.isEnabled = false

                dateTIL.hint = resources.getString(R.string.plan_date)

                ViewUtil.getDateWithPicker(
                    dateTIL.editText!!,
                    fm,
                    minDateForPicker,
                    maxDateForPicker,
                    preselectedDate
                )
                ViewUtil.getTimeWithPicker(startDateET, fm)
                ViewUtil.getTimeWithPicker(endDateET, fm)

                isDoneCB.isEnabled = false
            }
            PlanItem.TYPE_HOMEWORK -> {

                dateTIL.hint =
                    resources.getString(R.string.homework_hint_date)
                ViewUtil.getDateAndTimeWithPicker(
                    dateTIL.editText!!,
                    fm,
                    minDateForPicker,
                    maxDateForPicker,
                    preselectedDate
                )

                startDateET.isEnabled = false
                endDateET.isEnabled = false
            }
            PlanItem.TYPE_EVENT -> {

                dateTIL.hint = resources.getString(R.string.plan_date)
                ViewUtil.getDateWithPicker(
                    dateTIL.editText!!,
                    fm,
                    minDateForPicker,
                    maxDateForPicker,
                    preselectedDate
                )
                ViewUtil.getTimeWithPicker(startDateET, fm)
                ViewUtil.getTimeWithPicker(endDateET, fm)

                subjectTIL.isEnabled = false
                isDoneCB.isEnabled = false
            }
            PlanItem.TYPE_REMINDER -> {

                descTIL.isEnabled = false

                dateTIL.hint = resources.getString(R.string.plan_date_time)
                ViewUtil.getDateAndTimeWithPicker(
                    dateTIL.editText!!,
                    fm,
                    minDateForPicker,
                    maxDateForPicker,
                    preselectedDate
                )

                startDateET.isEnabled = false
                endDateET.isEnabled = false
                subjectTIL.isEnabled = false

                //TODO: Reminder.isDone()
            }
        }
    }

    private fun fillUpViewsWhenUpdatingPlan(planItem: PlanItem) {
        with(planItem) {
            nameTIL.editText?.setText(name)

            when (planItem.itemType) {
                PlanItem.TYPE_EXAM -> {

                    val startLdt = DateUtil.getDateTime(startDate!!)
                    val endLdt = DateUtil.getDateTime(endDate!!)
                    dateTIL.editText?.setText(
                        DateUtil.formatDate(
                            startLdt.year,
                            startLdt.month.value,
                            startLdt.dayOfMonth
                        )
                    )
                    startDateET.setText(
                        DateUtil.formatTime(
                            startLdt.hour, startLdt.minute
                        )
                    )
                    endDateET.setText(
                        DateUtil.formatTime(
                            endLdt.hour, endLdt.minute
                        )
                    )
                    subjectTIL.editText?.setText(subject?.name)
                }
                PlanItem.TYPE_HOMEWORK -> {
                    descTIL.editText?.setText(description)
                    val ldt = DateUtil.getDateTime(date!!)
                    dateTIL.editText?.setText(
                        DateUtil.formatDateWithTime(ldt)
                    )
                    subjectTIL.editText?.setText(subject?.name)
                    isDoneCB.isChecked = isDone!!
                }
                PlanItem.TYPE_EVENT -> {
                    val startLdt = DateUtil.getDateTime(startDate!!)
                    val endLdt = DateUtil.getDateTime(endDate!!)
                    descTIL.editText?.setText(description)
                    dateTIL.editText?.setText(
                        DateUtil.formatDate(
                            startLdt.year,
                            startLdt.month.value,
                            startLdt.dayOfMonth
                        )
                    )
                    startDateET.setText(
                        DateUtil.formatTime(
                            startLdt.hour, startLdt.minute
                        )
                    )
                    endDateET.setText(
                        DateUtil.formatTime(
                            endLdt.hour, endLdt.minute
                        )
                    )
                }
                PlanItem.TYPE_REMINDER -> {
                    val ldt = DateUtil.getDateTime(date!!)
                    dateTIL.editText?.setText(
                        DateUtil.formatDateWithTime(ldt)
                    )
                    isDoneCB.isChecked = isDone!!
                }
                else -> return
            }
        }
    }

    /**
     * Validate the data according to plan type
     */
    private fun validateInput() {
        val name: String
        var desc = ""
        var startTime = ""
        var endTime = ""
        var dateTime = ""
        var subject = ""
        var isDone: Boolean? = null

        name = nameTIL.checkIfEmptyAndGetText()

        when (vm.currentPlanItemType) {
            PlanItem.TYPE_EXAM -> {
                dateTime = dateTIL.checkIfEmptyAndGetText()
                startTime =
                    "$dateTime ${startDateET.checkIfEmptyAndGetText()}"
                endTime = "$dateTime ${endDateET.checkIfEmptyAndGetText()}"
                //TODO: get subject names and make sure subject is one of them
                subject = subjectTIL.checkIfEmptyAndGetText()

                isValidated = vm.validateData(
                    vm.currentPlanItemType,
                    name = name,
                    startTime = startTime,
                    endTime = endTime,
                    subject = subject
                )
            }
            PlanItem.TYPE_HOMEWORK -> {
                desc = descTIL.editText?.text?.toString() ?: ""
                dateTime = dateTIL.checkIfEmptyAndGetText()
                subject = subjectTIL.checkIfEmptyAndGetText()
                isDone = isDoneCB.isChecked

                isValidated = vm.validateData(
                    vm.currentPlanItemType,
                    name = name,
                    desc = desc,
                    dateTime = dateTime,
                    subject = subject
                )
            }
            PlanItem.TYPE_EVENT -> {
                desc = descTIL.editText?.text?.toString() ?: ""
                dateTime = dateTIL.checkIfEmptyAndGetText()
                startTime =
                    "$dateTime ${startDateET.checkIfEmptyAndGetText()}"
                endTime = "$dateTime ${endDateET.checkIfEmptyAndGetText()}"

                isValidated = vm.validateData(
                    vm.currentPlanItemType,
                    name = name,
                    desc = desc,
                    startTime = startTime,
                    endTime = endTime
                )
            }
            PlanItem.TYPE_REMINDER -> {
                dateTime = dateTIL.checkIfEmptyAndGetText()
                isDone = isDoneCB.isChecked

                isValidated = vm.validateData(
                    vm.currentPlanItemType,
                    name = name,
                    dateTime = dateTime
                )
            }
        }

        if (isValidated) {
            addOrUpdatePlan(
                vm.currentPlanItem?.id,
                name,
                desc,
                startTime,
                endTime,
                dateTime,
                subject,
                isDone
            )
        }
    }

    private fun addOrUpdatePlan(
        id: Long?,
        name: String,
        desc: String = "",
        startTime: String = "",
        endTime: String = "",
        dt: String = "",
        subject: String = "",
        isDone: Boolean? = null
    ) {
        // All input fields are valid, add the plan to database
        when (vm.currentPlanItemType) {
            PlanItem.TYPE_EXAM -> {
                val startEpoch: Long =
                    DateUtil.toEpochMili(startTime, ViewUtil.DATE_TIME)
                val endEpoch: Long =
                    DateUtil.toEpochMili(endTime, ViewUtil.DATE_TIME)
                if (state == INSERT_STATE) {
                    vm.addExam(name, subject, startEpoch, endEpoch)
                    context?.showLongToast(
                        getString(
                            R.string.success_plan_added,
                            getString(R.string.exam_label)
                        )
                    )
                } else {
                    vm.updateExam(id!!, name, subject, startEpoch, endEpoch)
                    context?.showLongToast(
                        getString(
                            R.string.success_plan_update,
                            getString(R.string.exam_label)
                        )
                    )
                }
            }
            PlanItem.TYPE_HOMEWORK -> {
                val epoch: Long =
                    DateUtil.toEpochMili(dt, ViewUtil.DATE_TIME)

                if (state == INSERT_STATE) {
                    vm.addHomework(name, subject, epoch, desc, isDone!!)
                    context?.showLongToast(
                        getString(
                            R.string.success_plan_added,
                            getString(R.string.homework_label)
                        )
                    )
                } else {
                    //TODO: Make isDone checkbox and update code here
                    vm.updateHomework(
                        id!!,
                        name,
                        subject,
                        epoch,
                        desc,
                        isDone!!
                    )
                    context?.showLongToast(
                        getString(
                            R.string.success_plan_update,
                            getString(R.string.homework_label)
                        )
                    )
                }
            }
            PlanItem.TYPE_EVENT -> {
                val startEpoch: Long =
                    DateUtil.toEpochMili(startTime, ViewUtil.DATE_TIME)
                val endEpoch: Long =
                    DateUtil.toEpochMili(endTime, ViewUtil.DATE_TIME)

                if (state == INSERT_STATE) {
                    vm.addEvent(name, startEpoch, endEpoch, desc)
                    context?.showLongToast(
                        getString(
                            R.string.success_plan_added,
                            getString(R.string.event_label)
                        )
                    )
                } else {
                    vm.updateEvent(id!!, name, startEpoch, endEpoch, desc)
                    context?.showLongToast(
                        getString(
                            R.string.success_plan_update,
                            getString(R.string.event_label)
                        )
                    )
                }
            }
            PlanItem.TYPE_REMINDER -> {
                val epoch: Long =
                    DateUtil.toEpochMili(dt, ViewUtil.DATE_TIME)

                if (state == INSERT_STATE) {
                    vm.addReminder(name, epoch, isDone!!)
                    context?.showLongToast(
                        getString(
                            R.string.success_plan_added,
                            getString(R.string.reminder_label)
                        )
                    )
                } else {
                    //TODO: Make isDone checkbox and update code here
                    vm.updateReminder(id!!, name, epoch, isDone!!)
                    context?.showLongToast(
                        getString(
                            R.string.success_plan_update,
                            getString(R.string.reminder_label)
                        )
                    )
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }
}
