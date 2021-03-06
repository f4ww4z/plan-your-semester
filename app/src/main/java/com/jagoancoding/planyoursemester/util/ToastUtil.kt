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
import android.widget.Toast

object ToastUtil {

    private var mToast: Toast? = null

    fun Context.showShortToast(message: String) {
        if (mToast != null) {
            mToast!!.cancel()
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        mToast?.show()
    }

    fun Context.showShortToast(
        messageId: Int,
        vararg strings: String = arrayOf()
    ) {
        this.showShortToast(getString(messageId, strings))
    }

    fun Context.showLongToast(message: String) {
        if (mToast != null) {
            mToast!!.cancel()
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_LONG)
        mToast?.show()
    }

    fun Context.showLongToast(
        messageId: Int,
        vararg strings: String = arrayOf()
    ) {
        this.showLongToast(getString(messageId, strings))
    }
}