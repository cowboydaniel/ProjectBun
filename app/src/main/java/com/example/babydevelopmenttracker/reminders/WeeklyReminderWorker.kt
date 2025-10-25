package com.example.babydevelopmenttracker.reminders

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.babydevelopmenttracker.MainActivity
import com.example.babydevelopmenttracker.R
import com.example.babydevelopmenttracker.data.UserPreferencesKeys
import com.example.babydevelopmenttracker.data.userPreferencesDataStore
import com.example.babydevelopmenttracker.model.BabyDevelopmentRepository
import com.example.babydevelopmenttracker.model.calculateWeekFromDueDate
import com.example.babydevelopmenttracker.model.findWeek
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.ZoneId

class WeeklyReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val preferences = applicationContext.userPreferencesDataStore.data.first()
        val remindersEnabled = preferences[UserPreferencesKeys.REMINDER_ENABLED] ?: false
        if (!remindersEnabled) {
            return Result.success()
        }

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        if (!notificationManager.areNotificationsEnabled()) {
            return Result.success()
        }

        val dueDateEpochDay = preferences[UserPreferencesKeys.DUE_DATE_EPOCH_DAY]
        val zoneId = ZoneId.systemDefault()
        val today = LocalDate.now(zoneId)
        val dueDate = dueDateEpochDay?.let(LocalDate::ofEpochDay)
        val weekFromDueDate = dueDate?.let { calculateWeekFromDueDate(it, today) }
        val targetWeek = weekFromDueDate ?: BabyDevelopmentRepository.weeks.first().week
        val weekInfo = BabyDevelopmentRepository.findWeek(targetWeek)

        val context = applicationContext
        val contentText = weekInfo?.babyHighlights?.firstOrNull()?.let { highlight ->
            context.getString(R.string.notification_body_with_tip, highlight)
        } ?: context.getString(R.string.notification_body_generic)

        val title = context.getString(R.string.notification_title, targetWeek)

        val contentIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(Intent(context, MainActivity::class.java))
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val notification = NotificationCompat.Builder(context, WEEKLY_REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_reminder)
            .setContentTitle(title)
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(WEEKLY_REMINDER_NOTIFICATION_ID, notification)

        return Result.success()
    }
}
