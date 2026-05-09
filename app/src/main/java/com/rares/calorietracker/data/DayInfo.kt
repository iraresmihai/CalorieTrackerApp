package com.rares.calorietracker.data

import androidx.compose.runtime.mutableStateOf
import java.time.LocalDate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue

data class DayInfo(val date: LocalDate, var trainingName: String, val targetCalories: Double, var id: String = "") {
    var currentCalories by mutableDoubleStateOf(0.0)
    var protein by mutableDoubleStateOf(0.0)
    var qualProtein by mutableDoubleStateOf(0.0)
    var carbs by mutableDoubleStateOf(0.0)
    var fat by mutableDoubleStateOf(0.0)
    var cost by mutableDoubleStateOf(0.0)
    var salt by mutableDoubleStateOf(0.0)
    var foodWeight by mutableDoubleStateOf(0.0)

    var workoutRating by mutableIntStateOf(0)
    var weight by mutableDoubleStateOf(0.0)
    var stepCount by mutableIntStateOf(0)
    var trainingTime by mutableIntStateOf(0)

    val currentFood = mutableStateListOf<FinalMeal>() //adds a new meal. if the recipe gets changed afterwards, it won't get updated

    fun addMeal(finalMeal: FinalMeal) {
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

    fun recalcVal() {
        currentCalories = 0.0
        protein = 0.0
        qualProtein = 0.0
        carbs = 0.0
        fat = 0.0
        cost = 0.0
        salt = 0.0
        foodWeight = 0.0

        for ( x in currentFood ) {
            currentCalories += x.getTotalCal()
            protein += x.getTotalProtein()
            qualProtein += x.getTotalQualProtein()
            carbs += x.getTotalCarbs()
            fat += x.getTotalFat()
            cost += x.getTotalPrice()
            foodWeight += x.weightEaten
            salt += x.getTotalSalt()
        }
    }
    fun delMeal(finalMeal: FinalMeal) {
        currentFood.remove(finalMeal)
        recalcVal()
    }

    /*
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
     */
}