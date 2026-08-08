package com.example.babydevelopmenttracker.model

import androidx.compose.runtime.Immutable

@Immutable
data class FetalGrowthPoint(
    val week: Int,
    val lengthCm: Float,
    val weightG: Int
)

/**
 * Reference fetal growth measurements, sampled every second week.
 *
 * Lengths are crown to rump throughout, which is how they are measured on ultrasound for most of
 * pregnancy. A crown to rump length is markedly shorter than the head to heel length a newborn is
 * measured by at birth - roughly 36cm against roughly 51cm at term - so any label shown alongside
 * these values needs to say which measurement it is.
 */
object FetalGrowthTrends {
    val weeklyGrowth: List<FetalGrowthPoint> = listOf(
        FetalGrowthPoint(week = 4, lengthCm = 0.1f, weightG = 1),
        FetalGrowthPoint(week = 6, lengthCm = 0.4f, weightG = 1),
        FetalGrowthPoint(week = 8, lengthCm = 1.6f, weightG = 1),
        FetalGrowthPoint(week = 10, lengthCm = 3.1f, weightG = 4),
        FetalGrowthPoint(week = 12, lengthCm = 5.4f, weightG = 14),
        FetalGrowthPoint(week = 14, lengthCm = 8.6f, weightG = 43),
        FetalGrowthPoint(week = 16, lengthCm = 11.6f, weightG = 100),
        FetalGrowthPoint(week = 18, lengthCm = 14.2f, weightG = 190),
        FetalGrowthPoint(week = 20, lengthCm = 16.5f, weightG = 320),
        FetalGrowthPoint(week = 22, lengthCm = 19.0f, weightG = 430),
        FetalGrowthPoint(week = 24, lengthCm = 21.4f, weightG = 600),
        FetalGrowthPoint(week = 26, lengthCm = 23.0f, weightG = 760),
        FetalGrowthPoint(week = 28, lengthCm = 25.4f, weightG = 1005),
        FetalGrowthPoint(week = 30, lengthCm = 27.4f, weightG = 1319),
        FetalGrowthPoint(week = 32, lengthCm = 28.9f, weightG = 1702),
        FetalGrowthPoint(week = 34, lengthCm = 30.0f, weightG = 2146),
        FetalGrowthPoint(week = 36, lengthCm = 33.0f, weightG = 2622),
        FetalGrowthPoint(week = 38, lengthCm = 34.6f, weightG = 3083),
        FetalGrowthPoint(week = 40, lengthCm = 35.5f, weightG = 3462)
    )

    fun findClosestWeek(week: Int): FetalGrowthPoint? {
        if (weeklyGrowth.isEmpty()) return null
        return weeklyGrowth.minByOrNull { kotlin.math.abs(it.week - week) }
    }

    /**
     * Estimates measurements for [week], interpolating between the two nearest samples.
     *
     * Samples only exist every second week, so [findClosestWeek] snaps an odd week onto a
     * neighbouring even one and reports that neighbour's week number. Interpolating instead means
     * week 21 reads as week 21, with a figure between the week 20 and week 22 samples, rather than
     * silently showing week 20's.
     *
     * Weeks outside the sampled range clamp to the nearest end.
     */
    fun estimateForWeek(week: Int): FetalGrowthPoint? {
        if (weeklyGrowth.isEmpty()) return null

        val first = weeklyGrowth.first()
        val last = weeklyGrowth.last()
        if (week <= first.week) return first.copy(week = week.coerceAtLeast(first.week))
        if (week >= last.week) return last.copy(week = week.coerceAtMost(last.week))

        weeklyGrowth.firstOrNull { it.week == week }?.let { return it }

        val lower = weeklyGrowth.last { it.week < week }
        val upper = weeklyGrowth.first { it.week > week }
        val span = (upper.week - lower.week).toFloat()
        val fraction = (week - lower.week) / span

        return FetalGrowthPoint(
            week = week,
            lengthCm = lower.lengthCm + (upper.lengthCm - lower.lengthCm) * fraction,
            weightG = (lower.weightG + (upper.weightG - lower.weightG) * fraction).toInt()
        )
    }
}
