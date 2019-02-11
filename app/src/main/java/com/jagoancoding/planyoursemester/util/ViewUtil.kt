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
import androidx.core.graphics.drawable.DrawableCompat
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

    const val DATE = 164
    const val TIME = 165
    const val DATE_TIME = 166
    const val NON_DATE = 167

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

    fun TextView.setDrawableColor(colorId: Int) {
        DrawableCompat.setTint(this.background, colorId)
    }

    fun TextInputLayout.checkIfEmptyAndGetText(): String =
        editText!!.checkIfEmptyAndGetText()

    //TODO: Handle date validation
    fun EditText.checkIfEmptyAndGetText(): String {
        val textInputted = text

        if (textInputted.isNullOrBlank()) {
            error = resources.getString(R.string.error_text_empty)
            return ""
        }
        error = null
        return "$textInputted"
    }

    private fun baseDatePicker(
        minDate: MonthAdapter.CalendarDay,
        maxDate: MonthAdapter.CalendarDay,
        preselectedDate: LocalDate
    ): CalendarDatePickerDialogFragment = CalendarDatePickerDialogFragment()
        .setFirstDayOfWeek(Calendar.SUNDAY)
        .setDoneText("Select")
        .setCancelText("Cancel")
        .setDateRange(minDate, maxDate)
        .setPreselectedDate(
            preselectedDate.year,
            preselectedDate.month.value,
            preselectedDate.dayOfMonth
        )

    private fun baseTimePicker(fm: FragmentManager): TimePickerBuilder =
        TimePickerBuilder()
            .setFragmentManager(fm)
            .setStyleResId(R.style.BetterPickersDialogFragment)

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
                val dtp = baseDatePicker(minDate, maxDate, pre)
                    .setOnDateSetListener { _, year, monthOfYear, dayOfMonth ->

                        // When date picker is dismissed, show the time picker
                        val tpb = baseTimePicker(fm)
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

                dtp?.show(fm, DATE_TIME_PICKER_TAG)
            }

            true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun getDateWithPicker(
        editText: EditText,
        fm: FragmentManager,
        minDate: MonthAdapter.CalendarDay,
        maxDate: MonthAdapter.CalendarDay,
        pre: LocalDate
    ) {
        editText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {

                val dtp = baseDatePicker(minDate, maxDate, pre)
                    .setOnDateSetListener { _, year, monthOfYear, dayOfMonth ->

                        val dateString =
                            DateUtil.formatDate(year, monthOfYear, dayOfMonth)
                        editText.setText(dateString)
                    }

                dtp?.show(fm, DATE_TIME_PICKER_TAG)
            }

            true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun getTimeWithPicker(editText: EditText, fm: FragmentManager) {

        editText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val tpb = baseTimePicker(fm)
                    .addTimePickerDialogHandler { _, hourOfDay, minute ->
                        // Time is selected, set it in EditText
                        val time = DateUtil.formatTime(hourOfDay, minute)
                        editText.setText(time)
                    }
                tpb.show()
            }

            true
        }
    }
}