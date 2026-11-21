package com.mytum.model

data class RestaurantUpdateServiceAndSequencePojo(
    val action: String,
    val country: String,
    val parentType: String,
    val sections: ArrayList<Section>,
    val timeZone: String,
    val userId: String,
    val zipCode: String
)
