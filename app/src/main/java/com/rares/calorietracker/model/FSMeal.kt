package com.rares.calorietracker.model

import com.rares.calorietracker.data.TypeOfMeal

data class FSMeal (
    val id: String = "",
    val typeOfMeal: String = "",
    val name: String = "",
    val calories: Double = 0.0,
    val portionSize: Double = 0.0,
    val price: Double = 0.0,
    val fat: Double = 0.0,
    val carbs: Double = 0.0,
    val protein: Double = 0.0,
    val qualProtein: Double = 0.0,
    val salt: Double = 0.0,
    val ingredients: List<FSFinalMeal> = listOf(),
) {
}