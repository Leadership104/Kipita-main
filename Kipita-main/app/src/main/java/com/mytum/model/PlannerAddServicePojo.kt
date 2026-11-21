package com.mytum.model

data class PlannerAddServicePojo(
    val action: String,
    val country: String,
    val timeZone: String,
    val userId: String,
    val zipCode: String,
    val serviceName: String,
    val serviceURL: String,
    val id: Int,
)
