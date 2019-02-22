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
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.jagoancoding.planyoursemester.OverviewActivity
import com.jagoancoding.planyoursemester.R
import com.jagoancoding.planyoursemester.model.PlanItem
import com.jagoancoding.planyoursemester.ui.overview.OverviewFragment
import org.threeten.bp.LocalDateTime
import java.util.concurrent.TimeUnit

object Notifier {

    private const val NORMAL_CHANNEL_ID = "Default High Priority Channel 101"

    private const val NOTIF_TITLE = "NOTIFICATION_TITLE"
    private const val NOTIF_CONTENT_TEXT = "NOTIFICATION_TEXT"
    private const val NOTIF_DATETIME = "NOTIFICATION_DATE_TIME"

    private var notificationWorks = hashMapOf<String, Long>()
    private var notifications = hashMapOf<Int, Long>()
    private var newNotificationId: Int = 1640
        get() = field++

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

        notifyUserAt(oneDayBefore, planItem.name, content)
    }

    /**
     * Display a notification in the user's device at a specified time
     * @param dt date and time to display the notification
     * @param title the notification's title
     * @param text the notification's description (appears below title)
     */
    fun notifyUserAt(dt: LocalDateTime, title: String, text: String) {
        val epoch = DateUtil.toEpochMili(dt.toLocalDate())
        val delay = epoch - System.currentTimeMillis()

        val data: Data = Data.Builder()
            .putString(NOTIF_TITLE, title)
            .putString(NOTIF_CONTENT_TEXT, text)
            .putLong(NOTIF_DATETIME, epoch)
            .build()

        val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(data)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        //TODO: Get the work's Id and update it when the user updates the plan or adds a new plan
        //TODO: And remove the work if user removes plan
        val workId = "${notificationWork.id}"
        notificationWorks[workId] = epoch

        WorkManager.getInstance().enqueue(notificationWork)
    }

    class NotificationWorker(val context: Context, params: WorkerParameters) :
        Worker(context, params) {

        override fun doWork(): Result {

            // Get data
            val title = inputData.getString(NOTIF_TITLE)
            val contentText = inputData.getString(NOTIF_CONTENT_TEXT)
            val epoch = inputData.getLong(NOTIF_DATETIME, 0L)

            // Build the notification
            val mNotification =
                NotificationCompat.Builder(context, NORMAL_CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(contentText)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(overviewScreenIntent(context, epoch))
                    .setAutoCancel(true)
                    .build()!!

            val id = newNotificationId
            NotificationManagerCompat.from(context)
                .notify(id, mNotification)

            // Store the notification id for later use
            notifications[id] = epoch

            return Result.success()
        }

        private fun overviewScreenIntent(
            context: Context, epoch: Long
        ): PendingIntent {

            val bundle = Bundle().apply {
                putLong(OverviewFragment.KEY_SCROLL_TO_DATE, epoch)
            }
            val intent = Intent(context, OverviewActivity::class.java).apply {
                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtras(bundle)
            }

            return PendingIntent.getActivity(context, 0, intent, 0)
        }
    }
}