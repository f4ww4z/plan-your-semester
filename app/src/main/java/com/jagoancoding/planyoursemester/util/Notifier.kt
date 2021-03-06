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
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.jagoancoding.planyoursemester.AppRepository
import com.jagoancoding.planyoursemester.OverviewActivity
import com.jagoancoding.planyoursemester.R
import com.jagoancoding.planyoursemester.model.PlanItem
import com.jagoancoding.planyoursemester.ui.overview.OverviewFragment
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZonedDateTime
import java.util.UUID
import java.util.concurrent.TimeUnit

object Notifier {

    private const val TAG = "Notifier"

    private const val NORMAL_CHANNEL_ID = "Default High Priority Channel 101"

    private const val NOTIF_PLAN_ID = "NOTIFICATION_PLAN_ID"
    private const val NOTIF_PLAN_TYPE = "NOTIFICATION_PLAN_TYPE"
    private const val NOTIF_TITLE = "NOTIFICATION_TITLE"
    private const val NOTIF_CONTENT_TEXT = "NOTIFICATION_TEXT"
    private const val NOTIF_DATETIME = "NOTIFICATION_DATE_TIME"

    private const val _1_DAY_BEFORE = "1daybefore"
    private const val _30_MIN_BEFORE = "30minutesbefore"
    private const val _NOW = "nowthen"

    const val NOTIFICATIONS_MAP_FILE = "notifications_map.txt"
    const val NOTIFICATION_WORKERS_MAP_FILE = "notification_workers.txt"

    const val BASE_NOTIFICATION_ID_COUNT: Int = 1640

    /**
     * Create the NotificationChannel, but only on API 26+ because
     * the NotificationChannel class is new and not in the support library
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun createDefaultNotificationChannel(context: Context) {
        with(context) {
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

    //TODO: Update notification worker when plan item is updated
    //TODO: Remove notification worker if: notification finished, plan item is deleted

    fun showNotificationAt(context: Context, planItem: PlanItem) {
        when (planItem.itemType) {
            PlanItem.TYPE_EXAM -> {
                notifyUserOneDayBefore(context, planItem)
                notify30MinsBefore(context, planItem)
                notifyAtPlanDate(context, planItem)
            }
            PlanItem.TYPE_HOMEWORK -> {
                notifyUserOneDayBefore(context, planItem)
                notify30MinsBefore(context, planItem)
                notifyAtPlanDate(context, planItem)
            }
            PlanItem.TYPE_EVENT -> {
                notifyUserOneDayBefore(context, planItem)
                notify30MinsBefore(context, planItem)
                notifyAtPlanDate(context, planItem)
            }
            else -> {
                notifyAtPlanDate(context, planItem)
            }
        }
    }

    private fun notifyUserOneDayBefore(context: Context, planItem: PlanItem) {

        val epoch: Long = DateUtil.getEpoch(planItem)

        val date: LocalDateTime = DateUtil.getDateTime(epoch)
        val oneDayBefore: LocalDateTime = date.minusDays(1)
        val now: LocalDateTime =
            ZonedDateTime.now(AppRepository.zoneId).toLocalDateTime()

        val content: String = getContentText(context, oneDayBefore, planItem)

        if (oneDayBefore.isAfter(now)) {
            notifyUserAt(
                context,
                planItem.id,
                planItem.itemType,
                oneDayBefore,
                planItem.name,
                content
            )
        }
    }

    private fun notify30MinsBefore(context: Context, planItem: PlanItem) {

        val epoch: Long = DateUtil.getEpoch(planItem)

        val date: LocalDateTime = DateUtil.getDateTime(epoch)
        val _30MinsBefore: LocalDateTime = date.minusMinutes(30)
        val now: LocalDateTime =
            ZonedDateTime.now(AppRepository.zoneId).toLocalDateTime()

        val content: String = getContentText(context, _30MinsBefore, planItem)

        if (_30MinsBefore.isAfter(now)) {
            notifyUserAt(
                context,
                planItem.id,
                planItem.itemType,
                _30MinsBefore,
                planItem.name,
                content
            )
        }
    }

    private fun notifyAtPlanDate(context: Context, planItem: PlanItem) {
        val epoch = DateUtil.getEpoch(planItem)
        val date: LocalDateTime = DateUtil.getDateTime(epoch)
        val content = getContentText(context, date, planItem)

        notifyUserAt(
            context,
            planItem.id,
            planItem.itemType,
            date,
            planItem.name,
            content
        )
    }

    private fun getContentText(
        context: Context,
        dt: LocalDateTime,
        planItem: PlanItem
    ): String {

        val type = determineDateTimeType(dt, DateUtil.getEpoch(planItem))

        with(planItem) {
            return when (type) {
                _1_DAY_BEFORE -> when (itemType) {
                    PlanItem.TYPE_EXAM -> {
                        context.getString(
                            R.string.notif_1_day_before,
                            context.getString(R.string.exam_label)
                        )
                    }
                    PlanItem.TYPE_HOMEWORK -> {
                        context.getString(
                            R.string.notif_due_in_1_day,
                            context.getString(R.string.homework_label)
                        )
                    }
                    PlanItem.TYPE_EVENT -> {
                        context.getString(
                            R.string.notif_1_day_before,
                            context.getString(R.string.event_label)
                        )
                    }
                    else -> context.getString(R.string.starts_tomorrow)
                }
                _30_MIN_BEFORE -> when (itemType) {
                    PlanItem.TYPE_EXAM -> {
                        context.getString(
                            R.string.notif_30_mins_before,
                            context.getString(R.string.exam_label)
                        )
                    }
                    PlanItem.TYPE_HOMEWORK -> {
                        context.getString(
                            R.string.notif_due_in_30_min,
                            context.getString(R.string.homework_label)
                        )
                    }
                    PlanItem.TYPE_EVENT -> {
                        context.getString(
                            R.string.notif_30_mins_before,
                            context.getString(R.string.event_label)
                        )
                    }
                    else -> context.getString(R.string.notif_30_mins_before)
                }
                else -> when (itemType) {
                    PlanItem.TYPE_EXAM -> {
                        DateUtil.getTimeStartEnd(
                            startDate!!,
                            endDate!!,
                            context.resources
                        )
                    }
                    PlanItem.TYPE_HOMEWORK -> {
                        DateUtil.getHomeworkDueTime(date!!, context.resources)
                    }
                    PlanItem.TYPE_EVENT -> {
                        DateUtil.getTimeStartEnd(
                            startDate!!,
                            endDate!!,
                            context.resources
                        )
                    }
                    else -> DateUtil.getFormattedTime(date!!)
                }
            }
        }

    }

    private fun determineDateTimeType(
        notifDateTime: LocalDateTime, planItemEpoch: Long
    ): String {
        val planItemDateTime = DateUtil.getDateTime(planItemEpoch)

        return when (notifDateTime) {
            planItemDateTime.minusDays(1) -> _1_DAY_BEFORE
            planItemDateTime.minusMinutes(30) -> _30_MIN_BEFORE
            else -> _NOW
        }
    }

    /**
     * Display a notification in the user's device at a specified time
     * @param dt date and time to display the notification
     * @param title the notification's title
     * @param text the notification's description (appears below title)
     */
    private fun notifyUserAt(
        context: Context,
        planItemId: Long,
        planItemType: Int,
        dt: LocalDateTime,
        title: String,
        text: String
    ) {
        val epoch = DateUtil.toEpochMilli(dt)
        val now = DateUtil.toEpochMilli(
            ZonedDateTime.now(AppRepository.zoneId).toLocalDateTime()
        )
        val delay = epoch - now + 2

        val data: Data = Data.Builder()
            .putLong(NOTIF_PLAN_ID, planItemId)
            .putInt(NOTIF_PLAN_TYPE, planItemType)
            .putString(NOTIF_TITLE, title)
            .putString(NOTIF_CONTENT_TEXT, text)
            .putLong(NOTIF_DATETIME, epoch)
            .build()

        val constraints: Constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresCharging(false)
            .build()

        val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(data)
            .setConstraints(constraints)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        //TODO: Get the work's Id and update it when the user updates the plan or adds a new plan
        //TODO: And remove the work if user removes plan

        WorkManager.getInstance().enqueue(notificationWork)

        val planWithType = DataUtil.PlanWithType(planItemId, planItemType)
        cancelNotifAndWorkWhenFinished(context, planWithType)
    }

