package com.mytum.model.places

data class AddressComponent(
    val languageCode: String,
    val longText: String,
    val shortText: String,
    val types: List<String>
)