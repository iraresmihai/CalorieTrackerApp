package com.rares.calorietracker.data

import java.time.LocalDate

data class DayInfo(val date: LocalDate, var trainingName: String) {
    private var currentCalories: Double = 0.0
    private var protein: Double = 0.0
    private var qualProtein: Double = 0.0
    private var carbs: Double = 0.0
    private var fat: Double = 0.0
    private var cost: Double = 0.0
    private var foodWeight: Double = 0.0

    private var workoutRationg: Int = 0
    private var weight: Int = 0
    private var stepCount: Int = 0
    private var trainingTime: Int = 0

    private val currentFood = mutableListOf<FinalMeal>() //adds a new meal. if the recipe gets changed afterwards, it won't get updated

    fun addMeal(finalMealAux: FinalMeal) {
        val finalMeal = finalMealAux.copy()
        currentCalories += finalMeal.getTotalCal()
        protein += finalMeal.getTotalProtein()
        qualProtein += finalMeal.getTotalQualProtein()
        carbs += finalMeal.getTotalCarbs()
        fat += finalMeal.getTotalFat()
        cost += finalMeal.getTotalPrice()
        foodWeight += finalMeal.weightEaten
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

        currentCalories += newMeal.getTotalCal()
        protein += newMeal.getTotalProtein()
        qualProtein += newMeal.getTotalQualProtein()
        carbs += newMeal.getTotalCarbs()
        fat += newMeal.getTotalFat()
        cost += newMeal.getTotalPrice()
        foodWeight += newMeal.weightEaten

        var copyOfNew = newMeal.copy()
        currentFood.replaceAll { if (it == oldMeal) copyOfNew else it }
    }
}