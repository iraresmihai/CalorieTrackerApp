package com.rares.calorietracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.rares.calorietracker.data.Meal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIngredientDialog(
    ingredients: List<Meal>,
    onDismiss: () -> Unit,
    onConfirm: (Meal, Double) -> Unit,
    whatToAdd:String
) {
    var expanded = remember { mutableStateOf(false) }
    var selectedIngredient = remember { mutableStateOf<Meal?>(null) }
    var weightText = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = false),
        title = { Text("Add " + whatToAdd) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expanded.value,
                    onExpandedChange = { expanded.value = !expanded.value }
                ) {
                    OutlinedTextField(
                        value = selectedIngredient.value?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Ingredient") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false }
                    ) {
                        ingredients.forEach { ingredient ->
                            DropdownMenuItem(
                                text = { Text(ingredient.name) },
                                onClick = {
                                    selectedIngredient.value = ingredient
                                    weightText.value = ingredient.portionSize.toString()
                                    expanded.value = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = weightText.value,
                    onValueChange = { weightText.value = it },
                    label = { Text("Weight (g)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val weight = weightText.value.toDoubleOrNull()
                val ingredient = selectedIngredient.value
                if (weight != null && ingredient != null) {
                    onConfirm(ingredient, weight)
                }
            }) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}