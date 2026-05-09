package com.rares.calorietracker.model

data class FSTypeOfTraining (
    val id: String = "",
    val allTraining: List<String> = listOf(),
    val targetCalories: Int = 0
) {
}