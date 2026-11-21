package com.mytum.model.places

data class PlacesTextSearchPojo(
    val includedType: String?,
    val textQuery: String,
    val maxResultCount: Int?,
    val rankPreference: String?,
    val locationBias: LocationRestriction?,
)