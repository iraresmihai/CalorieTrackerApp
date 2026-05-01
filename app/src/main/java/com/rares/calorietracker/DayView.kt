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

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayView(dayInfo: DayInfo, allTrainingNames: TypeOfTraining) {
    val ringColor = MaterialTheme.colorScheme.surfaceContainer

    var settingsDialog = remember {
        mutableStateOf(false)
    }
    var rating = remember {
        mutableStateOf(dayInfo.workoutRating)
    }
    var trainingTime = remember {
        mutableStateOf(dayInfo.trainingTime)
    }
    var nrSteps = remember {
        mutableStateOf(dayInfo.stepCount)
    }
    var weight = remember {
        mutableStateOf(dayInfo.weight)
    }
    var expandedTrainingName = remember {
        mutableStateOf(false)
    }
    var selectedTraining = remember {
        mutableStateOf(dayInfo.trainingName)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        if (settingsDialog.value) {
            ModalBottomSheet(
                properties = ModalBottomSheetProperties(shouldDismissOnBackPress = false),
                onDismissRequest = {
                    settingsDialog.value = false
                    expandedTrainingName.value = false
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
                            value = rating.value.toString(),
                            onValueChange = {
                                if ( it.toIntOrNull() != null && 1 <= it.toInt() && it.toInt() <=10 )
                                    rating.value = it.toInt()
                                else
                                    rating.value = dayInfo.workoutRating
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
                            value = trainingTime.value.toString(),
                            onValueChange = {
                                if ( it.toIntOrNull() != null )
                                    trainingTime.value = it.toInt()
                                else
                                    trainingTime.value = dayInfo.trainingTime
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
                            value = nrSteps.value.toString(),
                            onValueChange = {
                                if ( it.toIntOrNull() != null )
                                    nrSteps.value = it.toInt()
                                else
                                    nrSteps.value = dayInfo.stepCount
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
                                if ( it.toDoubleOrNull() != null )
                                    weight.value = it.toDouble()
                                else
                                    weight.value = dayInfo.weight
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
                            weight.value = dayInfo.weight
                            trainingTime.value = dayInfo.trainingTime
                            rating.value = dayInfo.workoutRating
                            nrSteps.value = dayInfo.stepCount
                            selectedTraining.value = dayInfo.trainingName
                        }) {
                            Text("Dismiss")
                        }
                        TextButton(onClick = {
                            dayInfo.weight = weight.value
                            dayInfo.trainingTime = trainingTime.value
                            dayInfo.workoutRating = rating.value
                            dayInfo.stepCount = nrSteps.value
                            dayInfo.trainingName = selectedTraining.value

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
                IconButton(onClick = { /* TODO GO TO PREVIOUS DAY */ }) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous day"
                    )
                }
                Text(
                    text = dayInfo.date.dayOfWeek.toString().lowercase()
                        .replaceFirstChar { it.uppercase() } + " - " + dayInfo.date,
                    style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = { /* TODO GO TO NEXT DAY */ }) {
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        "Food log",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                        IconButton(onClick = { /* TODO settings */ }) {
                            Icon(Icons.Default.Add, contentDescription = "Settings")
                        }
                    }
                }
                HorizontalDivider(thickness = 3.dp, modifier = Modifier.padding(0.dp))
            }
        }
        Card(modifier = Modifier.fillMaxWidth().weight(1f), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.outlineVariant)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(7.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                items(dayInfo.currentFood.size) {
                    FoodView(dayInfo.currentFood[it],)
                }
            }
        }
    }
}

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
    val chickenMeal = Meal("Chicken breast", TypeOfMeal.INGREDIENT).apply {
        calories = 165.0
        protein = 31.0
        qualProtein = 31.0
        carbs = 0.0
        fat = 3.6
        salt = 0.1
        price = 0.8
        portionSize = 100.0
    }

    val rice = Meal("Rice", TypeOfMeal.INGREDIENT).apply {
        calories = 130.0
        protein = 2.7
        qualProtein = 2.7
        carbs = 28.0
        fat = 0.3
        salt = 0.0
        price = 0.2
        portionSize = 100.0
    }

    val oliveOil = Meal("Olive oil", TypeOfMeal.INGREDIENT).apply {
        calories = 884.0
        protein = 0.0
        qualProtein = 0.0
        carbs = 0.0
        fat = 100.0
        salt = 0.0
        price = 1.0
        portionSize = 100.0
    }

    val chickenRiceBowl = Meal("Chicken Rice Bowl", TypeOfMeal.BASIC_MEAL).apply {
        addIngredient(chickenMeal, 200.0)
        addIngredient(rice, 150.0)
        addIngredient(oliveOil, 10.0)
    }

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
        DayView(dayInfo = fakeDayInfo, allTrainingNames = fakeTypeOfTraining)
    }
}

