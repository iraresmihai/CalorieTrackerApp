package com.rares.calorietracker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rares.calorietracker.viewmodel.CalorieTrackerViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsView(viewModel: CalorieTrackerViewModel, navController: NavController) {
    var targetCaloriesText by remember { mutableStateOf(viewModel.typeOfTraining.targetCalories.toString()) }
    var showAddTraining by remember { mutableStateOf(false) }
    var newTrainingName by remember { mutableStateOf("") }
    val trainingList = remember { mutableStateListOf(*viewModel.typeOfTraining.allTraining.toTypedArray()) }
    val lazyListState = rememberLazyListState()
    val reorderState = rememberReorderableLazyListState(lazyListState) { from, to ->
        trainingList.add(to.index, trainingList.removeAt(from.index))
    }

    if (showAddTraining) {
        AlertDialog(
            onDismissRequest = { showAddTraining = false },
            title = { Text("Add training") },
            text = {
                OutlinedTextField(
                    value = newTrainingName,
                    onValueChange = { newTrainingName = it },
                    label = { Text("Training name") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newTrainingName.isNotEmpty()) {
                        trainingList.add(newTrainingName)
                        newTrainingName = ""
                    }
                    showAddTraining = false
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showAddTraining = false }) { Text("Cancel") }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TopAppBar(
            title = { Text("Recipes") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Calorie target:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = targetCaloriesText,
                    onValueChange = { targetCaloriesText = it },
                    modifier = Modifier.weight(2f),
                    textStyle = MaterialTheme.typography.titleMedium
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Training schedule:", style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = { showAddTraining = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add training")
                    }
                }
                HorizontalDivider(thickness = 3.dp)
            }

            Card(
                modifier = Modifier.fillMaxWidth().weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.outlineVariant)
            ) {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize().padding(7.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    items(trainingList.size, key = { trainingList[it] }) { index ->
                        ReorderableItem(reorderState, key = trainingList[index]) { isDragging ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = if (isDragging) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        trainingList[index],
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Row {
                                        IconButton(onClick = { trainingList.removeAt(index) }) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = Color.Red
                                            )
                                        }
                                        IconButton(
                                            modifier = Modifier.draggableHandle(),
                                            onClick = {}) {
                                            Icon(Icons.Default.Menu, contentDescription = "Drag")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = {
                viewModel.typeOfTraining.allTraining.clear()
                viewModel.typeOfTraining.allTraining.addAll(trainingList)
                viewModel.typeOfTraining.targetCalories = targetCaloriesText.toIntOrNull() ?: viewModel.typeOfTraining.targetCalories
                viewModel.saveTypeOfTraining(viewModel.typeOfTraining)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}