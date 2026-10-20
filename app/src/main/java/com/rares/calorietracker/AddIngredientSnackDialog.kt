package com.rares.calorietracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.rares.calorietracker.data.Meal
import java.math.RoundingMode

//100.0 -> "100", 12.50 -> "12.5", always with '.' so it can be parsed back
fun formatWeight(weight: Double): String {
    return weight.toBigDecimal().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
}

//accepts both "12.5" and "12,5"
fun parseWeight(text: String): Double? {
    return text.trim().replace(',', '.').toDoubleOrNull()
}

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
    var searchText = remember { mutableStateOf("") }
    var weightText = remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val query = searchText.value.trim()
    val filtered = if ( selectedIngredient.value != null ) {
        ingredients.sortedBy { it.name.lowercase() }
    } else {
        //names starting with the search come first, then alphabetical
        ingredients.filter { it.name.contains(query, ignoreCase = true) }
            .sortedWith(compareBy({ !it.name.startsWith(query, ignoreCase = true) }, { it.name.lowercase() }))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = true),
        title = { Text("Add " + whatToAdd) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expanded.value,
                    onExpandedChange = { expanded.value = it }
                ) {
                    OutlinedTextField(
                        value = searchText.value,
                        onValueChange = {
                            searchText.value = it
                            selectedIngredient.value = null
                            expanded.value = true
                        },
                        singleLine = true,
                        label = { Text("Ingredient") },
                        placeholder = { Text("Type to search") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable).fillMaxWidth()
                    )
                    //limited height so the menu fits under the field (over the weight) even with the keyboard open
                    ExposedDropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false },
                        modifier = Modifier.heightIn(max = 240.dp)
                    ) {
                        if ( filtered.isEmpty() ) {
                            DropdownMenuItem(
                                text = { Text("No matches") },
                                onClick = {},
                                enabled = false
                            )
                        }
                        filtered.forEach { ingredient ->
                            DropdownMenuItem(
                                text = { Text(ingredient.name) },
                                onClick = {
                                    selectedIngredient.value = ingredient
                                    searchText.value = ingredient.name
                                    weightText.value = formatWeight(ingredient.portionSize)
                                    expanded.value = false
                                    focusManager.clearFocus()
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = weightText.value,
                    onValueChange = { weightText.value = it },
                    label = { Text("Weight (g)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val weight = parseWeight(weightText.value)
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

@Composable
fun EditWeightDialog(
    name: String,
    currentWeight: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var weightText = remember { mutableStateOf(formatWeight(currentWeight)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = true),
        title = { Text(name) },
        text = {
            OutlinedTextField(
                value = weightText.value,
                onValueChange = { weightText.value = it },
                label = { Text("Weight (g)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = {
                val weight = parseWeight(weightText.value)
                if (weight != null && weight > 0) {
                    onConfirm(weight)
                }
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
