package com.example.babydevelopmenttracker.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BabyDevelopmentRepositoryTest {

    @Test
    fun coversAllWeeksWithSpecificContent() {
        val weeks = BabyDevelopmentRepository.weeks
        assertEquals("Expected 39 weeks of content", 39, weeks.size)
        assertEquals(4, weeks.first().week)
        assertEquals(42, weeks.last().week)

        (4..42).forEach { week ->
            val info = BabyDevelopmentRepository.findWeek(week)
            assertNotNull("Missing content for week $week", info)
            info!!
            assertTrue(info.babyHighlights.isNotEmpty())
            assertTrue(info.parentChanges.isNotEmpty())
            assertTrue(info.tips.isNotEmpty())

            assertNotEquals(
                "Week $week should not use fallback baby highlights",
                BabyDevelopmentRepository.DEFAULT_BABY_HIGHLIGHTS,
                info.babyHighlights
            )
            assertNotEquals(
                "Week $week should not use fallback parent changes",
                BabyDevelopmentRepository.DEFAULT_PARENT_CHANGES,
                info.parentChanges
            )
            assertNotEquals(
                "Week $week should not use fallback tips",
                BabyDevelopmentRepository.DEFAULT_TIPS,
                info.tips
            )
        }
    }

    @Test
    fun returnsNullOutsideSupportedRange() {
        assertNull(BabyDevelopmentRepository.findWeek(3))
        assertNull(BabyDevelopmentRepository.findWeek(43))
    }
}
