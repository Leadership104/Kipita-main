package com.mytum.model.places

data class PlaceCategorySearchPojo(
    val includedTypes: List<String>,
    val maxResultCount: Int,
    val rankPreference: String,
    val locationRestriction: LocationRestriction,
)