package com.example.babydevelopmenttracker.model

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.ceil

private const val TOTAL_WEEKS = 40
private const val MIN_WEEK = 4
private const val MAX_WEEK = 42
private const val DAYS_IN_WEEK = 7

/**
 * Calculates the current pregnancy week based on a provided due date.
 *
 * The calculation assumes a full-term pregnancy of 40 weeks and returns null
 * when the derived week falls before week 4 to avoid surfacing content that is
 * not yet available in the data set. Weeks beyond 42 are clamped to the final
 * available week.
 */
fun calculateWeekFromDueDate(
    dueDate: LocalDate,
    today: LocalDate = LocalDate.now()
): Int? {
    val daysUntilDue = ChronoUnit.DAYS.between(today, dueDate).toInt()

    val computedWeek = if (daysUntilDue >= 0) {
        val weeksRemaining = ceil(daysUntilDue.toDouble() / DAYS_IN_WEEK)
        TOTAL_WEEKS - weeksRemaining.toInt()
    } else {
        val overdueWeeks = ceil(abs(daysUntilDue).toDouble() / DAYS_IN_WEEK)
        TOTAL_WEEKS + overdueWeeks.toInt()
    }

    if (computedWeek < MIN_WEEK) {
        return null
    }

    return computedWeek.coerceIn(MIN_WEEK, MAX_WEEK)
}
