package com.rares.calorietracker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rares.calorietracker.data.AllFoodList
import com.rares.calorietracker.data.FinalMeal
import com.rares.calorietracker.data.Meal
import com.rares.calorietracker.data.TypeOfMeal
import com.rares.calorietracker.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.rares.calorietracker.viewmodel.CalorieTrackerViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesView(allFoodList: AllFoodList, navController: NavController, viewModel: CalorieTrackerViewModel) {
    val tabs = listOf("Ingredients", "Meals", "Snacks")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            windowInsets = WindowInsets(0),
            title = { Text("Recipes") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )
        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(title) }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> IngredientsView(allFoodList, navController, viewModel)
                1 -> MealsView(allFoodList, navController, viewModel)
                2 -> SnacksView(allFoodList, navController, viewModel)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun IngredientsView(allFoodList: AllFoodList, navController: NavController, viewModel: CalorieTrackerViewModel) {
    var showAdd = remember { mutableStateOf(false) }
    val emptyIngredient = remember { mutableStateOf(Meal("New ingredient", TypeOfMeal.INGREDIENT)) }
    var coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            items(allFoodList.allIngredients.size) { MealCard(allFoodList.allIngredients[it], false, onClick = {
                navController.navigate("Ingredient/${allFoodList.allIngredients[it].id}")
            })
            }
        }
        if (showAdd.value) {
            EditSnackIngredient(
                meal = emptyIngredient.value,
                onDismiss = { wasAdded ->
                    if (wasAdded) {
                        coroutineScope.launch {
                            viewModel.makeDeepCopy(emptyIngredient.value, makeCustom = false)

                            emptyIngredient.value = Meal("New ingredient", TypeOfMeal.INGREDIENT)
                        }
                    }
                    showAdd.value = false
                },
                isToAdd = true
            )
        }
        FloatingActionButton(
            onClick = {
                showAdd.value = true
                emptyIngredient.value = Meal("New ingredient", TypeOfMeal.INGREDIENT)
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add ingredient")
        }
    }
}

@Composable
fun MealsView(allFoodList: AllFoodList, navController: NavController, viewModel: CalorieTrackerViewModel) {

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            items(allFoodList.allMeals.size) {
                MealCard(
                    allFoodList.allMeals[it], false,
                    onClick = {
                        navController.navigate("Meal/false/false/null/${allFoodList.allMeals[it].id}")
                    }) }
        }
        FloatingActionButton(
            onClick = { navController.navigate("Meal/true/false/null/-1") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add meal")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SnacksView(allFoodList: AllFoodList, navController: NavController, viewModel: CalorieTrackerViewModel) {
    var showAdd = remember { mutableStateOf(false) }
    val emptySnack = remember { mutableStateOf(Meal("New snack", TypeOfMeal.SNACK)) }
    var coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            items(allFoodList.allSnacks.size) { MealCard(allFoodList.allSnacks[it], false, onClick = {
                navController.navigate("Snack/${allFoodList.allSnacks[it].id}")
            })
            }
        }
        if (showAdd.value) {
            EditSnackIngredient(
                meal = emptySnack.value,
                onDismiss = { wasAdded ->
                    if (wasAdded) {
                        coroutineScope.launch {
                            viewModel.makeDeepCopy(emptySnack.value, makeCustom = false)

                            emptySnack.value = Meal("New snack", TypeOfMeal.SNACK)
                        }
                    }
                    showAdd.value = false
                },
                isToAdd = true
            )
        }
        FloatingActionButton(
            onClick = {
                showAdd.value = true
                emptySnack.value = Meal("New snack", TypeOfMeal.SNACK)
                      },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add snack")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecipesViewPreview() {
    val fakeAllFoodList = AllFoodList()
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
    fakeAllFoodList.addIngredient(chicken)
    fakeAllFoodList.addIngredient(rice)
    fakeAllFoodList.addIngredient(oliveOil)

    val proteinBar = Meal("Protein bar", TypeOfMeal.SNACK).apply {
        calories = 220.0; protein = 20.0; qualProtein = 18.0
        carbs = 25.0; fat = 7.0; salt = 0.3; price = 1.5; portionSize = 60.0
    }
    val banana = Meal("Banana", TypeOfMeal.SNACK).apply {
        calories = 89.0; protein = 1.1; qualProtein = 1.0
        carbs = 23.0; fat = 0.3; salt = 0.0; price = 0.3; portionSize = 100.0
    }
    fakeAllFoodList.addSnack(proteinBar)
    fakeAllFoodList.addSnack(banana)

    val chickenRiceBowl = Meal("Chicken Rice Bowl", TypeOfMeal.BASIC_MEAL).apply {
        addIngredient(chicken, 200.0)
        addIngredient(rice, 150.0)
        addIngredient(oliveOil, 10.0)
    }
    fakeAllFoodList.addMeal(chickenRiceBowl)
    val navController = rememberNavController()

    MyApplicationTheme {
        //RecipesView(allFoodList = fakeAllFoodList, navController)
    }
}