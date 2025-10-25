package com.example.babydevelopmenttracker.reminders

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

class WeeklyReminderScheduler(private val context: Context) {

    fun scheduleWeeklyReminder(dueDateEpochDay: Long?) {
        val workManager = WorkManager.getInstance(context)
        val request = PeriodicWorkRequestBuilder<WeeklyReminderWorker>(7, TimeUnit.DAYS)
            .setInitialDelay(calculateInitialDelay(dueDateEpochDay))
            .build()

        workManager.enqueueUniquePeriodicWork(
            WEEKLY_REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun cancelWeeklyReminder() {
        WorkManager.getInstance(context).cancelUniqueWork(WEEKLY_REMINDER_WORK_NAME)
    }

    private fun calculateInitialDelay(dueDateEpochDay: Long?): Duration {
        val zoneId = ZoneId.systemDefault()
        val now = ZonedDateTime.now(zoneId)
        val today = now.toLocalDate()

        val targetDate = dueDateEpochDay?.let { epochDay ->
            val dueDate = LocalDate.ofEpochDay(epochDay)
            val daysUntilDue = ChronoUnit.DAYS.between(today, dueDate)
            val remainder = ((daysUntilDue % 7) + 7) % 7
            today.plusDays(remainder.toLong())
        } ?: today.plusDays(1)

        var nextTrigger = targetDate.atTime(9, 0).atZone(zoneId)
        if (!nextTrigger.isAfter(now)) {
            nextTrigger = nextTrigger.plusWeeks(1)
        }

        return Duration.between(now, nextTrigger)
    }
}
