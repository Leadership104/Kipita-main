package com.mytum.model.places

data class RegularOpeningHours(
    val openNow: Boolean,
    val periods: List<Period>,
    val weekdayDescriptions: List<String>
)