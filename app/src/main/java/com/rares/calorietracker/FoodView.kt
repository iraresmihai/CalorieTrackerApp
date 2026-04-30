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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FoodView(meal: FinalMeal){
    Column() {
        Card(
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
                            text = meal.getTotalCal().toInt().toString() + " kcal",
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
                                text = meal.getTotalProtein().toInt().toString() + " P",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = meal.getTotalFat().toInt().toString() + " F",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = meal.getTotalCarbs().toInt().toString() + " C",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Text(
                            textAlign = TextAlign.End,
                            text = "%.2f".format(meal.getTotalPrice()) + " CHF",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Text(
                        text = "${meal.weightEaten.toInt()}g - ${"%.1f".format(meal.getTotalSalt())}g Salt",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
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
                    Text("Modify", style = MaterialTheme.typography.titleSmall, fontSize = 11.sp)
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun foodViewPreview() {
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
    MyApplicationTheme {
        FoodView(meal = fakeFinalMeal,)
    }
}