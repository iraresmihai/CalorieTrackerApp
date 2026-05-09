package com.rares.calorietracker.data

import androidx.compose.animation.core.Ease
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.setValue

data class Meal(val initialName : String, var typeOfMeal: TypeOfMeal = TypeOfMeal.CUSTOM_MEAL, var id: String = "") {
    //a basic meal/custom meal can contain only ingredients and snacks
    var name by mutableStateOf(initialName)
    var calories by mutableDoubleStateOf(0.0)
    var portionSize by mutableDoubleStateOf(0.0)
    var price by mutableDoubleStateOf(0.0)
    var fat by mutableDoubleStateOf(0.0)
    var carbs by mutableDoubleStateOf(0.0)
    var protein by mutableDoubleStateOf(0.0)
    var qualProtein by mutableDoubleStateOf(0.0)
    var salt by mutableDoubleStateOf(0.0)

    val ingredients = mutableStateListOf<FinalMeal>()

    val mealsWhichContain = mutableStateListOf<Meal>() //all meals which contain this meal

    fun getCalPer100g(): Double {
        if ( portionSize != 0.0 ) {
            return calories * 100 / portionSize;
        }
        return 0.0;
    }

    fun getFatPer100g(): Double {
        if ( portionSize != 0.0 ) {
            return fat * 100 / portionSize;
        }
        return 0.0;
    }

    fun getPricePer100g(): Double {
        if ( portionSize != 0.0 ) {
            return price * 100 / portionSize;
        }
        return 0.0;
    }

    fun getCarbsPer100g(): Double {
        if ( portionSize != 0.0 ) {
            return carbs * 100 / portionSize;
        }
        return 0.0;
    }

    fun getProteinPer100g(): Double {
        if ( portionSize != 0.0 ) {
            return protein * 100 / portionSize;
        }
        return 0.0;
    }

    fun getSaltPer100g(): Double {
        if ( portionSize != 0.0 ) {
            return salt * 100 / portionSize;
        }
        return 0.0
    }

    fun getQualProteinPer100g(): Double {
        if ( portionSize != 0.0 ) {
            return qualProtein * 100 / portionSize;
        }
        return 0.0;
    }

    fun isFinalMeal() : Boolean {
        return typeOfMeal == TypeOfMeal.INGREDIENT || typeOfMeal == TypeOfMeal.SNACK || typeOfMeal == TypeOfMeal.CUSTOM_INGREDIENT
    }

    fun addIngredient(meal: Meal, portionSize: Double) {
        if ( isFinalMeal() ) {
            return
        }
        if ( typeOfMeal != TypeOfMeal.CUSTOM_MEAL && meal.typeOfMeal != TypeOfMeal.CUSTOM_INGREDIENT ) {
            meal.mealsWhichContain.add(this)
        }

        ingredients.add(FinalMeal(meal, portionSize))
        this.portionSize += portionSize
        this.salt += meal.getSaltPer100g() * portionSize / 100
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
        this.salt = 0.0
        this.portionSize = 0.0
        this.calories = 0.0
        this.fat = 0.0
        this.carbs = 0.0
        this.protein = 0.0
        this.qualProtein = 0.0
        this.price = 0.0
        for ( x in ingredients ) {
            this.portionSize += x.weightEaten
            this.salt += x.meal.getSaltPer100g() * x.weightEaten / 100
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

    fun updInfo(newCalories: Double, newFat: Double, newCarbs: Double, newProtein: Double, newQualProtein: Double, newPrice: Double, newPortionSize: Double, newSalt: Double) {
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
        this.salt = newSalt
        for ( x in mealsWhichContain ) {
            x.recalcInfo()
        }
    }
}