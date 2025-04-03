package com.example.apptempocerto.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_cities")
data class FavoriteCity(
    @PrimaryKey
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val timestamp: Long = System.currentTimeMillis()
) 