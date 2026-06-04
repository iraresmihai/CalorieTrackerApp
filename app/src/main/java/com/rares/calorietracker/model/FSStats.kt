package com.rares.calorietracker.model

import com.rares.calorietracker.data.StatInfo

data class FSStats (
    val id: String = "",
    val history: Map<String, StatInfo> = emptyMap()
) {
}