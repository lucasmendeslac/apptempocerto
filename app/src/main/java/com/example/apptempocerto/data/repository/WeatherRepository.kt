package com.example.apptempocerto.data.repository

import com.example.apptempocerto.data.local.FavoriteCity
import com.example.apptempocerto.data.local.FavoriteCityDao
import com.example.apptempocerto.data.model.SearchLocation
import com.example.apptempocerto.data.model.WeatherResponse
import com.example.apptempocerto.data.remote.WeatherApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WeatherRepository(
    private val weatherApi: WeatherApi,
    private val favoriteCityDao: FavoriteCityDao
) {
    // Dados do clima
    suspend fun getCurrentWeather(location: String): WeatherResponse {
        return withContext(Dispatchers.IO) {
            weatherApi.getCurrentWeather(location)
        }
    }
    
    suspend fun getForecast(location: String, days: Int = 7): WeatherResponse {
        return withContext(Dispatchers.IO) {
            weatherApi.getForecast(location, days)
        }
    }
    
    suspend fun searchLocation(query: String): List<SearchLocation> {
        return withContext(Dispatchers.IO) {
            weatherApi.searchLocation(query)
        }
    }
    
    // Cidades favoritas
    fun getAllFavoriteCities(): Flow<List<FavoriteCity>> {
        return favoriteCityDao.getAllFavoriteCities()
    }
    
    suspend fun addFavoriteCity(city: FavoriteCity) {
        withContext(Dispatchers.IO) {
            favoriteCityDao.addFavoriteCity(city)
        }
    }
    
    suspend fun removeFavoriteCity(city: FavoriteCity) {
        withContext(Dispatchers.IO) {
            favoriteCityDao.removeFavoriteCity(city)
        }
    }
    
    suspend fun getFavoriteCityByName(cityName: String): FavoriteCity? {
        return withContext(Dispatchers.IO) {
            favoriteCityDao.getFavoriteCityByName(cityName)
        }
    }
    
    fun isCityFavorite(cityName: String): Flow<Boolean> {
        return favoriteCityDao.isCityFavorite(cityName)
    }
} 