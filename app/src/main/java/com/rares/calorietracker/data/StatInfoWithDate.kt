package com.rares.calorietracker.data

import java.time.LocalDate

data class StatInfoWithDate(var calories: Double, var weight: Double, var usedWeight: Double, val date: LocalDate, var sevenDayAvg: Double) {
}