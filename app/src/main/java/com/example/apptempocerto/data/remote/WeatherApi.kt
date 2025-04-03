package com.example.apptempocerto.data.remote

import com.example.apptempocerto.data.model.SearchLocation
import com.example.apptempocerto.data.model.WeatherResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class WeatherApi(private val client: HttpClient) {
    
    companion object {
        private const val BASE_URL = "https://api.weatherapi.com/v1"
        private const val API_KEY = "5dc2e8cadffe4eebb24193425252403"
    }
    
    suspend fun getCurrentWeather(location: String): WeatherResponse {
        return client.get("$BASE_URL/current.json") {
            parameter("key", API_KEY)
            parameter("q", location)
            parameter("aqi", "yes")
        }.body()
    }
    
    suspend fun getForecast(location: String, days: Int = 7): WeatherResponse {
        return client.get("$BASE_URL/forecast.json") {
            parameter("key", API_KEY)
            parameter("q", location)
            parameter("days", days)
            parameter("aqi", "yes")
            parameter("alerts", "no")
        }.body()
    }
    
    suspend fun searchLocation(query: String): List<SearchLocation> {
        return client.get("$BASE_URL/search.json") {
            parameter("key", API_KEY)
            parameter("q", query)
        }.body()
    }
} 