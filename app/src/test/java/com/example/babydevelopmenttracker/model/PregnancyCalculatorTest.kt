package com.example.babydevelopmenttracker.model

import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PregnancyCalculatorTest {

    private val today = LocalDate.of(2024, 6, 15)

    @Test
    fun `returns null when calculated week is earlier than supported range`() {
        val dueDate = today.plusWeeks(36)

        val result = calculateWeekFromDueDate(dueDate, today)

        assertNull(result)
    }

    @Test
    fun `returns week 39 when due date is one week away`() {
        val dueDate = today.plusWeeks(1)

        val result = calculateWeekFromDueDate(dueDate, today)

        assertEquals(39, result)
    }

    @Test
    fun `returns week 40 on the due date`() {
        val dueDate = today

        val result = calculateWeekFromDueDate(dueDate, today)

        assertEquals(40, result)
    }

    @Test
    fun `caps at week 42 when pregnancy is overdue`() {
        val dueDate = today.minusWeeks(3)

        val result = calculateWeekFromDueDate(dueDate, today)

        assertEquals(42, result)
    }
}