    //TODO: Call this when item is updated
    fun cancelNotifcation(
        context: Context, planItemId: Long, planItemType: Int
    ) {
        val planWithType = DataUtil.PlanWithType(planItemId, planItemType)
        cancelNotifAndWorkWhenFinished(context, planWithType)
    }

    private fun cancelNotifAndWorkWhenFinished(
        context: Context, planWithType: DataUtil.PlanWithType
    ) {
        val entry = DataUtil.findNotificationWorkerEntry(context, planWithType)
        if (entry != null) {
            val notificationWorkId = UUID.fromString(entry.key)

            WorkManager.getInstance()
                .getWorkInfoByIdLiveData(notificationWorkId)
                .observe(context as LifecycleOwner,
                    Observer { workInfo ->
                        // Check if notification is finished showing
                        if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                            WorkManager.getInstance()
                                .cancelWorkById(notificationWorkId)

                            DataUtil.removeNotificationAndWorkFromData(
                                context, planWithType
                            )
                        }
                    })
        }
    }

    class NotificationWorker(val context: Context, params: WorkerParameters) :
        Worker(context, params) {

        override fun doWork(): Result {
            // Get data
            val planItemId = inputData.getLong(NOTIF_PLAN_ID, 0)
            val planItemType = inputData.getInt(NOTIF_PLAN_TYPE, 0)
            val title = inputData.getString(NOTIF_TITLE)
            val contentText = inputData.getString(NOTIF_CONTENT_TEXT)
            val epoch = inputData.getLong(NOTIF_DATETIME, 0)

            // Store the worker id
            DataUtil.setNotificationWorkOfId(
                "$id",
                planItemId,
                planItemType,
                context
            )
            Log.i(
                TAG,
                "Notification workers: ${DataUtil.getNotificationWorksMap(
                    context
                )}"
            )

            // Build the notification
            val mNotification =
                NotificationCompat.Builder(context, NORMAL_CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(contentText)
                    .setSmallIcon(R.drawable.cal_notification_icon)
                    .setLargeIcon(
                        BitmapFactory.decodeResource(
                            context.resources, R.drawable.cal_notification_icon
                        )
                    )
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(overviewScreenIntent(context, epoch))
                    .setAutoCancel(true)
                    .build()!!

            // Show the notification
            val id = DataUtil.newNotificationId(context)
            NotificationManagerCompat.from(context)
                .notify(id, mNotification)

            // Store the notification id for later use
            DataUtil.setNotificationOfId(id, planItemId, planItemType, context)

            Log.i(
                TAG, "Notifications: ${DataUtil.getNotificationsMap(context)}"
            )
            Log.i(TAG, "Showing notification: '$title'")
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