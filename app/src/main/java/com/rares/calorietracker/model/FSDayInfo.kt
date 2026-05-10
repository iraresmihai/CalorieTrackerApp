package com.rares.calorietracker.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

data class FSDayInfo (
    val id: String = "",
    val date: String = "",
    val trainingName: String = "",
    val targetCalories: Double = 0.0,
    val workoutRating: Int = 0,
    val weight: Double = 0.0,
    val stepCount: Int = 0,
    val trainingTime: Int = 0,
    val currentFood: List<String> = listOf()
) {
}