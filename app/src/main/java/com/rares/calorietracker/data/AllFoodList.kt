package com.rares.calorietracker.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf

class AllFoodList(var id: String = "") {
    val allMeals = mutableStateListOf<Meal>()
    val allSnacks = mutableStateListOf<Meal>()
    val allIngredients = mutableStateListOf<Meal>()

    fun addMeal(meal: Meal) {
        allMeals.add(meal)
    }

    fun delMeal(meal: Meal) {
        allMeals.remove(meal)
    }

    fun addSnack(meal: Meal) {
        allSnacks.add(meal)
    }

    fun delSnack(meal: Meal) {
        allSnacks.remove(meal)
    }

    fun addIngredient(meal: Meal) {
        allIngredients.add(meal)
    }

    fun delIngredient(meal: Meal) {
        allIngredients.remove(meal)
    }
}