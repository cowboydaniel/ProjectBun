package com.example.babydevelopmenttracker.model

import androidx.compose.runtime.Immutable

@Immutable
data class FetalGrowthPoint(
    val week: Int,
    val lengthCm: Float,
    val weightG: Int
)

object FetalGrowthTrends {
    val weeklyGrowth: List<FetalGrowthPoint> = listOf(
        FetalGrowthPoint(week = 4, lengthCm = 0.1f, weightG = 1),
        FetalGrowthPoint(week = 6, lengthCm = 0.4f, weightG = 2),
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
}
