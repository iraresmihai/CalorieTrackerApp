package com.rares.calorietracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rares.calorietracker.data.AllFoodList
import com.rares.calorietracker.data.FinalMeal
import com.rares.calorietracker.data.Meal
import com.rares.calorietracker.data.TypeOfMeal
import com.rares.calorietracker.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

@Composable
fun RecipesView(allFoodList: AllFoodList) {
    val tabs = listOf("Ingredients", "Meals", "Snacks")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
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
                0 -> IngredientsView(allFoodList.allIngredients)
                1 -> MealsView(allFoodList.allMeals)
                2 -> SnacksView(allFoodList.allSnacks)
            }
        }
    }
}

@Composable
fun IngredientsView(ingredients: List<Meal>) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
        items(ingredients.size) { MealCard(ingredients[it]) }
    }
}

@Composable
fun MealsView(meals: List<Meal>) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
        items(meals.size) { MealCard(meals[it]) }
    }
}

@Composable
fun SnacksView(snacks: List<Meal>) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
        items(snacks.size) { MealCard(snacks[it]) }
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

    MyApplicationTheme {
        RecipesView(allFoodList = fakeAllFoodList)
    }
}