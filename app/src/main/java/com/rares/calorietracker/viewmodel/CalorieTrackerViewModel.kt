package com.rares.calorietracker.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.rares.calorietracker.data.AllFoodList
import com.rares.calorietracker.data.DayInfo
import com.rares.calorietracker.data.TypeOfTraining
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.rares.calorietracker.data.FinalMeal
import com.rares.calorietracker.data.Meal
import com.rares.calorietracker.data.TypeOfMeal

@RequiresApi(Build.VERSION_CODES.O)
class CalorieTrackerViewModel : ViewModel() {
    var allFoodList by mutableStateOf(AllFoodList())
    var typeOfTraining by mutableStateOf(TypeOfTraining())
    var allDays = mutableStateMapOf<LocalDate, DayInfo>()
    init {
        // Ingredients
        val chicken = Meal("Chicken breast", TypeOfMeal.INGREDIENT).apply {
            calories = 165.0; protein = 31.0; qualProtein = 31.0
            carbs = 0.0; fat = 3.6; salt = 0.1; price = 0.8; portionSize = 100.0
        }
        val rice = Meal("Rice", TypeOfMeal.INGREDIENT).apply {
            calories = 130.0; protein = 2.7; qualProtein = 2.7
            carbs = 28.0; fat = 0.3; salt = 0.0; price = 0.2; portionSize = 100.0
        }
        val oliveOil = Meal("Olive oil", TypeOfMeal.INGREDIENT).apply {
            calories = 884.0; protein = 0.0; qualProtein = 0.0
            carbs = 0.0; fat = 100.0; salt = 0.0; price = 1.0; portionSize = 100.0
        }
        allFoodList.addIngredient(chicken)
        allFoodList.addIngredient(rice)
        allFoodList.addIngredient(oliveOil)

        // Snacks
        val proteinBar = Meal("Protein bar", TypeOfMeal.SNACK).apply {
            calories = 220.0; protein = 20.0; qualProtein = 18.0
            carbs = 25.0; fat = 7.0; salt = 0.3; price = 1.5; portionSize = 60.0
        }
        val banana = Meal("Banana", TypeOfMeal.SNACK).apply {
            calories = 89.0; protein = 1.1; qualProtein = 1.0
            carbs = 23.0; fat = 0.3; salt = 0.0; price = 0.3; portionSize = 100.0
        }
        allFoodList.addSnack(proteinBar)
        allFoodList.addSnack(banana)

        // Meals
        val chickenRiceBowl = Meal("Chicken Rice Bowl", TypeOfMeal.BASIC_MEAL).apply {
            addIngredient(chicken, 200.0)
            addIngredient(rice, 150.0)
            addIngredient(oliveOil, 10.0)
        }
        allFoodList.addMeal(chickenRiceBowl)

        // Training
        typeOfTraining.addTraining("Push 1", -1)
        typeOfTraining.addTraining("Push 2", -1)
        typeOfTraining.addTraining("Pull 1", -1)
        typeOfTraining.addTraining("Legs 1", -1)
        typeOfTraining.addTraining("Rest", -1)
        typeOfTraining.targetCalories = 2300

        // Today's day
        val today = java.time.LocalDate.now()
        val todayInfo = getDayInfo(today)
        todayInfo.trainingName = "Push 1"
        todayInfo.workoutRating = 8
        todayInfo.weight = 66.0
        todayInfo.stepCount = 9000
        todayInfo.trainingTime = 75
        todayInfo.addMeal(FinalMeal(chickenRiceBowl, 360.0))
        todayInfo.addMeal(FinalMeal(proteinBar, 60.0))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDayInfo(date: LocalDate): DayInfo {
        if ( allDays.containsKey(date) ) {
            return allDays[date]!!
        }
        var lastTraining: String? = null
        var ans: String? = null
        var bestDay: LocalDate? = null

        if ( typeOfTraining.allTraining.size > 0 ) {
            ans = typeOfTraining.allTraining[0]
            for (day in allDays.keys) {
                if (day < date && (bestDay == null || bestDay < day)) {
                    bestDay = day;
                }
            }

            if (bestDay != null) {
                lastTraining = allDays[bestDay]!!.trainingName
                var id = typeOfTraining.allTraining.indexOf(lastTraining)
                if ( id != -1 ) {
                    var nrDays = ChronoUnit.DAYS.between(bestDay, date)
                    ans = typeOfTraining.allTraining[(id + nrDays.toInt()) % typeOfTraining.allTraining.size]
                }
            }
        }

        if ( ans == null ){
            ans = "None"
        }

        allDays[date] = DayInfo(date, ans, typeOfTraining.targetCalories.toDouble())

        return allDays[date]!!
    }
}