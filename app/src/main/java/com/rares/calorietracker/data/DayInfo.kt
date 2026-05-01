package com.rares.calorietracker.data

import java.time.LocalDate

data class DayInfo(val date: LocalDate, var trainingName: String, val targetCalories: Double) {
    var currentCalories: Double = 0.0
    var protein: Double = 0.0
    var qualProtein: Double = 0.0
    var carbs: Double = 0.0
    var fat: Double = 0.0
    var cost: Double = 0.0
    var salt: Double = 0.0
    var foodWeight: Double = 0.0

    var workoutRating: Int = 0
    var weight: Double = 0.0
    var stepCount: Int = 0
    var trainingTime: Int = 0

    val currentFood = mutableListOf<FinalMeal>() //adds a new meal. if the recipe gets changed afterwards, it won't get updated

    fun addMeal(finalMealAux: FinalMeal) {
        val finalMeal = finalMealAux.copy()
        currentFood.add(finalMeal)
        currentCalories += finalMeal.getTotalCal()
        protein += finalMeal.getTotalProtein()
        qualProtein += finalMeal.getTotalQualProtein()
        carbs += finalMeal.getTotalCarbs()
        fat += finalMeal.getTotalFat()
        cost += finalMeal.getTotalPrice()
        foodWeight += finalMeal.weightEaten
        salt += finalMeal.getTotalSalt()
    }

    fun updMealStart(oldMeal: FinalMeal, newMeal: FinalMeal) {
        if ( !currentFood.contains(oldMeal) ) {
            return
        }
        currentCalories -= oldMeal.getTotalCal()
        protein -= oldMeal.getTotalProtein()
        qualProtein -= oldMeal.getTotalQualProtein()
        carbs -= oldMeal.getTotalCarbs()
        fat -= oldMeal.getTotalFat()
        cost -= oldMeal.getTotalPrice()
        foodWeight -= oldMeal.weightEaten
        salt -= oldMeal.getTotalSalt()

        currentCalories += newMeal.getTotalCal()
        protein += newMeal.getTotalProtein()
        qualProtein += newMeal.getTotalQualProtein()
        carbs += newMeal.getTotalCarbs()
        fat += newMeal.getTotalFat()
        cost += newMeal.getTotalPrice()
        foodWeight += newMeal.weightEaten
        salt += newMeal.getTotalSalt()

        var copyOfNew = newMeal.copy()
        currentFood.replaceAll { if (it == oldMeal) copyOfNew else it }
    }
}