package com.rares.calorietracker

import android.graphics.drawable.Icon
import android.os.Build
import android.view.Display
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rares.calorietracker.data.DayInfo
import com.rares.calorietracker.ui.theme.MyApplicationTheme
import java.time.LocalDate
import kotlin.math.max
import kotlin.math.min


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayView(dayInfo: DayInfo) {
    val ringColor = MaterialTheme.colorScheme.surfaceContainer

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* TODO GO TO PREVIOUS DAY */ }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous day")
            }
            Text(text = dayInfo.date.dayOfWeek.toString().lowercase().replaceFirstChar { it.uppercase() } + " - " + dayInfo.date, style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = { /* TODO GO TO NEXT DAY */ }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Previous day")
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
                    Text(text = dayInfo.carbs.toInt().toString() + " C", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1.4f))
                    Text(text = dayInfo.fat.toInt().toString() + " F", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = "%.1f".format(dayInfo.salt) + "g Salt", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Total cost: " + "%.2f".format(dayInfo.cost) + " CHF", style = MaterialTheme.typography.titleMedium)
            Text(text = "Food weight: " + "%.1f".format(dayInfo.foodWeight/ 1000) + " KG", style = MaterialTheme.typography.titleMedium)
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
                    IconButton(onClick = { /* TODO settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            }
            HorizontalDivider()
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Training score: ${dayInfo.workoutRationg}/10", modifier = Modifier.weight(1f))
                Text("Weight: ${dayInfo.weight} KG", modifier = Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Training time: ${dayInfo.trainingTime}", modifier = Modifier.weight(1f))
                Text("Step count: ${dayInfo.stepCount}", modifier = Modifier.weight(1f))
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
        workoutRationg = 9
        weight = 65.1
        stepCount = 8500
        trainingTime = 60
        salt = 2.56
    }
    MyApplicationTheme {
        DayView(dayInfo = fakeDayInfo)
    }
}

