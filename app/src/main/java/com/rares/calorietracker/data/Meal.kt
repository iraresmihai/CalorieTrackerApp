package com.rares.calorietracker.data

import androidx.compose.animation.core.Ease

data class Meal(var name : String, val typeOfMeal: TypeOfMeal = TypeOfMeal.CUSTOM_MEAL) {
    //a basic meal/custom meal can contain only ingredients and snacks
    var calories: Double = 0.0
    var portionSize: Double = 0.0
    var price: Double = 0.0
    var fat: Double = 0.0
    var carbs: Double = 0.0
    var protein: Double = 0.0
    var qualProtein: Double = 0.0

    private val ingredients = mutableListOf<FinalMeal>()

    private val mealsWhichContain = mutableListOf<Meal>() //all meals which contain this meal

    fun getCalPer100g(): Double {
        if ( portionSize != 0.0 ) {
            return calories / portionSize;
        }
        return 0.0;
    }

    fun getFatPer100g(): Double {
        if ( portionSize != 0.0 ) {
            return fat / portionSize;
        }
        return 0.0;
    }

    fun getPricePer100g(): Double {
        if ( portionSize != 0.0 ) {
            return price / portionSize;
        }
        return 0.0;
    }

    fun getCarbsPer100g(): Double {
        if ( portionSize != 0.0 ) {
            return carbs / portionSize;
        }
        return 0.0;
    }

    fun getProteinPer100g(): Double {
        if ( portionSize != 0.0 ) {
            return protein / portionSize;
        }
        return 0.0;
    }

    fun getQualProteinPer100g(): Double {
        if ( portionSize != 0.0 ) {
            return qualProtein / portionSize;
        }
        return 0.0;
    }

    fun isFinalMeal() : Boolean {
        return typeOfMeal == TypeOfMeal.INGREDIENT || typeOfMeal == TypeOfMeal.SNACK
    }

    fun addIngredient(meal: Meal, portionSize: Double) {
        if ( isFinalMeal() ) {
            return
        }
        if ( typeOfMeal != TypeOfMeal.CUSTOM_MEAL ) {
            meal.mealsWhichContain.add(this)
        }

        ingredients.add(FinalMeal(meal, portionSize))
        this.portionSize += portionSize
        this.calories += meal.getCalPer100g() * portionSize / 100
        this.fat += meal.getFatPer100g() * portionSize / 100
        this.carbs += meal.getCarbsPer100g() * portionSize / 100
        this.protein += meal.getProteinPer100g() * portionSize / 100
        this.qualProtein += meal.getQualProteinPer100g() * portionSize / 100
        this.price += meal.getPricePer100g() * portionSize / 100
    }

    fun recalcInfo() {
        if ( isFinalMeal() ) { //final type of ingredient
            return
        }
        this.portionSize = 0.0
        this.calories = 0.0
        this.fat = 0.0
        this.carbs = 0.0
        this.protein = 0.0
        this.qualProtein = 0.0
        this.price = 0.0
        for ( x in ingredients ) {
            this.portionSize += x.weightEaten
            this.calories += x.meal.getCalPer100g() * x.weightEaten / 100
            this.fat += x.meal.getFatPer100g() * x.weightEaten / 100
            this.carbs += x.meal.getCarbsPer100g() * x.weightEaten / 100
            this.protein += x.meal.getProteinPer100g() * x.weightEaten / 100
            this.qualProtein += x.meal.getQualProteinPer100g() * x.weightEaten / 100
            this.price += x.meal.getPricePer100g() * x.weightEaten / 100
        }
    }

    fun removeIngredient(meal: Meal) {
        ingredients.removeIf { it.meal == meal }
        recalcInfo()
    }

    fun updInfo(newCalories: Double, newFat: Double, newCarbs: Double, newProtein: Double, newQualProtein: Double, newPrice: Double, newPortionSize: Double) {
        if ( !isFinalMeal() ) {
            return
        }
        this.calories = newCalories
        this.fat = newFat
        this.carbs = newCarbs
        this.protein = newProtein
        this.qualProtein = newQualProtein
        this.price = newPrice
        this.portionSize = newPortionSize
        for ( x in mealsWhichContain ) {
            x.recalcInfo()
        }
    }
}