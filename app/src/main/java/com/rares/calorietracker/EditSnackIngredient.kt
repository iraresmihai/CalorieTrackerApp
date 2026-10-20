package com.rares.calorietracker

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import kotlinx.coroutines.launch
import java.io.File
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

//temporary file the camera app writes the label photo into
private fun newPhotoUri(context: Context): Uri {
    val dir = File(context.cacheDir, "label_photos").apply { mkdirs() }
    val file = File(dir, "label.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

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

    //nutrition label scanning
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isScanning = remember { mutableStateOf(false) }
    var scanMessage = remember { mutableStateOf<String?>(null) }
    var scanFailed = remember { mutableStateOf(false) }
    var photoUri = remember { mutableStateOf<Uri?>(null) }

    val scanImage: (Uri) -> Unit = { uri ->
        isScanning.value = true
        scanMessage.value = null
        coroutineScope.launch {
            try {
                val values = LabelScanner.scan(context, uri)
                values.calories?.let { newCalories.value = formatWeight(it) }
                values.fat?.let { newFat.value = formatWeight(it) }
                values.carbs?.let { newCarbs.value = formatWeight(it) }
                values.protein?.let { newProtein.value = formatWeight(it) }
                values.salt?.let { newSalt.value = formatWeight(it) }
                newPortionSize.value = "100" //the label values are per 100g
                if (values.name != null && (newName.value.isBlank() || newName.value == meal.initialName)) {
                    newName.value = values.name
                }
                val missing = listOf("calories" to values.calories, "fat" to values.fat, "carbs" to values.carbs, "protein" to values.protein, "salt" to values.salt)
                    .filter { it.second == null }.map { it.first }
                scanFailed.value = false
                scanMessage.value = if (missing.isEmpty()) "Filled in per 100g, check the values." else "Couldn't read: ${missing.joinToString()}. Please fill those in."
            } catch (e: Exception) {
                scanFailed.value = true
                scanMessage.value = (e as? LabelScanException)?.message ?: "Scanning failed: ${e.message}"
            } finally {
                isScanning.value = false
            }
        }
    }
    val takePhoto = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val uri = photoUri.value
        if (success && uri != null) scanImage(uri)
    }
    val pickPhoto = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) scanImage(uri)
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
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    enabled = !isScanning.value,
                    onClick = {
                        val uri = newPhotoUri(context)
                        photoUri.value = uri
                        takePhoto.launch(uri)
                    }
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null)
                    Text(" Scan label")
                }
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    enabled = !isScanning.value,
                    onClick = { pickPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                ) {
                    Icon(Icons.Default.Image, contentDescription = null)
                    Text(" From gallery")
                }
            }
            if (isScanning.value) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Text("Reading label…", style = MaterialTheme.typography.bodyMedium)
                }
            }
            scanMessage.value?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, color = if (scanFailed.value) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
            }
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