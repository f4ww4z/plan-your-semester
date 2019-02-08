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

package com.jagoancoding.planyoursemester.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter
import com.codetroopers.betterpickers.timepicker.TimePickerBuilder
import com.google.android.material.textfield.TextInputLayout
import com.jagoancoding.planyoursemester.R
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import java.util.Calendar

object ViewUtil {

    const val DATE_TIME_PICKER_TAG = "DateTimePicker"

    private var calDateTimePicker: CalendarDatePickerDialogFragment? = null

    fun TextView.setTextAndGoneIfEmpty(text: String?) {
        if (text.isNullOrEmpty() || text.isNullOrBlank()) {
            visibility = View.GONE
        } else {
            this.text = text
        }
    }

    fun calculatePx(dp: Int, r: Resources) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        r.displayMetrics
    ).toInt()

    fun Context.getColorByResId(resId: Int) =
        ContextCompat.getColor(this, resId)

    fun TextInputLayout.validateAndGetText(
        minDate: Long,
        maxDate: Long
    ): String {
        val tilText = editText?.text
        val min = DateUtil.getDate(minDate)
        val max = DateUtil.getDate(maxDate)
        if (tilText.isNullOrEmpty()) {
            error = this.resources.getString(R.string.error_text_empty)
            return ""
        } else {
            val date = DateUtil.parseDateTime("$tilText")
            if (date.toLocalDate().isAfter(max)
                || date.toLocalDate().isBefore(min)
            ) {
                error = context.getString(R.string.error_date_format)
                return ""
            }
        }
        return "$tilText"
    }

    fun EditText.validateAndGetText(
        minDate: Long,
        maxDate: Long
    ): String {
        val editText = text
        val min = DateUtil.getDate(minDate)
        val max = DateUtil.getDate(maxDate)
        if (editText.isNullOrEmpty()) {
            error = resources.getString(R.string.error_text_empty)
            return ""
        } else {
            val date = DateUtil.parseDateTime("$editText")
            if (date.toLocalDate().isAfter(max)
                || date.toLocalDate().isBefore(min)
            ) {
                error = context.getString(R.string.error_date_format)
                return ""
            }
        }
        return "$editText"
    }

    @SuppressLint("ClickableViewAccessibility")
    fun getDateAndTimeWithPicker(
        editText: EditText,
        fm: FragmentManager,
        minDate: MonthAdapter.CalendarDay,
        maxDate: MonthAdapter.CalendarDay,
        pre: LocalDate
    ) {
        var dateString: String

        editText.setOnTouchListener { _, event ->

            if (event.action == MotionEvent.ACTION_UP) {
                calDateTimePicker = CalendarDatePickerDialogFragment()
                    .setFirstDayOfWeek(Calendar.SUNDAY)
                    .setDoneText("Select")
                    .setCancelText("Cancel")
                    .setDateRange(minDate, maxDate)
                    .setPreselectedDate(
                        pre.year,
                        pre.month.value,
                        pre.dayOfMonth
                    )
                    .setOnDateSetListener { _, year, monthOfYear, dayOfMonth ->

                        // When date picker is dismissed, show the time picker
                        val tpb = TimePickerBuilder()
                            .setFragmentManager(fm)
                            .setStyleResId(R.style.BetterPickersDialogFragment)
                            .addTimePickerDialogHandler { _, hourOfDay, minute ->
                                // Time is selected, set it in EditText
                                val dateTime = LocalDateTime.of(
                                    year,
                                    monthOfYear,
                                    dayOfMonth,
                                    hourOfDay,
                                    minute
                                )
                                dateString =
                                    DateUtil.formatDateWithTime(dateTime)
                                editText.setText(dateString)
                            }
                        tpb.show()
                    }

                calDateTimePicker?.show(fm, DATE_TIME_PICKER_TAG)
            }

            true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun getTimeWithPicker(editText: EditText, fm: FragmentManager) {

        editText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val tpb = TimePickerBuilder()
                    .setFragmentManager(fm)
                    .setStyleResId(R.style.BetterPickersDialogFragment)
                    .addTimePickerDialogHandler { _, hourOfDay, minute ->
                        // Time is selected, set it in EditText
                        val time = DateUtil.formatTime(hourOfDay, minute)
                        editText.setText(time)
                    }
                tpb.show()
            }
            //TODO: Make input field just for adding date, and below it an input field for start time and end time
            //TODO: If homework or reminder, make date input field accept date AND time, else just date
            true
        }
    }
}