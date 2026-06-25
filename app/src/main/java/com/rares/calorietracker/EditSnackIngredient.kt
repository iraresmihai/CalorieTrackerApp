package com.rares.calorietracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import com.rares.calorietracker.data.Meal
import com.rares.calorietracker.data.TypeOfMeal

private fun Double.toInputString(): String =
    if (this % 1.0 == 0.0) this.toInt().toString() else this.toString()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSnackIngredient(meal: Meal, isToAdd: Boolean = false, onDismiss: (wasAdded: Boolean) -> Unit) {
    var newProtein = remember {
        mutableStateOf(meal.protein.toInputString())
    }

    var newQualProtein = remember {
        mutableStateOf(meal.qualProtein.toInputString())
    }

    var newCarbs = remember {
        mutableStateOf(meal.carbs.toInputString())
    }

    var newFat = remember {
        mutableStateOf(meal.fat.toInputString())
    }

    var newSalt = remember {
        mutableStateOf(meal.salt.toInputString())
    }

    var newPrice = remember {
        mutableStateOf(meal.price.toInputString())
    }

    var newCalories = remember {
        mutableStateOf(meal.calories.toInputString())
    }

    var newPortionSize = remember {
        mutableStateOf(meal.portionSize.toInputString())
    }

    var newName = remember {
        mutableStateOf(meal.name)
    }

    ModalBottomSheet(
        //properties = ModalBottomSheetProperties(shouldDismissOnBackPress = false),
        onDismissRequest = {onDismiss(false)},
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier.padding(8.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isToAdd)
                if ( meal.typeOfMeal == TypeOfMeal.INGREDIENT )
                    Text("Create new ingredient", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
                else
                    Text("Create new snack", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
            else
                Text("Change product info", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Name: ", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                OutlinedTextField(
                    modifier = Modifier.weight(2f),
                    value = newName.value,
                    onValueChange = {
                        newName.value = it
                    },
                    textStyle = MaterialTheme.typography.titleMedium
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Protein: ", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                OutlinedTextField(
                    modifier = Modifier.weight(2f),
                    value = newProtein.value,
                    onValueChange = {
                        newProtein.value = it
                    },
                    textStyle = MaterialTheme.typography.titleMedium
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Quality protein: ", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                OutlinedTextField(
                    modifier = Modifier.weight(2f),
                    value = newQualProtein.value,
                    onValueChange = {
                        newQualProtein.value = it
                    },
                    textStyle = MaterialTheme.typography.titleMedium
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Carbs: ", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                OutlinedTextField(
                    modifier = Modifier.weight(2f),
                    value = newCarbs.value,
                    onValueChange = {
                        newCarbs.value = it
                    },
                    textStyle = MaterialTheme.typography.titleMedium
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Fat: ", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                OutlinedTextField(
                    modifier = Modifier.weight(2f),
                    value = newFat.value,
                    onValueChange = {
                        newFat.value = it
                    },
                    textStyle = MaterialTheme.typography.titleMedium
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Calories: ", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                OutlinedTextField(
                    modifier = Modifier.weight(2f),
                    value = newCalories.value,
                    onValueChange = {
                        newCalories.value = it
                    },
                    textStyle = MaterialTheme.typography.titleMedium
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Salt: ", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                OutlinedTextField(
                    modifier = Modifier.weight(2f),
                    value = newSalt.value,
                    onValueChange = {
                        newSalt.value = it
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
                    value = newPortionSize.value,
                    onValueChange = {
                        newPortionSize.value = it
                    },
                    textStyle = MaterialTheme.typography.titleMedium
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Price: ", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                OutlinedTextField(
                    modifier = Modifier.weight(2f),
                    value = newPrice.value,
                    onValueChange = {
                        newPrice.value = it
                    },
                    textStyle = MaterialTheme.typography.titleMedium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {
                    newProtein.value = meal.protein.toInputString()
                    newQualProtein.value = meal.qualProtein.toInputString()
                    newCarbs.value = meal.carbs.toInputString()
                    newFat.value = meal.fat.toInputString()
                    newSalt.value = meal.salt.toInputString()
                    newPrice.value = meal.price.toInputString()
                    newCalories.value = meal.calories.toInputString()
                    newPortionSize.value = meal.portionSize.toInputString()
                    newName.value = meal.name
                    onDismiss(false)
                }) {
                    Text("Dismiss")
                }
                TextButton(onClick = {
                    meal.name = newName.value
                    meal.updInfo(
                        newCalories.value.toDoubleOrNull() ?: meal.calories,
                        newFat.value.toDoubleOrNull() ?: meal.fat,
                        newCarbs.value.toDoubleOrNull() ?: meal.carbs,
                        newProtein.value.toDoubleOrNull() ?: meal.protein,
                        newQualProtein.value.toDoubleOrNull() ?: meal.qualProtein,
                        newPrice.value.toDoubleOrNull() ?: meal.price,
                        (newPortionSize.value.toDoubleOrNull() ?: meal.portionSize).let { if (it == 0.0) 100.0 else it },
                        newSalt.value.toDoubleOrNull() ?: meal.salt
                    )

                    onDismiss(true)
                }) {
                    Text("OK")
                }
            }
        }
    }
}