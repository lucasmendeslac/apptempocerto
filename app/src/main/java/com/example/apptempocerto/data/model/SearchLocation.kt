package com.example.apptempocerto.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchLocation(
    val id: Long,
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val url: String
) 