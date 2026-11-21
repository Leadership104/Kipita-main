package com.mytum.model.places

data class Review(
    val authorAttribution: AuthorAttribution,
    val name: String,
    val originalText: OriginalText,
    val publishTime: String,
    val rating: Float,
    val relativePublishTimeDescription: String,
    val text: Text
)