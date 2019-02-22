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

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.jagoancoding.planyoursemester.OverviewActivity
import com.jagoancoding.planyoursemester.R
import com.jagoancoding.planyoursemester.model.PlanItem
import org.threeten.bp.LocalDateTime

object Notifier {

    private const val NORMAL_CHANNEL_ID = "Default High Priority Channel 101"

    fun notifyUserOneDayBefore(context: Context, planItem: PlanItem) {

        val epoch: Long
        val content: String

        when (planItem.itemType) {
            PlanItem.TYPE_EXAM -> {
                epoch = planItem.startDate!!
                content = context.getString(
                    R.string.notif_1_day_before,
                    context.getString(R.string.exam_label)
                )
            }
            PlanItem.TYPE_HOMEWORK -> {
                epoch = planItem.date!!
                content = context.getString(
                    R.string.notif_due_in_1_day,
                    context.getString(R.string.homework_label)
                )
            }
            PlanItem.TYPE_EVENT -> {
                epoch = planItem.startDate!!
                content = context.getString(
                    R.string.notif_1_day_before,
                    context.getString(R.string.event_label)
                )
            }
            else -> {
                epoch = planItem.date!!
                content = context.getString(
                    R.string.notif_1_day_before,
                    context.getString(R.string.reminder_label)
                )
            }
        }

        val date: LocalDateTime = DateUtil.getDateTime(epoch)
        val oneDayBefore: LocalDateTime = date.minusDays(1)

        notifyUserAt(context, oneDayBefore, planItem.name, content)
    }

    private fun notifyUserAt(
        context: Context, dt: LocalDateTime, title: String, content: String
    ) {
        //TODO: Take user to Overview fragment and scroll to its date
        val intent = Intent(context, OverviewActivity::class.java).apply {
            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, 0)

        var builder =
            NotificationCompat.Builder(context, NORMAL_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(null)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)!!
    }

    /**
     * Create the NotificationChannel, but only on API 26+ because
     * the NotificationChannel class is new and not in the support library
     */
    fun createDefaultNotificationChannel(context: Context) {
        with(context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = getString(R.string.channel_name)
                val descriptionText = getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel =
                    NotificationChannel(
                        NORMAL_CHANNEL_ID, name, importance
                    ).apply {
                        description = descriptionText
                    }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

}