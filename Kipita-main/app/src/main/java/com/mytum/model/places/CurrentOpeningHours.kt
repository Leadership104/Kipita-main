package com.mytum.model.places

data class CurrentOpeningHours(
    val openNow: Boolean,
    val periods: List<Period>,
    val weekdayDescriptions: List<String>
)