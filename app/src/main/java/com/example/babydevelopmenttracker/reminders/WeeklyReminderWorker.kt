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
import com.example.babydevelopmenttracker.data.FamilyRole
import com.example.babydevelopmenttracker.data.UserPreferencesKeys
import com.example.babydevelopmenttracker.data.userPreferencesDataStore
import com.example.babydevelopmenttracker.model.BabyDevelopmentRepository
import com.example.babydevelopmenttracker.model.calculateWeekFromDueDate
import com.example.babydevelopmenttracker.model.findWeek
import kotlinx.coroutines.flow.first
import java.io.IOException
import java.time.LocalDate
import java.time.ZoneId

class WeeklyReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // Reading preferences can fail transiently (for example if the DataStore file is being
        // migrated). Retrying rather than throwing keeps the reminder chain alive - an uncaught
        // failure here would end it permanently, with no further reminders until the app is
        // reopened.
        val preferences = try {
            applicationContext.userPreferencesDataStore.data.first()
        } catch (error: IOException) {
            return Result.retry()
        }

        val dueDateEpochDay = preferences[UserPreferencesKeys.DUE_DATE_EPOCH_DAY]

        val remindersEnabled = preferences[UserPreferencesKeys.REMINDER_ENABLED] ?: false
        if (!remindersEnabled) {
            // Do not re-arm: the settings toggle owns scheduling and cancelled this chain.
            return Result.success()
        }

        // Every path from here re-arms next week's run before returning, so a week that cannot
        // deliver does not silently end the chain.
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        if (!notificationManager.areNotificationsEnabled()) {
            scheduleNextRun(dueDateEpochDay)
            return Result.success()
        }

        val zoneId = ZoneId.systemDefault()
        val today = LocalDate.now(zoneId)
        val dueDate = dueDateEpochDay?.let(LocalDate::ofEpochDay)
        val weekFromDueDate = dueDate?.let { calculateWeekFromDueDate(it, today) }
        val targetWeek = weekFromDueDate ?: BabyDevelopmentRepository.weeks.first().week
        val weekInfo = BabyDevelopmentRepository.findWeek(targetWeek)
        val familyRoleValue = preferences[UserPreferencesKeys.FAMILY_ROLE]
        val familyRole = FamilyRole.fromStorageValue(familyRoleValue)
        val isPartnerSupporter = familyRole == FamilyRole.PARTNER_SUPPORTER

        val context = applicationContext
        val contentText = if (isPartnerSupporter) {
            val partnerTip = BabyDevelopmentRepository.partnerSupportForWeek(targetWeek).firstOrNull()
            partnerTip?.let { context.getString(R.string.notification_body_partner_tip, it) }
                ?: context.getString(R.string.notification_body_generic)
        } else {
            weekInfo?.babyHighlights?.firstOrNull()?.let { highlight ->
                context.getString(R.string.notification_body_with_tip, highlight)
            } ?: context.getString(R.string.notification_body_generic)
        }

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

        try {
            notificationManager.notify(WEEKLY_REMINDER_NOTIFICATION_ID, notification)
        } catch (error: SecurityException) {
            // POST_NOTIFICATIONS was revoked after the work was enqueued. Keep the chain alive so
            // reminders resume if the permission is granted again.
            scheduleNextRun(dueDateEpochDay)
            return Result.success()
        }

        scheduleNextRun(dueDateEpochDay)
        return Result.success()
    }

    private fun scheduleNextRun(dueDateEpochDay: Long?) {
        WeeklyReminderScheduler(applicationContext).scheduleNextAfterRun(dueDateEpochDay)
    }
}
