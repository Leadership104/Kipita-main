package com.mytum.model.places

data class AccessibilityOptions(
    val wheelchairAccessibleEntrance: Boolean,
    val wheelchairAccessibleParking: Boolean,
    val wheelchairAccessibleRestroom: Boolean,
    val wheelchairAccessibleSeating: Boolean
)