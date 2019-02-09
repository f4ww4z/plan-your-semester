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
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter
import com.google.android.material.textfield.TextInputLayout
import com.jagoancoding.planyoursemester.R
import com.jagoancoding.planyoursemester.model.PlanItem
import com.jagoancoding.planyoursemester.ui.MainViewModel
import com.jagoancoding.planyoursemester.util.DateUtil
import com.jagoancoding.planyoursemester.util.ViewUtil
import com.jagoancoding.planyoursemester.util.ViewUtil.checkIfEmptyAndGetText

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AddPlanFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AddPlanFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class AddPlanFragment : Fragment() {

    companion object {
        const val PLAN_ITEM_TYPE = "PLAN_ITEM_TYPE"
        const val MiNIMUM_DATE = "MINIMUM_DATE"
        const val MAXIMUM_DATE = "MAXIMUM_DATE"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param planItemType Plan Item Type (can be of 4 values)
         * @param minDate minimum date in Date Picker
         * @param maxDate maximum date allowed in Date Picker
         * @return A new instance of fragment AddPlanFragment.
         */
        @JvmStatic
        fun newInstance(planItemType: Int, minDate: Long, maxDate: Long) =
            AddPlanFragment().apply {
                arguments = Bundle().apply {
                    putInt(PLAN_ITEM_TYPE, planItemType)
                    putLong(MiNIMUM_DATE, minDate)
                    putLong(MAXIMUM_DATE, maxDate)
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

    private var isValidated = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vm = ViewModelProviders
            .of(this)
            .get(MainViewModel::class.java)

        arguments?.apply {
            vm.planTypeToAdd = getInt(PLAN_ITEM_TYPE)
            vm.minimumDate = getLong(MiNIMUM_DATE)
            vm.maximumDate = getLong(MAXIMUM_DATE)
        }

        // Inflate the layout for this fragment
        val view =
            inflater.inflate(
                R.layout.fragment_add_plan,
                container,
                false
            ) as ConstraintLayout

        // Set up the app bar
        val toolbar: Toolbar? = activity?.findViewById(R.id.overview_toolbar)
        toolbar?.menu?.clear()

        toolbar?.title = when (vm.planTypeToAdd) {
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
        toolbar?.inflateMenu(R.menu.add_plan_menu)
        toolbar?.setOnMenuItemClickListener { item ->

            if (item.itemId == R.id.overviewFragment) {
                validateInput()
                if (isValidated) {
                    val navController = view.findNavController()
                    view.clearFocus()
                    item.onNavDestinationSelected(navController)
                }
            }

            super.onOptionsItemSelected(item)
        }

        // Reference views
        with(view) {
            nameTIL = findViewById(R.id.til_plan_name)
            descTIL = findViewById(R.id.til_plan_desc)
            dateTIL = findViewById(R.id.til_plan_date)
            startDateET = findViewById(R.id.et_plan_start_date)
            endDateET = findViewById(R.id.et_plan_end_date)
            subjectTIL = findViewById(R.id.til_plan_subject)
        }

        setupViews(fragmentManager!!)

        return view
    }

    private fun setupViews(fm: FragmentManager) {
        val minDateForPicker = MonthAdapter.CalendarDay(vm.minimumDate)
        val maxDateForPicker = MonthAdapter.CalendarDay(vm.maximumDate)

        nameTIL.isEnabled = true

        when (vm.planTypeToAdd) {
            PlanItem.TYPE_EXAM -> {

                descTIL.isEnabled = false

                dateTIL.hint = resources.getString(R.string.plan_date)
                ViewUtil.getDateWithPicker(
                    dateTIL.editText!!,
                    fm,
                    minDateForPicker,
                    maxDateForPicker,
                    vm.today
                )
                ViewUtil.getTimeWithPicker(startDateET, fm)
                ViewUtil.getTimeWithPicker(endDateET, fm)
            }
            PlanItem.TYPE_HOMEWORK -> {

                dateTIL.hint = resources.getString(R.string.plan_date_time)
                ViewUtil.getDateAndTimeWithPicker(
                    dateTIL.editText!!,
                    fm,
                    minDateForPicker,
                    maxDateForPicker,
                    vm.today
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
                    vm.today
                )
                ViewUtil.getTimeWithPicker(startDateET, fm)
                ViewUtil.getTimeWithPicker(endDateET, fm)

                subjectTIL.isEnabled = false
            }
            PlanItem.TYPE_REMINDER -> {

                descTIL.isEnabled = false

                dateTIL.hint = resources.getString(R.string.plan_date_time)
                ViewUtil.getDateAndTimeWithPicker(
                    dateTIL.editText!!,
                    fm,
                    minDateForPicker,
                    maxDateForPicker,
                    vm.today
                )

                startDateET.isEnabled = false
                endDateET.isEnabled = false
                subjectTIL.isEnabled = false

                //TODO: Reminder.isDone()
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

        name = nameTIL.checkIfEmptyAndGetText()

        when (vm.planTypeToAdd) {
            PlanItem.TYPE_EXAM -> {
                dateTime = dateTIL.checkIfEmptyAndGetText()
                startTime = "$dateTime ${startDateET.checkIfEmptyAndGetText()}"
                endTime = "$dateTime ${endDateET.checkIfEmptyAndGetText()}"
                //TODO: get subject names and make sure subject is one of them
                subject = subjectTIL.checkIfEmptyAndGetText()

                isValidated = vm.validateData(
                    vm.planTypeToAdd,
                    name = name,
                    startTime = startTime,
                    endTime = endTime,
                    subject = subject
                )
            }
            PlanItem.TYPE_HOMEWORK -> {
                desc = descTIL.checkIfEmptyAndGetText()
                dateTime = dateTIL.checkIfEmptyAndGetText()
                subject = subjectTIL.checkIfEmptyAndGetText()

                isValidated = vm.validateData(
                    vm.planTypeToAdd,
                    name = name,
                    desc = desc,
                    dateTime = dateTime,
                    subject = subject
                )
            }
            PlanItem.TYPE_EVENT -> {
                desc = descTIL.checkIfEmptyAndGetText()
                dateTime = dateTIL.checkIfEmptyAndGetText()
                startTime = "$dateTime ${startDateET.checkIfEmptyAndGetText()}"
                endTime = "$dateTime ${endDateET.checkIfEmptyAndGetText()}"

                isValidated = vm.validateData(
                    vm.planTypeToAdd,
                    name = name,
                    desc = desc,
                    startTime = startTime,
                    endTime = endTime
                )
            }
            PlanItem.TYPE_REMINDER -> {
                dateTime = dateTIL.checkIfEmptyAndGetText()

                isValidated = vm.validateData(
                    vm.planTypeToAdd,
                    name = name,
                    dateTime = dateTime
                )
            }
        }

        if (isValidated) {
            addPlanToDatabase(name, desc, startTime, endTime, dateTime, subject)
        }
    }

    private fun addPlanToDatabase(
        name: String,
        desc: String = "",
        startTime: String = "",
        endTime: String = "",
        dt: String = "",
        subject: String = ""
    ) {
        // All input fields are valid, add the plan to database
        when (vm.planTypeToAdd) {
            PlanItem.TYPE_EXAM -> {
                val startEpoch: Long =
                    DateUtil.toEpochMili(startTime, ViewUtil.DATE_TIME)
                val endEpoch: Long =
                    DateUtil.toEpochMili(endTime, ViewUtil.DATE_TIME)
                vm.addOrUpdateExam(name, subject, startEpoch, endEpoch)
            }
            PlanItem.TYPE_HOMEWORK -> {
                val epoch: Long = DateUtil.toEpochMili(dt, ViewUtil.DATE_TIME)
                vm.addOrUpdateHomework(name, subject, epoch, desc, false)
            }
            PlanItem.TYPE_EVENT -> {
                val startEpoch: Long =
                    DateUtil.toEpochMili(startTime, ViewUtil.DATE_TIME)
                val endEpoch: Long =
                    DateUtil.toEpochMili(endTime, ViewUtil.DATE_TIME)
                vm.addOrUpdateEvent(name, startEpoch, endEpoch, desc)
            }
            PlanItem.TYPE_REMINDER -> {
                val epoch: Long = DateUtil.toEpochMili(dt, ViewUtil.DATE_TIME)
                vm.addOrUpdateReminder(name, epoch, false)
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
