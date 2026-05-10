package com.rares.calorietracker.data

data class FinalMeal(val meal: Meal, var weightEaten: Double, var name: String = meal.name) {
    fun getTotalCal() : Double {
        return meal.getCalPer100g() * weightEaten / 100
    }

    fun getTotalSalt() : Double {
        return meal.getSaltPer100g() * weightEaten / 100
    }
    fun getTotalFat() : Double {
        return meal.getFatPer100g() * weightEaten / 100
    }
    fun getTotalCarbs() : Double {
        return meal.getCarbsPer100g() * weightEaten / 100
    }
    fun getTotalProtein() : Double {
        return meal.getProteinPer100g() * weightEaten / 100
    }
    fun getTotalQualProtein() : Double {
        return meal.getQualProteinPer100g() * weightEaten / 100
    }
    fun getTotalPrice() : Double {
        return meal.getPricePer100g() * weightEaten / 100
    }
}