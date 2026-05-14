package com.rares.calorietracker.viewmodel

import android.os.Build
import android.util.Log
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.rares.calorietracker.data.FinalMeal
import com.rares.calorietracker.data.Meal
import com.rares.calorietracker.data.TypeOfMeal
import com.rares.calorietracker.model.FSDayInfo
import com.rares.calorietracker.repository.CalorieTrackerRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class CalorieTrackerViewModel : ViewModel() {
    val allFoodList = AllFoodList()
    val allMealInfo = mutableMapOf<String, Meal>()
    val allDays = mutableStateMapOf<LocalDate, DayInfo>()
    var typeOfTraining by mutableStateOf(TypeOfTraining())

    private val repository = CalorieTrackerRepository()

    init {
        viewModelScope.launch {
            // load food list first since days depend on it
            repository.loadAllFoodList(allFoodList, allMealInfo)

            // load training info
            typeOfTraining = repository.loadTypeOfTraining()
        }
    }

    fun isLoggedIn() : Boolean {
        return  repository.isLoggedIn()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getDayInfo(date: LocalDate): DayInfo {
        if ( allDays.containsKey(date) ) {
            return allDays[date]!!
        }
        val fsDays = mutableMapOf<LocalDate, FSDayInfo>()
        repository.loadFsDays(fsDays, listOf(date))

        val fsDay = fsDays[date]
        if ( fsDay != null && fsDay.date.isNotEmpty() ) {
            allDays[date] = repository.fsToDayInfo(fsDay, allMealInfo)
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

        allDays[date]!!.id = repository.saveDay(allDays[date]!!)

        return allDays[date]!!
    }

    fun saveMeal(meal: Meal) {
        viewModelScope.launch {
            meal.id = repository.saveMeal(meal)
            if ( !allMealInfo.containsKey(meal.id) ) {
                allMealInfo[meal.id] = meal
                if ( meal.typeOfMeal == TypeOfMeal.INGREDIENT ) {
                    allFoodList.addIngredient(meal)
                } else if ( meal.typeOfMeal == TypeOfMeal.SNACK ) {
                    allFoodList.addSnack(meal)
                } else if ( meal.typeOfMeal == TypeOfMeal.BASIC_MEAL ) {
                    allFoodList.addMeal(meal)
                }
            }
        }
    }

    suspend fun saveMealSuspended(meal: Meal) {
        meal.id = repository.saveMeal(meal)
        if ( !allMealInfo.containsKey(meal.id) ) {
            allMealInfo[meal.id] = meal
            if ( meal.typeOfMeal == TypeOfMeal.INGREDIENT ) {
                allFoodList.addIngredient(meal)
            } else if ( meal.typeOfMeal == TypeOfMeal.SNACK ) {
                allFoodList.addSnack(meal)
            } else if ( meal.typeOfMeal == TypeOfMeal.BASIC_MEAL ) {
                allFoodList.addMeal(meal)
            }
        }
    }

    fun saveDay(dayInfo: DayInfo) {
        viewModelScope.launch {
            dayInfo.id = repository.saveDay(dayInfo)
            if ( !allDays.containsKey(dayInfo.date) ) {
                allDays[dayInfo.date] = dayInfo
            }
        }
    }

    suspend fun delMeal(meal: Meal) {
        if ( meal.typeOfMeal == TypeOfMeal.CUSTOM_MEAL || meal.typeOfMeal == TypeOfMeal.CUSTOM_INGREDIENT ) {
            return
        }
        if ( meal.typeOfMeal == TypeOfMeal.BASIC_MEAL ) {
            allFoodList.delMeal(meal)
            meal.typeOfMeal = TypeOfMeal.CUSTOM_MEAL
            saveMeal(meal)
            return
        }
        if ( meal.typeOfMeal == TypeOfMeal.INGREDIENT ) {
            //allFoodList.delIngredient(meal)
        } else if ( meal.typeOfMeal == TypeOfMeal.SNACK ) {
            //allFoodList.delSnack(meal)
        }
        meal.typeOfMeal = TypeOfMeal.CUSTOM_INGREDIENT
        saveMealSuspended(meal)
        allFoodList.delSnack(meal)
        allFoodList.delIngredient(meal)

    }

    suspend fun makeDeepCopy(meal: Meal, makeCustom: Boolean = true, weightEaten: Double = meal.portionSize) : Meal { //automatically saves the deep copy to the database
        Log.d("DEBUG", "makeDeepCopy, typeOfMeal = ${meal.typeOfMeal}, calories = ${meal.calories}")
        var ans = meal.copy()
        Log.d("DEBUG", "makeDeepCopy, typeOfMeal = ${ans.typeOfMeal}, calories = ${ans.calories}, mealcal = ${meal.calories}")
        ans.id = ""
        if ( makeCustom && ans.typeOfMeal == TypeOfMeal.BASIC_MEAL) {
            ans.typeOfMeal = TypeOfMeal.CUSTOM_MEAL
        }
        if (ans.typeOfMeal == TypeOfMeal.INGREDIENT || ans.typeOfMeal == TypeOfMeal.SNACK || ans.typeOfMeal == TypeOfMeal.CUSTOM_INGREDIENT) {
            if ( makeCustom )
                ans.typeOfMeal = TypeOfMeal.CUSTOM_INGREDIENT
            if ( weightEaten != ans.portionSize ) {
                ans.calories = ans.calories * weightEaten / ans.portionSize
                ans.protein = ans.protein * weightEaten / ans.portionSize
                ans.qualProtein = ans.qualProtein * weightEaten / ans.portionSize
                ans.carbs = ans.carbs * weightEaten / ans.portionSize
                ans.fat = ans.fat * weightEaten / ans.portionSize
                ans.salt = ans.salt * weightEaten / ans.portionSize
                ans.price = ans.price * weightEaten / ans.portionSize
                ans.portionSize = weightEaten
            }
            saveMealSuspended(ans)
            return ans
        }
        ans.ingredients.clear()
        ans.recalcInfo()
        for ( x in meal.ingredients ) {
            if ( weightEaten != 0.0 ) {
                ans.addIngredient(
                    makeDeepCopy(x.meal),
                    x.weightEaten * weightEaten / meal.portionSize
                )
            } else {
                ans.addIngredient(makeDeepCopy(x.meal), 0.0)
            }
        }

        saveMealSuspended(ans)

        return ans
    }

    fun saveTypeOfTraining(typeOfTraining: TypeOfTraining) {
        viewModelScope.launch {
            typeOfTraining.id = repository.saveTypeOfTraining(typeOfTraining)
            this@CalorieTrackerViewModel.typeOfTraining = typeOfTraining
        }
    }
}