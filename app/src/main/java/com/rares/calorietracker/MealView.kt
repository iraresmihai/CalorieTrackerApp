package com.rares.calorietracker

import com.rares.calorietracker.data.AllFoodList

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rares.calorietracker.data.Meal
import com.rares.calorietracker.data.TypeOfMeal
import com.rares.calorietracker.ui.theme.MyApplicationTheme
import java.time.LocalDate
import androidx.compose.runtime.State
import androidx.navigation.NavController
import com.rares.calorietracker.viewmodel.CalorieTrackerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MealView(meal: Meal, viewModel: CalorieTrackerViewModel, navController: NavController, isToAdd: Boolean = false, onAdd: (wasAdded: Boolean) -> Unit = {}, isCustom: Boolean = false, portionSize: Double = -1.0) {
    /* TODO MEAL VIEW FOR MEAL IN A DAY */
    var foodList = viewModel.allFoodList
    var deleteDialog = remember {
        mutableStateOf(false)
    }
    var showAddIngredient = remember { mutableStateOf(false) }
    var showAddSnack = remember { mutableStateOf(false) }
    var mealName = remember { mutableStateOf(meal.name) }
    var showAddIngredientCustom = remember { mutableStateOf(false) }
    var emptyIngredient = remember { mutableStateOf(Meal("New ingredient", TypeOfMeal.INGREDIENT)) }

    BackHandler {
        if (isToAdd) {
            onAdd(false)
            navController.popBackStack()
        } else {
            onAdd(true)
            navController.popBackStack()
        }
    }
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
    ) {
        if ( showAddIngredientCustom.value ) {
            EditSnackIngredient(
                meal = emptyIngredient.value,
                onDismiss = { wasAdded ->
                    if (wasAdded) {
                        meal.addIngredient(emptyIngredient.value, emptyIngredient.value.portionSize)
                        emptyIngredient.value = Meal("New ingredient", TypeOfMeal.INGREDIENT)
                    }
                    showAddIngredientCustom.value = false
                },
                isToAdd = true
            )
        }
        if (showAddIngredient.value) {
            AddIngredientDialog(
                ingredients = foodList.allIngredients,
                onDismiss = { showAddIngredient.value = false },
                onConfirm = { ingredient, weight ->
                    meal.addIngredient(ingredient, weight)
                    showAddIngredient.value = false
                },
                whatToAdd = "ingredient"
            )
        }
        if (showAddSnack.value) {
            AddIngredientDialog(
                ingredients = foodList.allSnacks,
                onDismiss = { showAddSnack.value = false },
                onConfirm = { ingredient, weight ->
                    meal.addIngredient(ingredient, weight)
                    showAddSnack.value = false
                },
                whatToAdd = "snack"
            )
        }
        if ( deleteDialog.value ) {
            AlertDialog(
                onDismissRequest = { deleteDialog.value = false },
                title = { Text("Delete meal") },
                text = { Text("This action cannot be undone.") },
                confirmButton = {
                    TextButton(onClick = {
                        onAdd(false)
                        deleteDialog.value = false
                        navController.popBackStack()
                    }) { Text("Delete") }
                },
                dismissButton = {
                    TextButton(onClick = { deleteDialog.value = false }) { Text("Cancel") }
                }
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if ( !isToAdd ) {
                    Spacer(modifier = Modifier.weight(1f))
                } else {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                        if ( isToAdd ) {
                            IconButton(onClick = {
                                onAdd(true)
                                navController.popBackStack()
                            }) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Add",
                                    tint = Color.Green
                                )
                            }
                        } else {
                            IconButton(onClick = {
                                onAdd(true)
                                navController.popBackStack()
                            }) {
                                Icon(
                                    Icons.Default.ChevronLeft,
                                    contentDescription = "Back",
                                    tint = Color.Green
                                )
                            }
                        }
                    }
                }
                BasicTextField(
                    value = mealName.value,
                    onValueChange = {
                        mealName.value = it
                        meal.name = it
                    },
                    textStyle = MaterialTheme.typography.titleMedium
                )
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                    IconButton(onClick = {
                        deleteDialog.value = true
                    }) {
                        if (!isToAdd)
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        else
                            Icon(Icons.Default.Clear, contentDescription = "Delete", tint = Color.Red)
                    }
                }
            }
            HorizontalDivider(thickness = 3.dp)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "%.1f".format(meal.protein) + "g Protein",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "%.1f".format(meal.fat) + "g Fat",
                style = MaterialTheme.typography.titleMedium
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "%.1f".format(meal.qualProtein) + "g Quality Protein",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "%.1f".format(meal.carbs) + "g Carbs",
                style = MaterialTheme.typography.titleMedium
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                textAlign = TextAlign.End,
                text = "%.2f".format(meal.calories) + " kcal",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                textAlign = TextAlign.End,
                text = "%.2f".format(meal.price) + " CHF",
                style = MaterialTheme.typography.titleMedium
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${meal.portionSize.toInt()}g - ${"%.1f".format(meal.salt)}g Salt",
                style = MaterialTheme.typography.titleMedium
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            //horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "Contains:",
                style = MaterialTheme.typography.titleMedium
            )
            HorizontalDivider(thickness = 3.dp)
        }
        Card(modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.outlineVariant)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(7.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showAddSnack.value = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Text("Add snack")
                        }
                        Button(
                            onClick = { showAddIngredient.value = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Text("Add ingredient")
                        }
                        if ( isCustom ) {
                            Button(
                                onClick = { showAddIngredientCustom.value = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Text("Add custom ingredient")
                            }
                        }
                    }
                }
                items(meal.ingredients.size) {
                    FoodView(meal.ingredients[it], onDelete = {
                        meal.ingredients.removeAt(it)
                        meal.recalcInfo()
                    })
                }
            }
        }
    }
}

/*
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun MealViewPreview() {
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
    //chicken.mealsWhichContain.add(chicken)
    MyApplicationTheme {
        MealView(meal = chickenRiceBowl, foodList = fakeAllFoodList)
    }
}
 */