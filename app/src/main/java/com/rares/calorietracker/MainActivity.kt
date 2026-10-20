package com.rares.calorietracker

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
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
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.notes.app.model.service.impl.AccountServiceRepository
import com.rares.calorietracker.data.DayInfo
import com.rares.calorietracker.data.Meal
import com.rares.calorietracker.data.TypeOfMeal
import com.rares.calorietracker.viewmodel.CalorieTrackerViewModel
import com.rares.calorietracker.viewmodel.SignInViewModel
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
    var showUnsavedSettingsDialog by remember { mutableStateOf(false) }
    var pendingNavRoute by remember { mutableStateOf("") }
    var showDiscardMealDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val accountService = remember { AccountServiceRepository() }
    val signInViewModel = remember { SignInViewModel(accountService) }
    val currentUser by accountService.currentUser.collectAsState(initial = null)

    // shared navigation helper: go to [route] and clear [popUp] off the back stack
    val openAndPopUp: (String, String) -> Unit = { route, popUp ->
        navController.navigate(route) {
            popUpTo(popUp) { inclusive = true }
            launchSingleTop = true
        }
    }

    val goHome: () -> Unit = {
        navController.navigate("Home") {
            popUpTo("Home") { inclusive = false }
            launchSingleTop = true
        }
    }

    // global auth guard: whenever the user is signed out (startup, logout,
    // token expiry, account deletion), force the whole stack back to splash
    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            navController.navigate("splash") {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        } else {
            viewModel.loadData()
        }
    }

    //val color = if (isSystemInDarkTheme()) Color.White else Color.Black
    Scaffold(
        bottomBar = {
            if ( viewModel.isLoggedIn() ) {
                Log.d("DEBUGBOTTOM", "bottom bar")
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
                        if (showUnsavedSettingsDialog) {
                            AlertDialog(
                                onDismissRequest = { showUnsavedSettingsDialog = false },
                                title = { Text("Unsaved changes") },
                                text = { Text("Do you wish to keep your changes?") },
                                confirmButton = {
                                    TextButton(onClick = {
                                        viewModel.commitPendingSettings?.invoke()
                                        navController.navigate(pendingNavRoute)
                                        showUnsavedSettingsDialog = false
                                    }) { Text("Keep") }
                                },
                                dismissButton = {
                                    TextButton(onClick = {
                                        viewModel.hasUnsavedSettings = false
                                        navController.navigate(pendingNavRoute)
                                        showUnsavedSettingsDialog = false
                                    }) { Text("Discard") }
                                }
                            )
                        }
                        if (showDiscardMealDialog) {
                            AlertDialog(
                                onDismissRequest = { showDiscardMealDialog = false },
                                title = { Text("Discard meal?") },
                                text = { Text("This meal hasn't been added yet. Are you sure you want to delete it?") },
                                confirmButton = {
                                    TextButton(onClick = {
                                        viewModel.isEditingNewMeal = false
                                        navController.navigate(pendingNavRoute)
                                        showDiscardMealDialog = false
                                    }) { Text("Delete") }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDiscardMealDialog = false }) { Text("Keep editing") }
                                }
                            )
                        }
                        listOf("Home", "Recipes", null, "Stats", "Settings").forEach { route ->
                            if (route == null) {
                                Spacer(modifier = Modifier.weight(1f))
                            } else {
                                Card(
                                    onClick = {
                                        val currentRoute = navController.currentBackStackEntry?.destination?.route
                                        if (currentRoute == "Settings" && viewModel.hasUnsavedSettings && route != "Settings") {
                                            pendingNavRoute = route
                                            showUnsavedSettingsDialog = true
                                        } else if (viewModel.isEditingNewMeal) {
                                            pendingNavRoute = route
                                            showDiscardMealDialog = true
                                        } else {
                                            navController.navigate(route)
                                        }
                                    },
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
            startDestination = "splash",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("splash") {
                SplashScreen(openAndPopUp = openAndPopUp, accountService = accountService)
            }
            composable("signIn") {
                SignInScreen(
                    openAndPopUp = openAndPopUp,
                    goToSignUp = { navController.navigate("signUp") },
                    viewModel = signInViewModel
                )
            }
            composable("signUp") {
                SignUpScreen(openAndPopUp = openAndPopUp, viewModel = signInViewModel)
            }
            composable("Home") {
                HomeView(viewModel, navController)
            }
            composable("Recipes") {
                BackHandler { goHome() }
                RecipesView(viewModel.allFoodList, navController, viewModel)
            }
            composable("MealInDay/{day}/{mealId}") { entry ->
                BackHandler { goHome() }
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
                        viewModel.saveDayAsync(dayInfo)
                    }, isInDay = true)
                }
            }
            composable("Stats") {
                BackHandler { goHome() }
                StatsView(viewModel)
            }
            composable("Settings") {
                BackHandler(enabled = !viewModel.hasUnsavedSettings) { goHome() }
                SettingsView(viewModel, navController, accountService, onNavigateBack = goHome)
            }
            composable("Meal/{isToAdd}/{isCustom}/{dayToAdd}/{idMeal}") { entry ->
                BackHandler { goHome() }
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
                                    viewModel.saveDay(viewModel.getDayInfo(accDay))
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
                BackHandler { goHome() }
                val snackId = entry.arguments?.getString("snackId") ?: ""
                val snack = viewModel.allMealInfo[snackId]!!
                IngredientSnackView(snack, onDelete = {
                    coroutineScope.launch {
                        viewModel.delMeal(snack)
                    }
                }, viewModel = viewModel, navController = navController)
            }
            composable("Ingredient/{ingredientId}") { entry ->
                BackHandler { goHome() }
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