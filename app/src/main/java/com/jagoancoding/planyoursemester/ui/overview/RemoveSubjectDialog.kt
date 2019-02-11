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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.jagoancoding.planyoursemester.R

class RemoveSubjectDialog : DialogFragment() {

    interface DialogClickListener {
        fun onSubjectToRemoveSelected(subjectId: String)
    }

    lateinit var listener: DialogClickListener
    lateinit var subjectNames: Array<String>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(context!!)
            .setTitle(R.string.dialog_rm_subj_title)
            .setItems(subjectNames) { _, which ->
                // Show confirmation dialog
                val subjectName = subjectNames[which]
                AlertDialog.Builder(context!!)
                    .setTitle(R.string.dialog_confirm_rm_subj_title)
                    .setMessage(
                        getString(
                            R.string.dialog_confirm_rm_subj_mes, subjectName
                        )
                    )
                    .setPositiveButton(android.R.string.yes) { dialog, _ ->
                        listener.onSubjectToRemoveSelected(subjectName)
                        dialog.dismiss()
                    }
                    .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                        dialog.cancel()
                    }
                    .show()
            }
            .create()

}