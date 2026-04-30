package com.rares.calorietracker.data

import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate

class MealTest {
    @Test
    fun testFinalMealCalories() {
        val fakeDayInfo = DayInfo(
            date = LocalDate.of(2026, 3, 25),
            trainingName = "Push 1",
            targetCalories = 2700.0
        ).apply {
            currentCalories = 2450.0
            protein = 195.0
            qualProtein = 150.0
            carbs = 150.0
            fat = 130.0
            cost = 10.0
            foodWeight = 2500.0
            workoutRationg = 9
            weight = 65.1
            stepCount = 8500
            trainingTime = 60
            salt = 2.56
        }
        val chickenMeal = Meal("Chicken breast", TypeOfMeal.INGREDIENT).apply {
            calories = 165.0
            protein = 31.0
            qualProtein = 31.0
            carbs = 0.0
            fat = 3.6
            salt = 0.1
            price = 0.8
            portionSize = 100.0
        }

        val rice = Meal("Rice", TypeOfMeal.INGREDIENT).apply {
            calories = 130.0
            protein = 2.7
            qualProtein = 2.7
            carbs = 28.0
            fat = 0.3
            salt = 0.0
            price = 0.2
            portionSize = 100.0
        }

        val oliveOil = Meal("Olive oil", TypeOfMeal.INGREDIENT).apply {
            calories = 884.0
            protein = 0.0
            qualProtein = 0.0
            carbs = 0.0
            fat = 100.0
            salt = 0.0
            price = 1.0
            portionSize = 100.0
        }

        val chickenRiceBowl = Meal("Chicken Rice Bowl", TypeOfMeal.BASIC_MEAL).apply {
            addIngredient(chickenMeal, 200.0)
            addIngredient(rice, 150.0)
            addIngredient(oliveOil, 10.0)
        }

        val fakeFinalMeal = FinalMeal(chickenRiceBowl, chickenRiceBowl.portionSize)

        fakeDayInfo.addMeal(fakeFinalMeal)
        fakeDayInfo.addMeal(fakeFinalMeal)

        print(fakeDayInfo.currentFood.size)

        assertTrue(fakeFinalMeal.getTotalCal() > 0)
    }
}