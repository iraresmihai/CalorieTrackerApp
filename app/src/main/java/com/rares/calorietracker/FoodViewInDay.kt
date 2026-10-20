package com.rares.calorietracker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rares.calorietracker.data.FinalMeal
import com.rares.calorietracker.ui.theme.MyApplicationTheme
import com.rares.calorietracker.data.Meal
import com.rares.calorietracker.data.TypeOfMeal
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Interval
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun FoodViewInDay(meal: Meal, onDelete: () -> Unit = {}, onWeightChange: (Double) -> Unit = {}, navController: NavController, day: String){
    var showEditWeight = remember { mutableStateOf(false) }

    if (showEditWeight.value) {
        EditWeightDialog(
            name = meal.name,
            currentWeight = meal.portionSize,
            onDismiss = { showEditWeight.value = false },
            onConfirm = { newWeight ->
                onWeightChange(newWeight)
                showEditWeight.value = false
            }
        )
    }

    Column() {
        Card(
            onClick = {
                if ( meal.isFinalMeal() ) { //ingredients and snacks only have a weight to change
                    showEditWeight.value = true
                } else {
                    navController.navigate("MealInDay/$day/${meal.id}")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(5.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(2f).padding(2.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = meal.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = meal.calories.toInt().toString() + " kcal",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = meal.protein.toInt().toString() + " P",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = meal.fat.toInt().toString() + " F",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = meal.carbs.toInt().toString() + " C",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Text(
                            textAlign = TextAlign.End,
                            text = "%.2f".format(meal.price) + " CHF",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Text(
                        text = "${meal.portionSize.toInt()}g - ${"%.1f".format(meal.salt)}g Salt",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        //.background(MaterialTheme.colorScheme.background)
                        .clickable { onDelete() }
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.background,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(5.dp)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null, tint = Color.Red)
                    }
                    Text("Delete", style = MaterialTheme.typography.titleSmall, fontSize = 11.sp)
                }
            }
        }
    }
}