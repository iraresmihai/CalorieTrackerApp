package com.rares.calorietracker.data

import androidx.compose.runtime.mutableStateListOf

class TypeOfTraining (var id: String = "") {
    val allTraining = mutableStateListOf<String>()
    var targetCalories = 0

    fun addTraining(name: String, pos: Int) {
        if ( pos == -1 ) {
            allTraining.add(name)
        } else {
            if ( 0 <= pos && pos < allTraining.size ) {
                allTraining.add(pos, name)
            }
        }
    }

    fun delTraining(pos: Int) {
        if ( 0 <= pos && pos < allTraining.size )
            allTraining.removeAt(pos)
    }

    fun delTraining(name: String) {
        allTraining.remove(name)
    }
}