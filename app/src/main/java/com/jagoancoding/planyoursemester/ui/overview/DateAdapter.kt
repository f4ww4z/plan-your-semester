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

import android.content.res.Resources
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.jagoancoding.planyoursemester.R
import com.jagoancoding.planyoursemester.model.DateItem
import com.jagoancoding.planyoursemester.model.PlanItem
import com.jagoancoding.planyoursemester.ui.addnewplan.AddPlanFragment
import com.jagoancoding.planyoursemester.util.ViewUtil.calculatePx
import com.jagoancoding.planyoursemester.util.ViewUtil.setTextAndGoneIfEmpty
import com.jagoancoding.planyoursemester.util.ViewUtil.getColorByResId

class DateAdapter(private var data: List<DateItem>) :
    RecyclerView.Adapter<DateAdapter.ViewHolder>() {

    companion object {
        private const val BASE_PLAN_ITEM_ID = 250601
    }

    private lateinit var inflater: LayoutInflater

    class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        val dayTV: TextView = root.findViewById(R.id.tv_day)
        val day2TV: TextView = root.findViewById(R.id.tv_day2)
        val planItemsRL: RelativeLayout = root.findViewById(R.id.rl_plan_items)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.date_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dateItem = data[position]
        with(holder) {
            dayTV.text = dateItem.dayOfWeek()
            day2TV.text = dateItem.dayName()
            planItemsRL.removeAllViewsInLayout()

            for (i in dateItem.planItems.indices) {
                planItemsRL.addItemView(
                    dateItem.planItems[i], i, itemView.resources
                )
            }
        }
    }

    fun setData(newData: List<DateItem>) {
        data = newData
        notifyDataSetChanged()
    }

    private fun RelativeLayout.addItemView(
        planItem: PlanItem,
        pos: Int,
        r: Resources
    ) {
        val id = BASE_PLAN_ITEM_ID + pos

        var planItemView: ConstraintLayout? = this.findViewById(id)
        this.removeView(planItemView)

        planItemView = inflater
            .inflate(R.layout.new_plan, this, false) as ConstraintLayout
        planItemView.id = id

        val planType = planItem.itemType

        with(planItemView) {
            // Background color depends on type of plan
            val bgColor = context.getColorByResId(
                when (planType) {
                    PlanItem.TYPE_EXAM -> R.color.colorBgExam
                    PlanItem.TYPE_HOMEWORK -> R.color.colorBgHomework
                    PlanItem.TYPE_EVENT -> R.color.colorBgEvent
                    PlanItem.TYPE_REMINDER -> R.color.colorBgReminder
                    else -> R.color.colorPrimaryLight
                }
            )
            this.setBackgroundColor(bgColor)

            val titleTV: TextView = this.findViewById(R.id.tv_title)
            val descriptionTV: TextView = this.findViewById(R.id.tv_desc)
            val dateTV: TextView = this.findViewById(R.id.tv_date)
            val subjectTV: TextView = this.findViewById(R.id.tv_subject)

            with(planItem) {
                titleTV.text = name
                descriptionTV.setTextAndGoneIfEmpty(description)
                dateTV.text = getDateToDisplay(resources)
                subjectTV.setTextAndGoneIfEmpty(subject?.name)

                // If homework or reminder is done, cross the text
                if (isDone != null) {
                    titleTV.paintFlags =
                        if (isDone!!) Paint.STRIKE_THRU_TEXT_FLAG else 0
                }

                // When clicked, update the plan item
                setOnClickListener {
                    val nc = it?.findNavController()
                    updatePlanItemScreen(nc, this)
                }
            }
        }

        val params =
            planItemView.layoutParams as RelativeLayout.LayoutParams

        val idOfBelow: Int

        if (planItemView.id > BASE_PLAN_ITEM_ID) {
            idOfBelow = planItemView.id - 1

            // If it is the 2nd or more item in the list, add a margin above it
            params.setMargins(
                params.leftMargin,
                calculatePx(8, r),
                params.rightMargin,
                params.bottomMargin
            )
        } else {
            idOfBelow = rootView.id
        }
        params.addRule(RelativeLayout.BELOW, idOfBelow)

        this.addView(planItemView, params)
    }

    private fun updatePlanItemScreen(nc: NavController?, planItem: PlanItem) {
        val bundle = Bundle().apply {
            putInt(AddPlanFragment.PLAN_ITEM_TYPE, planItem.itemType)
            putSerializable(AddPlanFragment.PLAN_ITEM_OBJECT, planItem)
        }
        nc?.navigate(R.id.addPlanFragment, bundle)
    }
}