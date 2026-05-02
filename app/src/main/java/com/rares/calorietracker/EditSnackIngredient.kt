package com.rares.calorietracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSnackIngredient(meal: Meal, isToAdd: Boolean = false, onDismiss: (wasAdded: Boolean) -> Unit) {
    var newProtein = remember {
        mutableStateOf(meal.protein)
    }

    var newQualProtein = remember {
        mutableStateOf(meal.qualProtein)
    }

    var newCarbs = remember {
        mutableStateOf(meal.carbs)
    }

    var newFat = remember {
        mutableStateOf(meal.fat)
    }

    var newSalt = remember {
        mutableStateOf(meal.salt)
    }

    var newPrice = remember {
        mutableStateOf(meal.price)
    }

    var newCalories = remember {
        mutableStateOf(meal.calories)
    }

    var newPortionSize = remember {
        mutableStateOf(meal.portionSize)
    }

    var newName = remember {
        mutableStateOf(meal.name)
    }

    ModalBottomSheet(
        properties = ModalBottomSheetProperties(shouldDismissOnBackPress = false),
        onDismissRequest = {onDismiss(false)},
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isToAdd)
                if ( meal.typeOfMeal == TypeOfMeal.INGREDIENT )
                    Text("Create new ingredient", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
                else
                    Text("Create new snack", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
            else
                Text("Change macros", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))
            if ( isToAdd ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Name: ", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    OutlinedTextField(
                        modifier = Modifier.weight(2f),
                        value = meal.name,
                        onValueChange = {
                            newName.value = it
                        },
                        textStyle = MaterialTheme.typography.titleMedium
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Protein: ", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                OutlinedTextField(
                    modifier = Modifier.weight(2f),
                    value = meal.protein.toString(),
                    onValueChange = {
                        if ( it.toDoubleOrNull() != null )
                            newProtein.value = it.toDouble()
                        else
                            newProtein.value = meal.protein
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
                    value = meal.qualProtein.toString(),
                    onValueChange = {
                        if ( it.toDoubleOrNull() != null )
                            newQualProtein.value = it.toDouble()
                        else
                            newQualProtein.value = meal.qualProtein
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
                    value = meal.carbs.toString(),
                    onValueChange = {
                        if ( it.toDoubleOrNull() != null )
                            newCarbs.value = it.toDouble()
                        else
                            newCarbs.value = meal.carbs
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
                    value = meal.fat.toString(),
                    onValueChange = {
                        if ( it.toDoubleOrNull() != null )
                            newFat.value = it.toDouble()
                        else
                            newFat.value = meal.fat
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
                    value = meal.calories.toString(),
                    onValueChange = {
                        if ( it.toDoubleOrNull() != null )
                            newCalories.value = it.toDouble()
                        else
                            newCalories.value = meal.calories
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
                    value = meal.salt.toString(),
                    onValueChange = {
                        if ( it.toDoubleOrNull() != null )
                            newSalt.value = it.toDouble()
                        else
                            newSalt.value = meal.salt
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
                    value = meal.portionSize.toString(),
                    onValueChange = {
                        if ( it.toDoubleOrNull() != null )
                            newPortionSize.value = it.toDouble()
                        else
                            newPortionSize.value = meal.portionSize
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
                    value = meal.price.toString(),
                    onValueChange = {
                        if ( it.toDoubleOrNull() != null )
                            newPrice.value = it.toDouble()
                        else
                            newPrice.value = meal.price
                    },
                    textStyle = MaterialTheme.typography.titleMedium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {
                    newProtein.value = meal.protein
                    newQualProtein.value = meal.qualProtein
                    newCarbs.value = meal.carbs
                    newFat.value = meal.fat
                    newSalt.value = meal.salt
                    newPrice.value = meal.price
                    newCalories.value = meal.calories
                    newPortionSize.value = meal.portionSize
                    newName.value = meal.name
                    onDismiss(false)
                }) {
                    Text("Dismiss")
                }
                TextButton(onClick = {
                    meal.name = newName.value
                    meal.updInfo(newCalories.value, newFat.value, newCarbs.value, newProtein.value, newQualProtein.value, newPrice.value, newPortionSize.value, newSalt.value)

                    onDismiss(true)
                }) {
                    Text("OK")
                }
            }
        }
    }
}