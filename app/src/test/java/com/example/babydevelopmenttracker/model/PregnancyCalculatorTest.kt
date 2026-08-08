package com.example.babydevelopmenttracker.model

import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.Test

class PregnancyCalculatorTest {

    private val dueDate = LocalDate.of(2026, 6, 1)

    @Test
    fun dueDateItselfIsWeekForty() {
        assertEquals(40, calculateWeekFromDueDate(dueDate, dueDate))
    }

    @Test
    fun daysPastTheDueDateStayInWeekForty() {
        // Gestational age counts completed weeks, so 40w1d through 40w6d are all week 40.
        for (daysOverdue in 1..6) {
            assertEquals(
                40,
                calculateWeekFromDueDate(dueDate, dueDate.plusDays(daysOverdue.toLong())),
                "expected week 40 at 40w${daysOverdue}d"
            )
        }
    }

    @Test
    fun aFullWeekPastTheDueDateIsWeekFortyOne() {
        assertEquals(41, calculateWeekFromDueDate(dueDate, dueDate.plusDays(7)))
        assertEquals(41, calculateWeekFromDueDate(dueDate, dueDate.plusDays(13)))
        assertEquals(42, calculateWeekFromDueDate(dueDate, dueDate.plusDays(14)))
    }

    @Test
    fun weeksBeforeTheDueDateCountDown() {
        assertEquals(39, calculateWeekFromDueDate(dueDate, dueDate.minusDays(7)))
        assertEquals(30, calculateWeekFromDueDate(dueDate, dueDate.minusDays(70)))
    }

    @Test
    fun beforeWeekFourThereIsNothingToShow() {
        assertNull(calculateWeekFromDueDate(dueDate, dueDate.minusDays(260)))
    }

    @Test
    fun weeksBeyondTheDataSetClampToTheLast() {
        assertEquals(42, calculateWeekFromDueDate(dueDate, dueDate.plusDays(60)))
    }
}
