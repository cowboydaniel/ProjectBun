package com.example.babydevelopmenttracker.model

import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Test

class FetalGrowthTrendsTest {

    @Test
    fun weightNeverFalls() {
        // A fetus does not shrink; week 6 once reported a heavier weight than week 8.
        FetalGrowthTrends.weeklyGrowth.zipWithNext { earlier, later ->
            assertTrue(
                later.weightG >= earlier.weightG,
                "week ${later.week} (${later.weightG}g) is lighter than " +
                    "week ${earlier.week} (${earlier.weightG}g)"
            )
        }
    }

    @Test
    fun lengthNeverFalls() {
        FetalGrowthTrends.weeklyGrowth.zipWithNext { earlier, later ->
            assertTrue(
                later.lengthCm >= earlier.lengthCm,
                "week ${later.week} is shorter than week ${earlier.week}"
            )
        }
    }

    @Test
    fun anEstimateReportsTheWeekItWasAskedFor() {
        // Samples exist every second week. Asking for an odd week used to report its neighbour's
        // week number alongside that neighbour's measurements.
        assertEquals(21, FetalGrowthTrends.estimateForWeek(21)?.week)
        assertEquals(27, FetalGrowthTrends.estimateForWeek(27)?.week)
    }

    @Test
    fun anEstimateFallsBetweenTheSurroundingSamples() {
        val lower = FetalGrowthTrends.weeklyGrowth.first { it.week == 20 }
        val upper = FetalGrowthTrends.weeklyGrowth.first { it.week == 22 }
        val estimate = requireNotNull(FetalGrowthTrends.estimateForWeek(21))

        assertTrue(estimate.lengthCm > lower.lengthCm && estimate.lengthCm < upper.lengthCm)
        assertTrue(estimate.weightG > lower.weightG && estimate.weightG < upper.weightG)
    }

    @Test
    fun sampledWeeksAreReturnedExactly() {
        val sample = FetalGrowthTrends.weeklyGrowth.first { it.week == 24 }
        assertEquals(sample, FetalGrowthTrends.estimateForWeek(24))
    }

    @Test
    fun weeksOutsideTheSampledRangeClampToTheNearestEnd() {
        val first = FetalGrowthTrends.weeklyGrowth.first()
        val last = FetalGrowthTrends.weeklyGrowth.last()

        assertEquals(first.weightG, FetalGrowthTrends.estimateForWeek(2)?.weightG)
        assertEquals(last.weightG, FetalGrowthTrends.estimateForWeek(42)?.weightG)
    }
}
