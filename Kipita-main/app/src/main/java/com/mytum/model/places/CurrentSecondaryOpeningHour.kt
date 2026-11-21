package com.mytum.model.places

data class CurrentSecondaryOpeningHour(
    val openNow: Boolean,
    val periods: List<Period>,
    val secondaryHoursType: String,
    val weekdayDescriptions: List<String>
)