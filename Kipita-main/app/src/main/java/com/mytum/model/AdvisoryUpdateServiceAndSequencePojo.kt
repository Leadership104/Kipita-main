package com.mytum.model

data class AdvisoryUpdateServiceAndSequencePojo(
    val action: String,
    val country: String,
    val sections: ArrayList<Section>,
    val timeZone: String,
    val userId: String,
    val zipCode: String
)
