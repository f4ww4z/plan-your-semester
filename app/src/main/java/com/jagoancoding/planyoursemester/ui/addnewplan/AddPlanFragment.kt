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
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.google.android.material.textfield.TextInputLayout
import com.jagoancoding.planyoursemester.util.ViewUtil.getTextNotifyIfEmpty
import com.jagoancoding.planyoursemester.R
import com.jagoancoding.planyoursemester.model.PlanItem

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
    private var planItemType: Int? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var nameTIL: TextInputLayout
    private lateinit var descTIL: TextInputLayout
    private lateinit var startDateET: EditText
    private lateinit var endDateET: EditText
    private lateinit var dateTIL: TextInputLayout
    private lateinit var subjectTIL: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            planItemType = it.getInt(PLAN_ITEM_TYPE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =
            inflater.inflate(R.layout.fragment_add_plan, container, false)

        // Set up the app bar
        val toolbar: Toolbar? = activity?.findViewById(R.id.overview_toolbar)
        toolbar?.menu?.clear()

        toolbar?.title = when (planItemType) {
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

            if (item.itemId == R.menu.add_plan_menu) {
                validateInput()
            }

            val navController = view.findNavController()
            item.onNavDestinationSelected(navController)
                    || super.onOptionsItemSelected(item)
        }

        // Reference views
        with(view) {
            nameTIL = findViewById(R.id.til_plan_name)
            descTIL = findViewById(R.id.til_plan_desc)
            dateTIL = findViewById(R.id.til_plan_desc)
            startDateET = findViewById(R.id.et_plan_start_date)
            endDateET = findViewById(R.id.et_plan_end_date)
            subjectTIL = findViewById(R.id.til_plan_subject)
        }

        when (planItemType) {
            PlanItem.TYPE_EXAM -> {
                descTIL.isEnabled = false
                dateTIL.isEnabled = false
            }
            PlanItem.TYPE_HOMEWORK -> {
                startDateET.isEnabled = false
                endDateET.isEnabled = false
            }
            PlanItem.TYPE_EVENT -> {
                dateTIL.isEnabled = false
                subjectTIL.isEnabled = false
            }
            PlanItem.TYPE_REMINDER -> {
                descTIL.isEnabled = false
                startDateET.isEnabled = false
                endDateET.isEnabled = false
                subjectTIL.isEnabled = false
                //TODO: Reminder.isDone()
            }
        }

        return view
    }

    /**
     * Validate the data according to plan type
     */
    private fun validateInput() {
        var name: String = ""
        var desc: String = ""
        var startDate: String = ""
        var endDate: String = ""
        var date: String = ""
        var subject: String = ""

        name = nameTIL.getTextNotifyIfEmpty()

        when (planItemType) {
            PlanItem.TYPE_EXAM -> {
                startDate = startDateET.getTextNotifyIfEmpty()
                endDate = endDateET.getTextNotifyIfEmpty()
                //TODO: get subject names and make sure subject is one of them
                subject = subjectTIL.getTextNotifyIfEmpty()
            }
            PlanItem.TYPE_HOMEWORK -> {
                desc = descTIL.getTextNotifyIfEmpty()
                date = dateTIL.getTextNotifyIfEmpty()
                subject = subjectTIL.getTextNotifyIfEmpty()
            }
            PlanItem.TYPE_EVENT -> {
                desc = descTIL.getTextNotifyIfEmpty()
                startDate = startDateET.getTextNotifyIfEmpty()
                endDate = endDateET.getTextNotifyIfEmpty()
            }
            PlanItem.TYPE_REMINDER -> {
                date = dateTIL.getTextNotifyIfEmpty()
            }
        }

        //TODO: Finish validation
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

    companion object {
        const val PLAN_ITEM_TYPE = "PLAN_ITEM_TYPE"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Plan Item Type (can be of 4 values)
         * @return A new instance of fragment AddPlanFragment.
         */
        @JvmStatic
        fun newInstance(param1: Int) =
            AddPlanFragment().apply {
                arguments = Bundle().apply {
                    putInt(PLAN_ITEM_TYPE, param1)
                }
            }
    }
}
