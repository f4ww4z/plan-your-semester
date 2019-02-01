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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jagoancoding.planyoursemester.R
import com.jagoancoding.planyoursemester.model.PlanItem
import kotlinx.android.synthetic.main.new_plan.view.tv_date
import kotlinx.android.synthetic.main.new_plan.view.tv_desc
import kotlinx.android.synthetic.main.new_plan.view.tv_subject
import kotlinx.android.synthetic.main.new_plan.view.tv_title

class PlanAdapter(private var data: List<PlanItem>) :
    RecyclerView.Adapter<PlanAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = LayoutInflater.from(parent.context)
        .inflate(R.layout.new_plan, parent, false)
        .run {
            ViewHolder(this)
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val planItem = data[position]
        with(holder.itemView) {
            tv_title.text = planItem.name
            tv_desc.setTextAndGoneIfEmpty(planItem.description)
            tv_date.text = planItem.getDateToDisplay(resources)
            tv_subject.setTextAndGoneIfEmpty(planItem.subject?.name)
        }
    }

    private fun TextView.setTextAndGoneIfEmpty(text: String?) {
        if (text.isNullOrEmpty() || text.isNullOrBlank()) {
            visibility = View.GONE
        } else {
            this.text = text
        }
    }

    fun setData(newData: List<PlanItem>) {
        data = newData
        notifyDataSetChanged()
    }
}