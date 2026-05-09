package com.rares.calorietracker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rares.calorietracker.data.DayInfo
import com.rares.calorietracker.data.FinalMeal
import com.rares.calorietracker.data.Meal
import com.rares.calorietracker.data.TypeOfMeal
import com.rares.calorietracker.data.TypeOfTraining
import com.rares.calorietracker.ui.theme.MyApplicationTheme
import java.time.LocalDate
import kotlin.math.min
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import com.rares.calorietracker.data.AllFoodList
import com.rares.calorietracker.viewmodel.CalorieTrackerViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayView(dayInfo: DayInfo, allTrainingNames: TypeOfTraining, foodList: AllFoodList, navController: NavController,     onPreviousDay: () -> Unit = {},
            onNextDay: () -> Unit = {},
            isToday: Boolean = false, viewModel: CalorieTrackerViewModel
) {
    val ringColor = MaterialTheme.colorScheme.surfaceContainer

    var settingsDialog = remember {
        mutableStateOf(false)
    }
    var rating = remember {
        mutableStateOf(dayInfo.workoutRating.toString())
    }
    var trainingTime = remember {
        mutableStateOf(dayInfo.trainingTime.toString())
    }
    var nrSteps = remember {
        mutableStateOf(dayInfo.stepCount.toString())
    }
    var weight = remember {
        mutableStateOf(dayInfo.weight.toString())
    }
    var expandedTrainingName = remember {
        mutableStateOf(false)
    }
    var selectedTraining = remember {
        mutableStateOf(dayInfo.trainingName)
    }

    var showAddMeal = remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        if (showAddMeal.value) {
            AddIngredientDialog(
                ingredients = foodList.allMeals + foodList.allSnacks,
                onDismiss = { showAddMeal.value = false },
                onConfirm = { ingredient, weight ->
                    coroutineScope.launch {
                        val aux = viewModel.makeDeepCopy(ingredient)
                        dayInfo.addMeal(FinalMeal(ingredient, weight))
                        viewModel.saveDay(dayInfo)
                    }
                    showAddMeal.value = false
                },
                whatToAdd = "food"
            )
        }
        if (settingsDialog.value) {
            ModalBottomSheet(
                properties = ModalBottomSheetProperties(shouldDismissOnBackPress = false),
                onDismissRequest = {
                    settingsDialog.value = false
                    expandedTrainingName.value = false
                    weight.value = dayInfo.weight.toString()
                    trainingTime.value = dayInfo.trainingTime.toString()
                    rating.value = dayInfo.workoutRating.toString()
                    nrSteps.value = dayInfo.stepCount.toString()
                    selectedTraining.value = dayInfo.trainingName
                },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Change today's information", style = MaterialTheme.typography.titleLarge)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Training name: ", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        ExposedDropdownMenuBox(
                            modifier = Modifier.weight(2f),
                            expanded = expandedTrainingName.value,
                            onExpandedChange = {
                                expandedTrainingName.value = !expandedTrainingName.value
                            }
                        ) {
                            OutlinedTextField(
                                value = selectedTraining.value,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Training") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTrainingName.value) },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedTrainingName.value,
                                onDismissRequest = {
                                    expandedTrainingName.value = false
                                }
                            ) {
                                allTrainingNames.allTraining.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            selectedTraining.value = option
                                            expandedTrainingName.value = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Training rating: ", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        OutlinedTextField(
                            modifier = Modifier.weight(2f),
                            value = rating.value,
                            onValueChange = {
                                rating.value = it
                            },
                            textStyle = MaterialTheme.typography.titleMedium
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Training time: ", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        OutlinedTextField(
                            modifier = Modifier.weight(2f),
                            value = trainingTime.value,
                            onValueChange = {
                                trainingTime.value = it
                            },
                            textStyle = MaterialTheme.typography.titleMedium
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Nr. steps: ", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        OutlinedTextField(
                            modifier = Modifier.weight(2f),
                            value = nrSteps.value,
                            onValueChange = {
                                nrSteps.value = it
                            },
                            textStyle = MaterialTheme.typography.titleMedium
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Weight: ", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        OutlinedTextField(
                            modifier = Modifier.weight(2f),
                            value = weight.value.toString(),
                            onValueChange = {
                                weight.value = it
                            },
                            textStyle = MaterialTheme.typography.titleMedium
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            settingsDialog.value = false
                            expandedTrainingName.value = false
                            weight.value = dayInfo.weight.toString()
                            trainingTime.value = dayInfo.trainingTime.toString()
                            rating.value = dayInfo.workoutRating.toString()
                            nrSteps.value = dayInfo.stepCount.toString()
                            selectedTraining.value = dayInfo.trainingName
                        }) {
                            Text("Dismiss")
                        }
                        TextButton(onClick = {
                            if ( weight.value.toDoubleOrNull() == null ) {
                                weight.value = dayInfo.weight.toString()
                            }
                            if ( trainingTime.value.toIntOrNull() == null ) {
                                trainingTime.value = dayInfo.trainingTime.toString()
                            }
                            if ( rating.value.toIntOrNull() == null || !(1 <= rating.value.toInt() && rating.value.toInt() <= 10) ) {
                                rating.value = dayInfo.workoutRating.toString()
                            }
                            if ( nrSteps.value.toIntOrNull() == null ) {
                                nrSteps.value = dayInfo.stepCount.toString()
                            }

                            dayInfo.weight = weight.value.toDouble()
                            dayInfo.trainingTime = trainingTime.value.toInt()
                            dayInfo.workoutRating = rating.value.toInt()
                            dayInfo.stepCount = nrSteps.value.toInt()
                            dayInfo.trainingName = selectedTraining.value

                            viewModel.saveDay(dayInfo)

                            expandedTrainingName.value = false
                            settingsDialog.value = false
                        }) {
                            Text("OK")
                        }
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth().padding(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onPreviousDay() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous day"
                    )
                }
                Text(
                    text = dayInfo.date.dayOfWeek.toString().lowercase()
                        .replaceFirstChar { it.uppercase() } + " - " + dayInfo.date,
                    style = MaterialTheme.typography.titleMedium)
                IconButton(enabled = !isToday, onClick = { onNextDay() }) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Previous day"
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(120.dp)) {
                        drawArc(
                            color = ringColor,
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 12.dp.toPx())
                        )
                        if (dayInfo.currentCalories <= dayInfo.targetCalories + 100) {
                            val progr =
                                min(1.0, dayInfo.currentCalories / dayInfo.targetCalories).toFloat()
                            drawArc(
                                color = Color(0xFF90EE90),
                                startAngle = -90f,
                                sweepAngle = 360f * progr,
                                useCenter = false,
                                style = Stroke(width = 12.dp.toPx())
                            )
                        } else {
                            val progr = min(
                                1.0,
                                (dayInfo.currentCalories - dayInfo.targetCalories) / dayInfo.targetCalories
                            ).toFloat()
                            drawArc(
                                color = Color(0xFFE53935),
                                startAngle = -90f,
                                sweepAngle = 360f * progr,
                                useCenter = false,
                                style = Stroke(width = 12.dp.toPx())
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = dayInfo.currentCalories.toInt().toString(),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = dayInfo.targetCalories.toInt().toString(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Column(
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = dayInfo.protein.toInt().toString() + " P",
                            modifier = Modifier.weight(1.4f),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = dayInfo.qualProtein.toInt().toString() + " QP",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = dayInfo.carbs.toInt().toString() + " C",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1.4f)
                        )
                        Text(
                            text = dayInfo.fat.toInt().toString() + " F",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "%.1f".format(dayInfo.salt) + "g Salt",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total cost: " + "%.2f".format(dayInfo.cost) + " CHF",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Food weight: " + "%.1f".format(dayInfo.foodWeight / 1000) + " KG",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        dayInfo.trainingName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                        IconButton(onClick = { settingsDialog.value = true }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                }
                HorizontalDivider(thickness = 3.dp)
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Training score: ${dayInfo.workoutRating}/10", modifier = Modifier.weight(1f))
                    Text("Weight: ${dayInfo.weight} KG", modifier = Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Training time: ${dayInfo.trainingTime}", modifier = Modifier.weight(1f))
                    Text("Step count: ${dayInfo.stepCount}", modifier = Modifier.weight(1f))
                }
            }
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Food log",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                HorizontalDivider(thickness = 3.dp, modifier = Modifier.padding(top=  7.dp))
            }
        }
        Card(modifier = Modifier.fillMaxWidth().weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.outlineVariant)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(7.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                       // horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { showAddMeal.value = true },
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Text("Add meal/snack")
                        }
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { navController.navigate("Meal/true/true/${dayInfo.date}/-1") },
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Text("Add custom")
                        }
                    }
                }
                items(dayInfo.currentFood.size) {
                    FoodView(dayInfo.currentFood[it], onDelete = {
                        dayInfo.delMeal(dayInfo.currentFood[it])
                        viewModel.saveDay(dayInfo)
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
fun DayScreenPreview() {
    val fakeDayInfo = DayInfo(
        date = LocalDate.of(2026, 3, 25),
        trainingName = "Push 1",
        targetCalories = 2700.0
    ).apply {
        currentCalories = 2450.0
        protein = 195.0
        qualProtein = 150.0
        carbs = 150.0
        fat = 130.0
        cost = 10.0
        foodWeight = 2500.0
        workoutRating = 9
        weight = 65.1
        stepCount = 8500
        trainingTime = 60
        salt = 2.56
    }

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

    val fakeFinalMeal = FinalMeal(chickenRiceBowl, chickenRiceBowl.portionSize)

    fakeDayInfo.addMeal(fakeFinalMeal)
    fakeDayInfo.addMeal(fakeFinalMeal)
    fakeDayInfo.addMeal(fakeFinalMeal)
    fakeDayInfo.addMeal(fakeFinalMeal)
    fakeDayInfo.addMeal(fakeFinalMeal)
    fakeDayInfo.addMeal(fakeFinalMeal)
    fakeDayInfo.addMeal(fakeFinalMeal)
    fakeDayInfo.addMeal(fakeFinalMeal)
    fakeDayInfo.addMeal(fakeFinalMeal)
    fakeDayInfo.addMeal(fakeFinalMeal)
    val fakeTypeOfTraining = TypeOfTraining().apply {
        addTraining("Push 1", -1)
        addTraining("Push 2", -1)
        addTraining("Pull 1", -1)
        addTraining("Legs 1", -1)
        addTraining("Rest", -1)
    }

    MyApplicationTheme {
        DayView(dayInfo = fakeDayInfo, allTrainingNames = fakeTypeOfTraining, foodList = fakeAllFoodList)
    }
}
 */

