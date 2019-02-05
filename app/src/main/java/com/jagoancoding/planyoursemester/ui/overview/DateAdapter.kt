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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.jagoancoding.planyoursemester.R
import com.jagoancoding.planyoursemester.model.DateItem
import com.jagoancoding.planyoursemester.model.PlanItem
import com.jagoancoding.planyoursemester.util.ViewUtil.calculatePx
import com.jagoancoding.planyoursemester.util.ViewUtil.setTextAndGoneIfEmpty
import kotlinx.android.synthetic.main.date_card.view.rl_plan_items
import kotlinx.android.synthetic.main.date_card.view.tv_day
import kotlinx.android.synthetic.main.date_card.view.tv_day2
import kotlinx.android.synthetic.main.new_plan.view.tv_date
import kotlinx.android.synthetic.main.new_plan.view.tv_desc
import kotlinx.android.synthetic.main.new_plan.view.tv_subject
import kotlinx.android.synthetic.main.new_plan.view.tv_title

class DateAdapter(private var data: List<DateItem>) :
    RecyclerView.Adapter<DateAdapter.ViewHolder>() {

    companion object {
        private const val BASE_PLAN_ITEM_ID = 164
    }

    private lateinit var inflater: LayoutInflater

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
        with(holder.itemView) {
            tv_day.text = dateItem.dayOfWeek()
            tv_day2.text = dateItem.dayName()

            for (i in dateItem.planItems.indices) {
                dateItem.planItems[i].addToRootView(rl_plan_items, i, resources)
            }
        }
    }

    fun setData(newData: List<DateItem>) {
        data = newData
        notifyDataSetChanged()
    }

    private fun PlanItem.addToRootView(
        rootView: RelativeLayout,
        pos: Int,
        r: Resources
    ) {
        val planItemView: ConstraintLayout = inflater
            .inflate(R.layout.new_plan, rootView, false) as ConstraintLayout

        planItemView.id = BASE_PLAN_ITEM_ID + pos

        with(planItemView) {
            tv_title.text = name
            tv_desc.setTextAndGoneIfEmpty(description)
            tv_date.text = getDateToDisplay(resources)
            tv_subject.setTextAndGoneIfEmpty(subject?.name)
        }

        val params = planItemView.layoutParams as RelativeLayout.LayoutParams

        val idOfBelow: Int

        if (planItemView.id > BASE_PLAN_ITEM_ID) {
            idOfBelow = planItemView.id - 1

            // If it is the 2nd or more item in the list, add a margin above it
            params.setMargins(
                params.leftMargin,
                calculatePx(16, r),
                params.rightMargin,
                params.bottomMargin
            )
        } else {
            idOfBelow = rootView.id
        }
        params.addRule(RelativeLayout.BELOW, idOfBelow)

        rootView.addView(planItemView, params)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}