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

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.jagoancoding.planyoursemester.AppRepository.defaultSubjectColor
import com.jagoancoding.planyoursemester.R
import com.jagoancoding.planyoursemester.util.ViewUtil.checkIfEmptyAndGetText

class AddSubjectDialog : DialogFragment() {
    lateinit var listener: DialogClickListener

    private lateinit var addSubjectNameTIL: TextInputLayout
    private lateinit var colorPickRect: View
    private var colorSelected: Int = 0

    interface DialogClickListener {

        fun onSubjectChosen(subjectName: String, color: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val defaultColor = defaultSubjectColor(context!!)
        val dialogView =
            activity?.layoutInflater?.inflate(R.layout.add_subject, null)
                ?.apply {
                    addSubjectNameTIL = findViewById(R.id.til_subject_name)
                    colorPickRect =
                        findViewById<View>(R.id.rect_color_picker).apply {
                            setBackgroundColor(defaultColor)
                            setOnClickListener {
                                clearFocus()
                                // Show color picker dialog
                                showColorPickerDialog(defaultColor)
                            }
                        }
                }

        val mDialog = AlertDialog.Builder(context!!)
            .setTitle(R.string.dialog_add_subj_title)
            .setView(dialogView)
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton(android.R.string.ok, null)
            .create()

        mDialog.setOnShowListener {
            mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                // Validation
                val subjectName = addSubjectNameTIL.checkIfEmptyAndGetText()
                if (subjectName.isNotBlank()) {
                    listener.onSubjectChosen(subjectName, colorSelected)
                    dialog?.dismiss()
                }
            }
        }

        return mDialog
    }

    private fun showColorPickerDialog(initialColor: Int) {
        ColorPickerDialogBuilder.with(context!!)
            .setTitle(R.string.dialog_add_subj_title)
            .initialColor(initialColor)
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            .density(12)
            .setOnColorSelectedListener {
                colorSelected = it
            }
            .setPositiveButton(R.string.select) { _, selectedColor, _ ->
                colorPickRect.setBackgroundColor(selectedColor)
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
            .build()
            .show()
    }
}