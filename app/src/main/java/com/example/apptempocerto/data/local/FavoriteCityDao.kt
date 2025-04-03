package com.example.apptempocerto.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCityDao {
    @Query("SELECT * FROM favorite_cities ORDER BY timestamp DESC")
    fun getAllFavoriteCities(): Flow<List<FavoriteCity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavoriteCity(city: FavoriteCity)

    @Delete
    suspend fun removeFavoriteCity(city: FavoriteCity)

    @Query("SELECT * FROM favorite_cities WHERE name = :cityName LIMIT 1")
    suspend fun getFavoriteCityByName(cityName: String): FavoriteCity?

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_cities WHERE name = :cityName LIMIT 1)")
    fun isCityFavorite(cityName: String): Flow<Boolean>
} 