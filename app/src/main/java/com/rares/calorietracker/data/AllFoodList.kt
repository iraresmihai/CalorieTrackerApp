package com.rares.calorietracker.data

class AllFoodList {
    val allMeals = mutableListOf<Meal>()
    val allSnacks = mutableListOf<Meal>()
    val allIngredients = mutableListOf<Meal>()

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