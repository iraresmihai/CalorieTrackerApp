package com.rares.calorietracker

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.ui.graphics.Color
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.rares.calorietracker.data.StatInfoWithDate
import com.rares.calorietracker.viewmodel.CalorieTrackerViewModel
import java.time.LocalDate
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.data.ExtraStore

enum class StatPlot(val displayName: String) {
    CALORIES("Calories"),
    WEIGHT("Weight"),
    TRAINING_TIME("Training time"),
    COST("Cost"),
    SALT("Salt"),
}

enum class TimeRange(val displayName: String) {
    ONE_WEEK("1 week"),
    ONE_MONTH("1 month"),
    THREE_MONTHS("3 months"),
    SIX_MONTHS("6 months"),
    ONE_YEAR("1 year"),
    ALL("All time")
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatsView(viewModel: CalorieTrackerViewModel) {
    var selectedPlot by remember { mutableStateOf(StatPlot.CALORIES) }
    var selectedRange by remember { mutableStateOf(TimeRange.ONE_MONTH) }
    var expandedRange by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.35f)
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.outlineVariant)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(7.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                item {
                    Text(
                        "Plots",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    HorizontalDivider(thickness = 2.dp)
                }
                items(StatPlot.entries) { plot ->
                    Card(
                        onClick = { selectedPlot = plot },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedPlot == plot)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 8.dp)
                        ) {
                            Text(
                                plot.displayName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (selectedPlot == plot)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        VerticalDivider(thickness = 1.dp)

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .weight(0.65f)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(selectedPlot.displayName, style = MaterialTheme.typography.titleLarge)
            HorizontalDivider(thickness = 2.dp)

            ExposedDropdownMenuBox(
                expanded = expandedRange,
                onExpandedChange = { expandedRange = !expandedRange }
            ) {
                OutlinedTextField(
                    value = selectedRange.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Time range") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRange) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedRange,
                    onDismissRequest = { expandedRange = false }
                ) {
                    TimeRange.entries.forEach { range ->
                        DropdownMenuItem(
                            text = { Text(range.displayName) },
                            onClick = {
                                selectedRange = range
                                expandedRange = false
                            }
                        )
                    }
                }
            }

            val filtered = remember(selectedRange, viewModel.allStats.size) {
                val nrDays = when (selectedRange) {
                    TimeRange.ONE_WEEK -> 7
                    TimeRange.ONE_MONTH -> 30
                    TimeRange.THREE_MONTHS -> 90
                    TimeRange.SIX_MONTHS -> 180
                    TimeRange.ONE_YEAR -> 365
                    TimeRange.ALL -> -1
                }
                mutableListOf<StatInfoWithDate>().also { viewModel.getLastDays(it, nrDays) }
            }

            /*
            val filteredPlaceholder = remember(selectedRange) {
                val today = LocalDate.now()
                listOf(
                    StatInfoWithDate(2100.0, 76.5, 76.5, today.minusDays(29), 76.5, 60, 8.50, 3.2),
                    StatInfoWithDate(2300.0, 76.2, 76.2, today.minusDays(28), 76.4, 0,  7.80, 2.8),
                    StatInfoWithDate(1900.0, 0.0,  76.2, today.minusDays(27), 76.3, 45, 6.90, 2.5),
                    StatInfoWithDate(2400.0, 75.9, 75.9, today.minusDays(26), 76.2, 70, 9.20, 3.5),
                    StatInfoWithDate(2200.0, 75.8, 75.8, today.minusDays(25), 76.1, 55, 8.10, 3.0),
                    StatInfoWithDate(2500.0, 0.0,  75.8, today.minusDays(24), 76.0, 0,  7.50, 2.7),
                    StatInfoWithDate(2150.0, 75.6, 75.6, today.minusDays(23), 76.0, 50, 8.30, 3.1),
                    StatInfoWithDate(2350.0, 75.4, 75.4, today.minusDays(22), 75.9, 65, 9.00, 3.4),
                    StatInfoWithDate(2000.0, 75.2, 75.2, today.minusDays(21), 75.8, 40, 7.20, 2.6),
                    StatInfoWithDate(2450.0, 0.0,  75.2, today.minusDays(20), 75.7, 0,  8.60, 3.3),
                    StatInfoWithDate(2250.0, 75.0, 75.0, today.minusDays(19), 75.6, 60, 8.90, 3.2),
                    StatInfoWithDate(2100.0, 74.8, 74.8, today.minusDays(18), 75.4, 75, 9.50, 3.6),
                    StatInfoWithDate(2300.0, 74.9, 74.9, today.minusDays(17), 75.3, 50, 8.20, 3.0),
                    StatInfoWithDate(2400.0, 74.7, 74.7, today.minusDays(16), 75.1, 60, 8.70, 3.3),
                    StatInfoWithDate(1950.0, 0.0,  74.7, today.minusDays(15), 74.9, 0,  7.10, 2.5),
                    StatInfoWithDate(2200.0, 74.5, 74.5, today.minusDays(14), 74.8, 55, 8.40, 3.1),
                    StatInfoWithDate(2350.0, 74.3, 74.3, today.minusDays(13), 74.7, 65, 9.10, 3.4),
                    StatInfoWithDate(2100.0, 74.4, 74.4, today.minusDays(12), 74.6, 45, 7.90, 2.9),
                    StatInfoWithDate(2500.0, 74.1, 74.1, today.minusDays(11), 74.4, 70, 9.30, 3.5),
                    StatInfoWithDate(2250.0, 0.0,  74.1, today.minusDays(10), 74.3, 0,  8.00, 3.0),
                    StatInfoWithDate(2150.0, 73.9, 73.9, today.minusDays(9),  74.2, 50, 8.50, 3.2),
                    StatInfoWithDate(2300.0, 73.8, 73.8, today.minusDays(8),  74.1, 60, 8.80, 3.3),
                    StatInfoWithDate(2050.0, 73.7, 73.7, today.minusDays(7),  74.0, 40, 7.60, 2.7),
                    StatInfoWithDate(2400.0, 0.0,  73.7, today.minusDays(6),  73.9, 0,  8.20, 3.0),
                    StatInfoWithDate(2200.0, 73.5, 73.5, today.minusDays(5),  73.8, 55, 8.90, 3.4),
                    StatInfoWithDate(2350.0, 73.4, 73.4, today.minusDays(4),  73.7, 65, 9.20, 3.5),
                    StatInfoWithDate(2100.0, 73.3, 73.3, today.minusDays(3),  73.6, 45, 7.80, 2.8),
                    StatInfoWithDate(2500.0, 73.2, 73.2, today.minusDays(2),  73.5, 70, 9.40, 3.6),
                    StatInfoWithDate(2250.0, 73.0, 73.0, today.minusDays(1),  73.4, 60, 8.60, 3.2),
                    StatInfoWithDate(2300.0, 72.9, 72.9, today,               73.3, 55, 8.30, 3.1),
                )
            }
            */

            StatsPlaceholder(selectedPlot, filtered)
        }
    }
}

