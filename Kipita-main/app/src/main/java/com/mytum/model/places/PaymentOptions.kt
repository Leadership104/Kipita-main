package com.mytum.model.places

data class PaymentOptions(
    val acceptsCashOnly: Boolean,
    val acceptsCreditCards: Boolean,
    val acceptsDebitCards: Boolean,
    val acceptsNfc: Boolean
)