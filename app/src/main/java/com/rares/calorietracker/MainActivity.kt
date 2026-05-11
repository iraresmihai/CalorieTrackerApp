package com.rares.calorietracker

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rares.calorietracker.ui.theme.MyApplicationTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rares.calorietracker.data.DayInfo
import com.rares.calorietracker.data.Meal
import com.rares.calorietracker.data.TypeOfMeal
import com.rares.calorietracker.viewmodel.CalorieTrackerViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                App()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App() {
    val navController = rememberNavController()
    val viewModel: CalorieTrackerViewModel = viewModel()
    val showAddMeal = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    //val color = if (isSystemInDarkTheme()) Color.White else Color.Black
    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp).padding(top = 5.dp)
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    listOf("Home", "Recipes", null, "Stats", "Settings").forEach { route ->
                        if (route == null) {
                            Spacer(modifier = Modifier.weight(1f))
                        } else {
                            Card(
                                onClick = { navController.navigate(route) },
                                modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White.copy(alpha = 0.1f)
                                )
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                                ) {
                                    Text(route, fontSize = 9.sp)
                                }
                            }
                        }
                    }
                }

                // floating circle button in the middle
                FloatingActionButton(
                    onClick = { showAddMeal.value = true },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = (-16).dp)
                        .size(72.dp),
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Text("Add meal", fontSize = 12.sp)
                }
            }
        }
    ) {
        paddingValues ->
        if ( showAddMeal.value ) {
            AddIngredientDialog(
                ingredients = viewModel.allFoodList.allMeals + viewModel.allFoodList.allSnacks,
                onDismiss = { showAddMeal.value = false },
                onConfirm = { ingredient, weight ->
                    coroutineScope.launch {
                        val aux = viewModel.getDayInfo(LocalDate.now())
                        val ingredient2 = viewModel.makeDeepCopy(ingredient, makeCustom = true, weightEaten = weight)
                        aux.addMeal(ingredient2)
                        viewModel.saveDay(aux)
                    }
                    showAddMeal.value = false
                },
                whatToAdd = "ingredient"
            )
        }
        NavHost(
            navController = navController,
            startDestination = "Home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("Home") {
                HomeView(viewModel, navController)
            }
            composable("Recipes") {
                RecipesView(viewModel.allFoodList, navController, viewModel)
            }
            composable("MealInDay/{day}/{mealId}") { entry ->
                val day = entry.arguments?.getString("day")?.let {
                    if (it == "null") null else LocalDate.parse(it)
                }
                val mealId = entry.arguments?.getString("mealId") ?: ""
                var currDay by remember { mutableStateOf<DayInfo?>(null) }

                LaunchedEffect(day) {
                    currDay = viewModel.getDayInfo(day!!)
                }

                currDay?.let { dayInfo ->
                    val accMeal = viewModel.allMealInfo[mealId]!!
                    MealView(accMeal, viewModel, navController, onAdd = { wasAdded ->
                        if (!wasAdded) {
                            dayInfo.delMeal(accMeal)
                        }
                        dayInfo.recalcVal()
                        viewModel.saveDay(dayInfo)
                    }, isInDay = true)
                }
            }
            composable("Settings") {
                SettingsView(viewModel, navController)
            }
            composable("Meal/{isToAdd}/{isCustom}/{dayToAdd}/{idMeal}") { entry ->
                val isToAdd = entry.arguments?.getString("isToAdd")?.toBoolean() ?: false
                val isCustom = entry.arguments?.getString("isCustom")?.toBoolean() ?: false
                val dayToAdd = entry.arguments?.getString("dayToAdd")?.let {
                    if (it == "null") null else LocalDate.parse(it)
                }
                val idMeal = entry.arguments?.getString("idMeal") ?: ""

                if ( isToAdd ) {
                    if ( isCustom ) {
                        val accDay = LocalDate.parse(dayToAdd!!.toString())
                        val newMeal =  Meal("Enter name", typeOfMeal = TypeOfMeal.CUSTOM_MEAL)
                        MealView(newMeal, viewModel, navController, isToAdd = true, isCustom = true, onAdd = { wasAdded ->
                            if ( wasAdded ) {
                                coroutineScope.launch {
                                    viewModel.saveMeal(newMeal)
                                    viewModel.getDayInfo(accDay).addMeal(
                                        newMeal
                                    )
                                }
                            }
                        })
                    } else {
                        val newMeal = Meal("Enter name", typeOfMeal = TypeOfMeal.BASIC_MEAL)
                        MealView(newMeal, viewModel, navController, isToAdd = true, onAdd = { wasAdded ->
                            if ( wasAdded ) {
                                viewModel.saveMeal(newMeal)
                            }
                        })
                    }
                } else {
                    val meal = viewModel.allMealInfo[idMeal]!!
                    MealView(meal, viewModel, navController, onAdd = { wasAdded ->
                        if ( !wasAdded ) {
                            coroutineScope.launch {
                                viewModel.delMeal(meal)
                            }
                        } else {
                            coroutineScope.launch {
                                viewModel.saveMeal(meal)
                            }
                        }
                    })
                }
            }
            composable("Snack/{snackId}") { entry ->
                val snackId = entry.arguments?.getString("snackId") ?: ""
                val snack = viewModel.allMealInfo[snackId]!!
                IngredientSnackView(snack, onDelete = {
                    coroutineScope.launch {
                        viewModel.delMeal(snack)
                    }
                }, viewModel = viewModel, navController = navController)
            }
            composable("Ingredient/{ingredientId}") { entry ->
                val ingredientId = entry.arguments?.getString("ingredientId") ?: -1
                val ingredient = viewModel.allMealInfo[ingredientId]!!
                IngredientSnackView(ingredient, onDelete = {
                    coroutineScope.launch {
                        viewModel.delMeal(ingredient)
                    }
                }, viewModel = viewModel, navController = navController)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        App()
    }
}