@Composable
fun StatsPlaceholder(plot: StatPlot, data: List<StatInfoWithDate>) {
    if (data.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.outlineVariant)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No data available", style = MaterialTheme.typography.bodyMedium)
            }
        }
        return
    }

    when (plot) {
        StatPlot.WEIGHT -> WeightChart(data)
        StatPlot.CALORIES -> CaloriesChart(data)
        StatPlot.TRAINING_TIME -> TrainingTimeChart(data)
        StatPlot.COST -> CostChart(data)
        StatPlot.SALT -> SaltChart(data)
    }
}

@Composable
fun WeightChart(data: List<StatInfoWithDate>) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(data) {
        modelProducer.runTransaction {
            lineSeries {
                series(data.map { if (it.weight == 0.0) it.usedWeight else it.weight })
                series(data.map { it.sevenDayAvg })
            }
        }
    }

    val line1 = LineCartesianLayer.rememberLine(
        fill = LineCartesianLayer.LineFill.single(fill(Color(0xFF42A5F5))),
        areaFill = null
    )
    val line2 = LineCartesianLayer.rememberLine(
        fill = LineCartesianLayer.LineFill.single(fill(Color(0xFFEF5350))),
        areaFill = null
    )

    val rangeProvider = remember(data) {
        val weights = data.map { if (it.weight == 0.0) it.usedWeight else it.weight }
        val min = weights.min()
        val max = weights.max()
        val padding = (max - min).coerceAtLeast(1.0) * 0.3
        object : CartesianLayerRangeProvider {
            override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore) = (min - padding).coerceAtLeast(0.0)
            override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore) = max + padding
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(line1, line2),
                rangeProvider = rangeProvider
            ),
            startAxis = VerticalAxis.rememberStart(
                valueFormatter = { _, value, _ -> "%.1f".format(value) }
            ),
            bottomAxis = HorizontalAxis.rememberBottom(),
        ),
        modelProducer = modelProducer,
        scrollState = rememberVicoScrollState(scrollEnabled = true),
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun CaloriesChart(data: List<StatInfoWithDate>) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(data) {
        modelProducer.runTransaction {
            lineSeries {
                series(data.map { it.calories })
            }
        }
    }

    val rangeProvider = remember(data) {
        val calories = data.map { it.calories }
        val min = calories.min()
        val max = calories.max()
        val padding = (max - min).coerceAtLeast(100.0) * 0.3
        object : CartesianLayerRangeProvider {
            override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore) = (min - padding).coerceAtLeast(0.0)
            override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore) = max + padding
        }
    }

    val line = LineCartesianLayer.rememberLine(
        fill = LineCartesianLayer.LineFill.single(fill(Color(0xFF66BB6A))),
        areaFill = null
    )

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(line),
                rangeProvider = rangeProvider
            ),
            startAxis = VerticalAxis.rememberStart(
                valueFormatter = { _, value, _ -> "%.0f".format(value) }
            ),
            bottomAxis = HorizontalAxis.rememberBottom(),
        ),
        modelProducer = modelProducer,
        scrollState = rememberVicoScrollState(scrollEnabled = true),
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun TrainingTimeChart(data: List<StatInfoWithDate>) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(data) {
        modelProducer.runTransaction {
            lineSeries { series(data.map { it.trainingTime.toDouble() }) }
        }
    }

    val rangeProvider = remember(data) {
        val values = data.map { it.trainingTime.toDouble() }
        val min = values.min()
        val max = values.max()
        val padding = (max - min).coerceAtLeast(10.0) * 0.3
        object : CartesianLayerRangeProvider {
            override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore) = (min - padding).coerceAtLeast(0.0)
            override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore) = max + padding
        }
    }

    val line = LineCartesianLayer.rememberLine(
        fill = LineCartesianLayer.LineFill.single(fill(Color(0xFFFFA726))),
        areaFill = null
    )

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(line),
                rangeProvider = rangeProvider
            ),
            startAxis = VerticalAxis.rememberStart(
                valueFormatter = { _, value, _ -> "%.0f min".format(value) }
            ),
            bottomAxis = HorizontalAxis.rememberBottom(),
        ),
        modelProducer = modelProducer,
        scrollState = rememberVicoScrollState(scrollEnabled = true),
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun CostChart(data: List<StatInfoWithDate>) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(data) {
        modelProducer.runTransaction {
            lineSeries { series(data.map { it.cost }) }
        }
    }

    val rangeProvider = remember(data) {
        val values = data.map { it.cost }
        val min = values.min()
        val max = values.max()
        val padding = (max - min).coerceAtLeast(1.0) * 0.3
        object : CartesianLayerRangeProvider {
            override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore) = (min - padding).coerceAtLeast(0.0)
            override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore) = max + padding
        }
    }

    val line = LineCartesianLayer.rememberLine(
        fill = LineCartesianLayer.LineFill.single(fill(Color(0xFFAB47BC))),
        areaFill = null
    )

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(line),
                rangeProvider = rangeProvider
            ),
            startAxis = VerticalAxis.rememberStart(
                valueFormatter = { _, value, _ -> "%.2f".format(value) }
            ),
            bottomAxis = HorizontalAxis.rememberBottom(),
        ),
        modelProducer = modelProducer,
        scrollState = rememberVicoScrollState(scrollEnabled = true),
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun SaltChart(data: List<StatInfoWithDate>) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(data) {
        modelProducer.runTransaction {
            lineSeries { series(data.map { it.salt }) }
        }
    }

    val rangeProvider = remember(data) {
        val values = data.map { it.salt }
        val min = values.min()
        val max = values.max()
        val padding = (max - min).coerceAtLeast(0.5) * 0.3
        object : CartesianLayerRangeProvider {
            override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore) = (min - padding).coerceAtLeast(0.0)
            override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore) = max + padding
        }
    }

    val line = LineCartesianLayer.rememberLine(
        fill = LineCartesianLayer.LineFill.single(fill(Color(0xFF26C6DA))),
        areaFill = null
    )

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(line),
                rangeProvider = rangeProvider
            ),
            startAxis = VerticalAxis.rememberStart(
                valueFormatter = { _, value, _ -> "%.1f g".format(value) }
            ),
            bottomAxis = HorizontalAxis.rememberBottom(),
        ),
        modelProducer = modelProducer,
        scrollState = rememberVicoScrollState(scrollEnabled = true),
        modifier = Modifier.fillMaxSize()
    )
}