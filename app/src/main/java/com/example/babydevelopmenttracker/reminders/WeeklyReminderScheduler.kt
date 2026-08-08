package com.example.babydevelopmenttracker.reminders

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

/**
 * Schedules the weekly check-in notification.
 *
 * The reminder is a chain of one-shot workers rather than a [androidx.work.PeriodicWorkRequest]:
 * each run enqueues the next one via [scheduleNextAfterRun]. A periodic request only guarantees
 * one execution somewhere inside each interval, so over a seven day period it drifts badly away
 * from the intended 9am delivery. Re-aiming a fresh one-shot at the next target each time keeps
 * delivery pinned to the morning.
 */
class WeeklyReminderScheduler(private val context: Context) {

    /**
     * Arms the reminder if it is not already armed, leaving any pending run untouched.
     *
     * This is the safe call for app start-up and permission grants. Using
     * [ExistingWorkPolicy.REPLACE] here would reset the delay on every launch and push the next
     * reminder out, so a user who opens the app often would rarely receive one.
     */
    fun ensureScheduled(dueDateEpochDay: Long?) {
        enqueue(dueDateEpochDay, ExistingWorkPolicy.KEEP)
    }

    /**
     * Re-arms the reminder against a new due date, discarding any pending run. Only for use when
     * the due date actually changed, since it moves the next delivery.
     */
    fun rescheduleNow(dueDateEpochDay: Long?) {
        enqueue(dueDateEpochDay, ExistingWorkPolicy.REPLACE)
    }

    /** Enqueues the following week's run. Called by the worker once it has delivered. */
    fun scheduleNextAfterRun(dueDateEpochDay: Long?) {
        enqueue(dueDateEpochDay, ExistingWorkPolicy.REPLACE)
    }

    fun cancelWeeklyReminder() {
        WorkManager.getInstance(context).cancelUniqueWork(WEEKLY_REMINDER_WORK_NAME)
    }

    private fun enqueue(dueDateEpochDay: Long?, policy: ExistingWorkPolicy) {
        val request = OneTimeWorkRequestBuilder<WeeklyReminderWorker>()
            .setInitialDelay(calculateInitialDelay(dueDateEpochDay))
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            WEEKLY_REMINDER_WORK_NAME,
            policy,
            request
        )
    }

    private fun calculateInitialDelay(dueDateEpochDay: Long?): Duration {
        val zoneId = ZoneId.systemDefault()
        val now = ZonedDateTime.now(zoneId)
        val today = now.toLocalDate()

        val targetDate = dueDateEpochDay?.let { epochDay ->
            val dueDate = LocalDate.ofEpochDay(epochDay)
            val daysUntilDue = ChronoUnit.DAYS.between(today, dueDate)
            val remainder = ((daysUntilDue % 7) + 7) % 7
            today.plusDays(remainder)
        } ?: today.plusDays(1)

        var nextTrigger = targetDate.atTime(REMINDER_HOUR, 0).atZone(zoneId)
        if (!nextTrigger.isAfter(now)) {
            nextTrigger = nextTrigger.plusWeeks(1)
        }

        return Duration.between(now, nextTrigger)
    }
}
