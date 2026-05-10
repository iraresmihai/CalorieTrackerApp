package com.rares.calorietracker.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.util.copy
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.firestore
import com.rares.calorietracker.data.AllFoodList
import com.rares.calorietracker.data.DayInfo
import com.rares.calorietracker.data.FinalMeal
import com.rares.calorietracker.data.Meal
import com.rares.calorietracker.data.TypeOfMeal
import com.rares.calorietracker.data.TypeOfTraining
import com.rares.calorietracker.model.FSDayInfo
import com.rares.calorietracker.model.FSFinalMeal
import com.rares.calorietracker.model.FSMeal
import com.rares.calorietracker.model.FSTypeOfTraining
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class CalorieTrackerRepository {
    private val db = Firebase.firestore
    private val USER_ID = "test"
    private val userDoc = db.collection("users").document(USER_ID)

    fun fsToIngredientSnack(FSMEal: FSMeal, allIngredients: MutableMap<String, Meal>) : Meal { //doesn t give back the isPartOf list
        if ( allIngredients.containsKey(FSMEal.id) ) {
            return allIngredients[FSMEal.id]!!
        }
        val aux = Meal(
            id = FSMEal.id,
            initialName = FSMEal.name,
            typeOfMeal = TypeOfMeal.valueOf(FSMEal.typeOfMeal)
        )
        aux.calories = FSMEal.calories
        aux.portionSize = FSMEal.portionSize
        aux.price = FSMEal.price
        aux.fat = FSMEal.fat
        aux.carbs = FSMEal.carbs
        aux.protein = FSMEal.protein
        aux.qualProtein = FSMEal.qualProtein
        aux.salt = FSMEal.salt

        allIngredients[aux.id] = aux

        return aux
    }

    suspend fun fsToMeal(FSMeal: FSMeal, allIngredients: MutableMap<String, Meal>): Meal {
        if ( allIngredients.containsKey(FSMeal.id) ) {
            return allIngredients[FSMeal.id]!!
        }

        val aux = Meal(
            id = FSMeal.id,
            initialName = FSMeal.name,
            typeOfMeal = TypeOfMeal.valueOf(FSMeal.typeOfMeal)
        )

        if ( aux.typeOfMeal == TypeOfMeal.INGREDIENT || aux.typeOfMeal == TypeOfMeal.SNACK || aux.typeOfMeal == TypeOfMeal.CUSTOM_INGREDIENT ) {
            allIngredients[aux.id] = fsToIngredientSnack(FSMeal, allIngredients)
            return allIngredients[aux.id]!!
        }

        for ( x in FSMeal.ingredients ) {
            if ( !allIngredients.containsKey(x.meal) ) {
                loadCustomRecipesAndAdd(allIngredients, listOf(x.meal))
            }
            aux.addIngredient(allIngredients[x.meal]!!, x.weightEaten)
        }

        aux.name = FSMeal.name

        allIngredients[aux.id] = aux

        return aux
    }

    suspend fun fsToMeal(FSMeal: String, allIngredients: MutableMap<String, Meal>): Meal {
        if ( allIngredients.containsKey(FSMeal) ) {
            return allIngredients[FSMeal]!!
        }

        loadCustomRecipesAndAdd(allIngredients, listOf(FSMeal))

        return allIngredients[FSMeal]!!
    }

    /*
    suspend fun fsToFinalMeal(FSFinalMeal: FSFinalMeal, allIngredients: MutableMap<String, Meal>): FinalMeal {
        if ( !allIngredients.containsKey(FSFinalMeal.meal) ) {
            loadCustomRecipesAndAdd(allIngredients, listOf(FSFinalMeal.meal))
        }
        return FinalMeal(
            meal = allIngredients[FSFinalMeal.meal]!!,
            weightEaten = FSFinalMeal.weightEaten,
            name = FSFinalMeal.name
        )
    }
    */

    fun fsToTypeOfTraining(FSTypeOfTraining: FSTypeOfTraining): TypeOfTraining {
        val aux = TypeOfTraining(FSTypeOfTraining.id)
        aux.targetCalories = FSTypeOfTraining.targetCalories
        for ( x in FSTypeOfTraining.allTraining ) {
            aux.addTraining(x, -1)
        }
        return aux
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fsToDayInfo(FSDayInfo: FSDayInfo, allIngredients: MutableMap<String, Meal>): DayInfo {
        val aux = DayInfo(date = LocalDate.parse(FSDayInfo.date), trainingName = FSDayInfo.trainingName, targetCalories = FSDayInfo.targetCalories, id = FSDayInfo.id)

        for ( x in FSDayInfo.currentFood ) {
            aux.addMeal(fsToMeal(x, allIngredients))
        }

        aux.workoutRating = FSDayInfo.workoutRating
        aux.weight = FSDayInfo.weight
        aux.stepCount = FSDayInfo.stepCount
        aux.trainingTime = FSDayInfo.trainingTime

        return aux
    }

    fun DayInfoToFS(dayInfo: DayInfo): FSDayInfo {
        val aux = mutableListOf<String>()
        for ( x in dayInfo.currentFood ) {
            aux.add(x.id)
        }
        return FSDayInfo(
            id = dayInfo.id,
            date = dayInfo.date.toString(),
            trainingName = dayInfo.trainingName,
            targetCalories = dayInfo.targetCalories,
            workoutRating = dayInfo.workoutRating,
            weight = dayInfo.weight,
            stepCount = dayInfo.stepCount,
            trainingTime = dayInfo.trainingTime,
            currentFood = aux.toList()
        )
    }

    fun TypeOfTrainingToFS(typeOfTraining: TypeOfTraining): FSTypeOfTraining {
        val aux = mutableListOf<String>()
        for ( x in typeOfTraining.allTraining ) {
            aux.add(x)
        }
        return FSTypeOfTraining(
            id = typeOfTraining.id,
            allTraining = aux.toList(),
            targetCalories = typeOfTraining.targetCalories
        )
    }

    fun mealToFS(meal: Meal): FSMeal {
        val aux = mutableListOf<FSFinalMeal>()
        for ( x in meal.ingredients ) {
            aux.add(FinalMealToFs(x))
        }

        return FSMeal(
            id = meal.id,
            typeOfMeal = meal.typeOfMeal.toString(),
            name = meal.name,
            calories = meal.calories,
            portionSize = meal.portionSize,
            price = meal.price,
            fat = meal.fat,
            carbs = meal.carbs,
            protein = meal.protein,
            qualProtein = meal.qualProtein,
            salt = meal.salt,
            ingredients = aux.toList()
        )
    }

    fun FinalMealToFs(FinalMeal: FinalMeal): FSFinalMeal {
        return FSFinalMeal(
            meal = FinalMeal.meal.id,
            weightEaten = FinalMeal.weightEaten,
            name = FinalMeal.name
        )
    }

    suspend fun loadIngredientsAndSnacks(allIngredients: MutableMap<String, Meal>) : MutableList<Meal> {
        val ans = mutableListOf<Meal>()
        val result = userDoc.collection("loggedInfo")
            .whereIn("typeOfMeal", listOf(TypeOfMeal.INGREDIENT.toString(), TypeOfMeal.SNACK.toString()))
            .get()
            .await()
        result.forEach { doc ->
            val fsMeal = doc.toObject(FSMeal::class.java).copy(id = doc.id)
            ans.add(fsToIngredientSnack(fsMeal, allIngredients))
        }
        return ans
    }

    fun getNeededCustomIds(FSMeal: FSMeal, targetList: MutableList<String>, allIngredients: MutableMap<String, Meal> = mutableMapOf<String, Meal>()) { //custom stuff are non-tileable(i.e. 2 meals can t share the same custom thing)
        for ( x in FSMeal.ingredients ) {
            if ( !allIngredients.containsKey(x.meal) ) {
                targetList.add(x.meal)
            }
        }
    }

    suspend fun loadAllFoodList(allFoodList: AllFoodList, allMealInfo: MutableMap<String, Meal>) {
        val aux = loadIngredientsAndSnacks(allMealInfo)

        for ( x in aux ) {
            if ( x.typeOfMeal == TypeOfMeal.INGREDIENT ) {
                allFoodList.addIngredient(x)
            } else {
                allFoodList.addSnack(x)
            }
        }

        val allFsMeals = loadFsMeals()
        val neededCustomIds = mutableListOf<String>()
        for ( x in allFsMeals ) {
            getNeededCustomIds(x, neededCustomIds, allMealInfo)
        }
        loadCustomIngredients(allMealInfo, neededCustomIds)

        for ( x in allFsMeals ) {
            allFoodList.addMeal(fsToMeal(x, allMealInfo))
        }
    }

    suspend fun loadFsMeals() : MutableList<FSMeal> {
        val ans = mutableListOf<FSMeal>()

        val result = userDoc.collection("loggedInfo")
            .whereIn("typeOfMeal", listOf(TypeOfMeal.BASIC_MEAL.toString()))
            .get()
            .await()
        result.forEach { doc ->
            val fsMeal = doc.toObject(FSMeal::class.java).copy(id = doc.id)
            ans.add(fsMeal)
        }
        return ans
    }

    suspend fun loadCustomIngredients(allIngredients: MutableMap<String, Meal>, neededIds: List<String>) {
        if ( neededIds.isEmpty() ) {
            return
        }
        val result = userDoc.collection("loggedInfo")
            .whereIn(FieldPath.documentId(), neededIds)
            .get()
            .await()
        result.forEach { doc ->
            val fsMeal = doc.toObject(FSMeal::class.java).copy(id = doc.id)
            allIngredients.set(fsMeal.id, fsToIngredientSnack(fsMeal, allIngredients))
        }
    }

    suspend fun loadCustomRecipes(targetList: MutableList<FSMeal>, neededIds: List<String>) {
        if ( neededIds.isEmpty() ) {
            return
        }
        val result = userDoc.collection("loggedInfo")
            .whereIn(FieldPath.documentId(), neededIds)
            .get()
            .await()
        result.forEach { doc ->
            val fsMeal = doc.toObject(FSMeal::class.java).copy(id = doc.id)
            targetList.add(fsMeal)
        }
    }

    suspend fun loadCustomRecipesAndAdd(allIngredients: MutableMap<String, Meal>, neededIds: List<String>) {
        val aux = mutableListOf<FSMeal>()
        val newIds = mutableListOf<String>()
        for ( x in neededIds ) {
            if ( !allIngredients.containsKey(x) ) {
                newIds.add(x)
            }
        }
        loadCustomRecipes(aux, newIds)
        newIds.clear()
        for ( x in aux ) {
            if ( x.ingredients.isNotEmpty() ) {
                for ( y in x.ingredients ) {
                    if ( !allIngredients.containsKey(y.meal) ) {
                        newIds.add(y.meal)
                    }
                }
            }
        }
        loadCustomIngredients(allIngredients, newIds)
        for ( x in aux ) {
            allIngredients[x.id] = fsToMeal(x, allIngredients)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loadFsDays(targetMap: MutableMap<LocalDate, FSDayInfo>, neededDates: List<LocalDate>) {
        if ( neededDates.isEmpty() ) {
            return
        }
        val dateStrings = neededDates.map { it.toString() }

        val result = userDoc.collection("days")
            .whereIn("date", dateStrings)
            .get()
            .await()
        result.forEach { doc ->
            val fsDay = doc.toObject(FSDayInfo::class.java).copy(id = doc.id)
            val date = LocalDate.parse(fsDay.date)
            targetMap[date] = fsDay
        }

        for ( x in neededDates ) {
            if ( !targetMap.containsKey(x) ) {
                targetMap[x] = FSDayInfo()
            }
        }
    }

    suspend fun loadDayMeals(FSDayInfo: FSDayInfo, allIngredients: MutableMap<String, Meal>) {
        val aux = mutableListOf<String>()
        for ( x in FSDayInfo.currentFood ) {
            if ( !allIngredients.contains(x) ) {
                aux.add(x)
            }
        }

        val aux2 = mutableListOf<FSMeal>()
        loadCustomRecipes(aux2, aux)
        aux.clear()

        for ( x in aux2 ) {
            for ( x2 in x.ingredients ) {
                if ( !allIngredients.contains(x2.meal) ) {
                    aux.add(x2.meal)
                }
            }
        }

        loadCustomIngredients(allIngredients, aux)

        for ( x in aux2 ) {
            allIngredients[x.id] = fsToMeal(x, allIngredients)
        }
    }

    suspend fun loadTypeOfTraining() : TypeOfTraining {
        var ans = TypeOfTraining()
        val result = userDoc.collection("trainingInfo")
            .get()
            .await()
        result.forEach { doc ->
            ans = fsToTypeOfTraining(doc.toObject(FSTypeOfTraining::class.java).copy(id = doc.id))
        }
        return ans
    }

    suspend fun saveDay(dayInfo: DayInfo): String {
        val fsDay = DayInfoToFS(dayInfo)
        return if (dayInfo.id.isEmpty()) {
            userDoc.collection("days").add(fsDay).await().id
        } else {
            userDoc.collection("days").document(dayInfo.id).set(fsDay).await()
            dayInfo.id
        }
    }

    suspend fun saveMeal(meal: Meal): String {
        val fsMeal = mealToFS(meal)
        return if (meal.id.isEmpty()) {
            userDoc.collection("loggedInfo").add(fsMeal).await().id
        } else {
            userDoc.collection("loggedInfo").document(meal.id).set(fsMeal).await()
            meal.id
        }
    }

    suspend fun saveTypeOfTraining(typeOfTraining: TypeOfTraining): String {
        val fsTypeOfTraining = TypeOfTrainingToFS(typeOfTraining)
        return if (typeOfTraining.id.isEmpty()) {
            userDoc.collection("trainingInfo").add(fsTypeOfTraining).await().id
        } else {
            userDoc.collection("trainingInfo").document(typeOfTraining.id).set(fsTypeOfTraining).await()
            typeOfTraining.id
        }
    }
}