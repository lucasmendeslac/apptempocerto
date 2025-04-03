package com.example.apptempocerto.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptempocerto.data.local.FavoriteCity
import com.example.apptempocerto.data.location.LocationService
import com.example.apptempocerto.data.model.SearchLocation
import com.example.apptempocerto.data.model.WeatherResponse
import com.example.apptempocerto.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository,
    private val locationService: LocationService
) : ViewModel() {
    
    private val _currentWeatherState = MutableStateFlow<WeatherUIState>(WeatherUIState.Loading)
    val currentWeatherState: StateFlow<WeatherUIState> = _currentWeatherState.asStateFlow()
    
    private val _forecastState = MutableStateFlow<ForecastUIState>(ForecastUIState.Loading)
    val forecastState: StateFlow<ForecastUIState> = _forecastState.asStateFlow()
    
    private val _searchState = MutableStateFlow<SearchUIState>(SearchUIState.Initial)
    val searchState: StateFlow<SearchUIState> = _searchState.asStateFlow()
    
    private val _favoriteCities = MutableStateFlow<List<FavoriteCity>>(emptyList())
    val favoriteCities: StateFlow<List<FavoriteCity>> = _favoriteCities.asStateFlow()
    
    private val _isCityFavorite = MutableStateFlow(false)
    val isCityFavorite: StateFlow<Boolean> = _isCityFavorite.asStateFlow()
    
    private var currentLocation: String? = null
    
    init {
        viewModelScope.launch {
            repository.getAllFavoriteCities().collect { cities ->
                _favoriteCities.value = cities
            }
        }
        fetchLocationAndWeather()
    }
    
    fun fetchLocationAndWeather() {
        viewModelScope.launch {
            _currentWeatherState.value = WeatherUIState.Loading
            _forecastState.value = ForecastUIState.Loading
            
            try {
                val coordinates = locationService.getLocationCoordinates()
                if (coordinates != null) {
                    val (lat, lon) = coordinates
                    val locationString = "$lat,$lon"
                    currentLocation = locationString
                    getWeatherForLocation(locationString)
                    getForecastForLocation(locationString)
                    checkIfCityIsFavorite()
                } else {
                    // Localização não disponível, usar uma cidade padrão
                    getWeatherForLocation("São Paulo")
                    getForecastForLocation("São Paulo")
                    currentLocation = "São Paulo"
                    checkIfCityIsFavorite()
                }
            } catch (e: Exception) {
                _currentWeatherState.value = WeatherUIState.Error("Erro ao obter localização: ${e.message}")
                _forecastState.value = ForecastUIState.Error("Erro ao obter previsão: ${e.message}")
            }
        }
    }
    
    fun getWeatherForLocation(location: String) {
        viewModelScope.launch {
            _currentWeatherState.value = WeatherUIState.Loading
            
            try {
                val response = repository.getCurrentWeather(location)
                _currentWeatherState.value = WeatherUIState.Success(response)
                currentLocation = location
                checkIfCityIsFavorite()
            } catch (e: Exception) {
                _currentWeatherState.value = WeatherUIState.Error("Erro ao obter clima: ${e.message}")
            }
        }
    }
    
    fun getForecastForLocation(location: String) {
        viewModelScope.launch {
            _forecastState.value = ForecastUIState.Loading
            
            try {
                val response = repository.getForecast(location)
                _forecastState.value = ForecastUIState.Success(response)
            } catch (e: Exception) {
                _forecastState.value = ForecastUIState.Error("Erro ao obter previsão: ${e.message}")
            }
        }
    }
    
    fun searchLocation(query: String) {
        if (query.length < 2) {
            _searchState.value = SearchUIState.Initial
            return
        }
        
        viewModelScope.launch {
            _searchState.value = SearchUIState.Loading
            
            try {
                val locations = repository.searchLocation(query)
                _searchState.value = if (locations.isEmpty()) {
                    SearchUIState.Empty
                } else {
                    SearchUIState.Success(locations)
                }
            } catch (e: Exception) {
                _searchState.value = SearchUIState.Error("Erro na busca: ${e.message}")
            }
        }
    }
    
    fun resetSearch() {
        _searchState.value = SearchUIState.Initial
    }
    
    fun addCityToFavorites() {
        viewModelScope.launch {
            val weatherState = _currentWeatherState.value
            if (weatherState is WeatherUIState.Success) {
                val weather = weatherState.data
                val city = FavoriteCity(
                    name = weather.location.name,
                    region = weather.location.region,
                    country = weather.location.country,
                    lat = weather.location.lat,
                    lon = weather.location.lon
                )
                repository.addFavoriteCity(city)
                _isCityFavorite.value = true
            }
        }
    }
    
    fun removeCityFromFavorites() {
        viewModelScope.launch {
            val weatherState = _currentWeatherState.value
            if (weatherState is WeatherUIState.Success) {
                val weather = weatherState.data
                val city = repository.getFavoriteCityByName(weather.location.name)
                city?.let {
                    repository.removeFavoriteCity(it)
                    _isCityFavorite.value = false
                }
            }
        }
    }
    
    private fun checkIfCityIsFavorite() {
        viewModelScope.launch {
            val weatherState = _currentWeatherState.value
            if (weatherState is WeatherUIState.Success) {
                repository.isCityFavorite(weatherState.data.location.name).collect { isFavorite ->
                    _isCityFavorite.value = isFavorite
                }
            }
        }
    }
}

sealed class WeatherUIState {
    data object Loading : WeatherUIState()
    data class Success(val data: WeatherResponse) : WeatherUIState()
    data class Error(val message: String) : WeatherUIState()
}

sealed class ForecastUIState {
    data object Loading : ForecastUIState()
    data class Success(val data: WeatherResponse) : ForecastUIState()
    data class Error(val message: String) : ForecastUIState()
}

sealed class SearchUIState {
    data object Initial : SearchUIState()
    data object Loading : SearchUIState()
    data object Empty : SearchUIState()
    data class Success(val locations: List<SearchLocation>) : SearchUIState()
    data class Error(val message: String) : SearchUIState()
} 