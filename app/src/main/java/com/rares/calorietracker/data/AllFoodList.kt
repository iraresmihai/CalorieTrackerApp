package com.rares.calorietracker.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf

class AllFoodList(var id: String = "") {
    val allMeals = mutableStateListOf<Meal>()
    val allSnacks = mutableStateListOf<Meal>()
    val allIngredients = mutableStateListOf<Meal>()

    fun clear() {
        allMeals.clear()
        allSnacks.clear()
        allIngredients.clear()
    }

    fun addMeal(meal: Meal) {
        allMeals.add(meal)
    }

    fun delMeal(meal: Meal) {
        for ( x in allMeals ) {
            if ( x.id == meal.id ) {
                allMeals.remove(x)
                return
            }
        }
    }

    fun addSnack(meal: Meal) {
        allSnacks.add(meal)
    }

    fun delSnack(meal: Meal) {
        for ( x in allSnacks ) {
            if ( x.id == meal.id ) {
                allSnacks.remove(x)
                return
            }
        }
    }

    fun addIngredient(meal: Meal) {
        allIngredients.add(meal)
    }

    fun delIngredient(meal: Meal) {
        for ( x in allIngredients ) {
            if ( x.id == meal.id ) {
                allIngredients.remove(x)
                return
            }
        }
    }
}