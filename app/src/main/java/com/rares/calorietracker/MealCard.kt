package com.rares.calorietracker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.rares.calorietracker.data.Meal
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rares.calorietracker.data.TypeOfMeal
import com.rares.calorietracker.ui.theme.MyApplicationTheme

@Composable
fun MealCard(meal: Meal, viewModify: Boolean, onClick : () -> Unit = {}) {
        Card(
            modifier = Modifier.fillMaxWidth()
                .clickable{onClick()},
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
                if ( viewModify ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            //.background(MaterialTheme.colorScheme.background)
                            .clickable { }
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
                            Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White)
                        }
                        Text(
                            "Modify",
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
}

@Preview(showBackground = true)
@Composable
fun MealCardPreview() {
    val chicken = Meal("Chicken breast", TypeOfMeal.INGREDIENT).apply {
        calories = 165.0; protein = 31.0; qualProtein = 31.0
        carbs = 0.0; fat = 3.6; salt = 0.1; price = 0.8; portionSize = 100.0
    }
    MyApplicationTheme {
        MealCard(meal = chicken, viewModify = false)
    }
}