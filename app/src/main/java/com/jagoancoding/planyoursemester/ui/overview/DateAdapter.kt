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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jagoancoding.planyoursemester.R
import com.jagoancoding.planyoursemester.model.DateItem
import kotlinx.android.synthetic.main.date_card.view.tv_day
import kotlinx.android.synthetic.main.date_card.view.tv_day2
import kotlinx.android.synthetic.main.overview_fragment.view.rv_overview

class DateAdapter(private var data: List<DateItem>) :
    RecyclerView.Adapter<DateAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder = LayoutInflater.from(parent.context)
        .inflate(R.layout.date_card, parent, false)
        .run {
            ViewHolder(this)
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dateItem = data[position]
        with(holder.itemView) {
            tv_day.text = dateItem.getDayOfMonth()
            tv_day2.text = dateItem.getDayName(resources)
            rv_overview.apply {
                adapter = PlanAdapter(dateItem.planItems)
                layoutManager = LinearLayoutManager(this.context)
                setHasFixedSize(false)
            }
        }
    }

    fun setData(newData: List<DateItem>) {
        data = newData
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}