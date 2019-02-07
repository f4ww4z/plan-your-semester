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

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import com.jagoancoding.planyoursemester.R

object ViewUtil {

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

    fun TextInputLayout.getTextNotifyIfEmpty(): String {
        val tilText = editText?.text
        if (tilText.isNullOrEmpty()) {
            error = this.resources.getString(R.string.error_text_empty)
            return ""
        }
        return tilText.toString()
    }

    fun EditText.getTextNotifyIfEmpty(): String {
        val text = text
        if (text.isNullOrEmpty()) {
            error = this.resources.getString(R.string.error_text_empty)
            return ""
        }
        return text.toString()
    }
}