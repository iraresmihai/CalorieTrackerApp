package com.rares.calorietracker.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.rares.calorietracker.data.AllFoodList
import com.rares.calorietracker.data.StatInfo
import com.rares.calorietracker.data.DayInfo
import com.rares.calorietracker.data.FinalMeal
import com.rares.calorietracker.data.Meal
import com.rares.calorietracker.data.TypeOfMeal
import com.rares.calorietracker.data.TypeOfTraining
import com.rares.calorietracker.model.FSDayInfo
import com.rares.calorietracker.model.FSFinalMeal
import com.rares.calorietracker.model.FSMeal
import com.rares.calorietracker.model.FSStats
import com.rares.calorietracker.model.FSTypeOfTraining
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class CalorieTrackerRepository {
    private val db = Firebase.firestore
    private val userDoc get() = db.collection("users").document(Firebase.auth.currentUser!!.uid)
    init {
        db.firestoreSettings = firestoreSettings {
            isPersistenceEnabled = true
        }
    }

    fun currentUserId() : String? {
        return Firebase.auth.currentUser?.uid
    }

    fun isLoggedIn() : Boolean {
        //Firebase.auth.signOut()
        //Log.d("LOGINUSER", Firebase.auth.currentUser.toString())
        return (Firebase.auth.currentUser != null)
    }
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
            if ( x.meal.isBlank() ) {
                continue
            }
            if ( !allIngredients.containsKey(x.meal) ) {
                loadCustomRecipesAndAdd(allIngredients, listOf(x.meal))
            }
            allIngredients[x.meal]?.let { aux.addIngredient(it, x.weightEaten) } //skipped if its document is missing
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
            if ( x.isBlank() ) { //left over from a meal that was saved to the day before it had an id
                continue
            }
            if ( !allIngredients.containsKey(x) ) {
                loadCustomRecipesAndAdd(allIngredients, listOf(x))
            }
            allIngredients[x]?.let { aux.addMeal(it) } //skipped if its document is missing
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

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun loadStats(allStats: MutableMap<LocalDate, StatInfo>) {
        val result = userDoc.collection("stats").document("summary").get().await()
        val fsStats = result.toObject(FSStats::class.java) ?: return
        for ((dateStr, statInfo) in fsStats.history) {
            allStats[LocalDate.parse(dateStr)] = statInfo
        }
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

    //loads the given meal documents. firestore rejects empty ids and allows at most 30 ids per whereIn, so they get filtered and split
    private suspend fun loadMealsById(neededIds: List<String>): List<FSMeal> {
        val ans = mutableListOf<FSMeal>()
        for ( chunk in neededIds.filter { it.isNotBlank() }.distinct().chunked(30) ) {
            val result = userDoc.collection("loggedInfo")
                .whereIn(FieldPath.documentId(), chunk)
                .get()
                .await()
            result.forEach { doc ->
                ans.add(doc.toObject(FSMeal::class.java).copy(id = doc.id))
            }
        }
        return ans
    }

    suspend fun loadCustomIngredients(allIngredients: MutableMap<String, Meal>, neededIds: List<String>) {
        for ( fsMeal in loadMealsById(neededIds) ) {
            allIngredients.set(fsMeal.id, fsToIngredientSnack(fsMeal, allIngredients))
        }
    }

    suspend fun loadCustomRecipes(targetList: MutableList<FSMeal>, neededIds: List<String>) {
        targetList.addAll(loadMealsById(neededIds))
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

    //ids are generated on the phone, so the object has its id right away. waiting for the server's id let other saves
    //run in the meantime with an empty id (a day pointing to food "", duplicate documents), and never finishes offline
    fun newMealId(): String {
        return userDoc.collection("loggedInfo").document().id
    }

    suspend fun saveDay(dayInfo: DayInfo, newStat: StatInfo? = null): String {
        if ( dayInfo.id.isEmpty() ) {
            dayInfo.id = userDoc.collection("days").document().id
        }
        val fsDay = DayInfoToFS(dayInfo)
        val newStatInfo : StatInfo

        if ( newStat == null )
            newStatInfo = StatInfo(dayInfo.currentCalories, dayInfo.weight, dayInfo.trainingTime, dayInfo.cost, dayInfo.salt)
        else
            newStatInfo = newStat

        userDoc.collection("stats").document("summary").set(hashMapOf("history" to hashMapOf(fsDay.date to newStatInfo)), SetOptions.merge())
        userDoc.collection("days").document(dayInfo.id).set(fsDay).await()
        return dayInfo.id
    }

    suspend fun saveMeal(meal: Meal): String {
        if ( meal.id.isEmpty() ) {
            meal.id = newMealId()
        }
        userDoc.collection("loggedInfo").document(meal.id).set(mealToFS(meal)).await()
        return meal.id
    }

    suspend fun saveTypeOfTraining(typeOfTraining: TypeOfTraining): String {
        if ( typeOfTraining.id.isEmpty() ) {
            typeOfTraining.id = userDoc.collection("trainingInfo").document().id
        }
        userDoc.collection("trainingInfo").document(typeOfTraining.id).set(TypeOfTrainingToFS(typeOfTraining)).await()
        return typeOfTraining.id
    }
}