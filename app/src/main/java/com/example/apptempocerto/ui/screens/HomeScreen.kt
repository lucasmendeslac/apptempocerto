package com.example.apptempocerto.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.apptempocerto.R
import com.example.apptempocerto.data.model.Condition
import com.example.apptempocerto.data.model.ForecastDay
import com.example.apptempocerto.data.model.Hour
import com.example.apptempocerto.data.model.SearchLocation
import com.example.apptempocerto.ui.components.CityListItem
import com.example.apptempocerto.ui.components.CurrentWeatherCard
import com.example.apptempocerto.ui.components.DailyForecastCard
import com.example.apptempocerto.ui.components.ErrorView
import com.example.apptempocerto.ui.components.HourlyForecastCard
import com.example.apptempocerto.ui.components.LoadingIndicator
import com.example.apptempocerto.ui.components.WeatherDetailsCard
import com.example.apptempocerto.ui.viewmodel.ForecastUIState
import com.example.apptempocerto.ui.viewmodel.SearchUIState
import com.example.apptempocerto.ui.viewmodel.WeatherUIState
import com.example.apptempocerto.ui.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: WeatherViewModel,
    navigateToFavorites: () -> Unit
) {
    val currentWeatherState by viewModel.currentWeatherState.collectAsState()
    val forecastState by viewModel.forecastState.collectAsState()
    val searchState by viewModel.searchState.collectAsState()
    val isCityFavorite by viewModel.isCityFavorite.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            viewModel.searchLocation(searchQuery)
        }
    }
    
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Previsão do Tempo") },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = {
                        viewModel.fetchLocationAndWeather()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_refresh),
                            contentDescription = "Atualizar"
                        )
                    }
                    
                    IconButton(onClick = {
                        if (isCityFavorite) {
                            viewModel.removeCityFromFavorites()
                        } else {
                            viewModel.addCityToFavorites()
                        }
                    }) {
                        Icon(
                            painter = painterResource(
                                id = if (isCityFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
                            ),
                            contentDescription = if (isCityFavorite) "Remover dos favoritos" else "Adicionar aos favoritos"
                        )
                    }
                    
                    IconButton(onClick = navigateToFavorites) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_location),
                            contentDescription = "Favoritos"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Search Bar
            DockedSearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = {
                    isSearchActive = false
                    viewModel.resetSearch()
                },
                active = isSearchActive,
                onActiveChange = { isSearchActive = it },
                placeholder = { Text("Buscar cidade") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "Buscar"
                    )
                }
            ) {
                when (val state = searchState) {
                    is SearchUIState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is SearchUIState.Success -> {
                        state.locations.forEach { location ->
                            CityListItem(
                                name = location.name,
                                region = location.region,
                                country = location.country,
                                onClick = {
                                    viewModel.getWeatherForLocation("${location.lat},${location.lon}")
                                    viewModel.getForecastForLocation("${location.lat},${location.lon}")
                                    searchQuery = ""
                                    isSearchActive = false
                                    viewModel.resetSearch()
                                }
                            )
                        }
                    }
                    is SearchUIState.Empty -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Nenhuma cidade encontrada")
                        }
                    }
                    is SearchUIState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.message,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    else -> { /* Initial state, do nothing */ }
                }
            }
            
            // Content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                when {
                    // Current Weather Loading
                    currentWeatherState is WeatherUIState.Loading && forecastState is ForecastUIState.Loading -> {
                        LoadingIndicator(
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    // Current Weather Error
                    currentWeatherState is WeatherUIState.Error -> {
                        val errorState = currentWeatherState as WeatherUIState.Error
                        ErrorView(
                            message = errorState.message,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    // Success - Show Weather Info
                    currentWeatherState is WeatherUIState.Success -> {
                        val currentWeather = (currentWeatherState as WeatherUIState.Success).data
                        
                        // Obter o fuso horário a partir do tz_id fornecido pela API
                        val timezone = TimeZone.getTimeZone(currentWeather.location.tz_id)
                        
                        // Usar a string de data localizada diretamente da API
                        // Isto já contém o fuso horário correto para a localização
                        val localDate = currentWeather.location.localtime
                        
                        // Mostrar o fuso horário para debug
                        println("DEBUG: Timezone na API: ${currentWeather.location.tz_id}, Data local: ${localDate}")
                        
                        // Formatar a data para exibição em português
                        val apiDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        apiDateFormat.timeZone = timezone
                        
                        val dateFormatter = SimpleDateFormat("EEEE, dd 'de' MMMM", Locale("pt", "BR"))
                        dateFormatter.timeZone = timezone
                        
                        // Converter a string de data da API para Date e então formatar em português
                        val date = try {
                            val parsedDate = apiDateFormat.parse(localDate)
                            // Log da data parseada para depuração
                            println("DEBUG: Data original: $localDate, Data parseada (epoch): ${parsedDate?.time}, Data formatada: ${dateFormatter.format(parsedDate)}")
                            dateFormatter.format(parsedDate)
                        } catch (e: Exception) {
                            println("DEBUG: Erro ao analisar data: ${e.message}")
                            // Fallback: usar timestamp para formato
                            dateFormatter.format(Date(currentWeather.location.localtime_epoch * 1000))
                        }
                        
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            // Current Weather
                            CurrentWeatherCard(
                                temperature = currentWeather.current.temp_c,
                                condition = currentWeather.current.condition,
                                location = "${currentWeather.location.name}, ${currentWeather.location.country}",
                                date = date,
                                feelsLike = currentWeather.current.feelslike_c,
                                humidity = currentWeather.current.humidity,
                                uvIndex = currentWeather.current.uv,
                                airQuality = currentWeather.current.air_quality
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Card com detalhes do clima (novo)
                            WeatherDetailsCard(
                                weather = currentWeather,
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Hourly Forecast
                            if (forecastState is ForecastUIState.Success) {
                                val forecast = (forecastState as ForecastUIState.Success).data
                                
                                // Verificar se temos dados de previsão
                                println("DEBUG: Forecast data: ${forecast.forecast != null}")
                                
                                // Conjunto de horas para exibir (pode ser vazio)
                                var hoursToDisplay = listOf<Hour>()
                                
                                val forecastDays = forecast.forecast?.forecastday
                                println("DEBUG: Número de dias de previsão: ${forecastDays?.size ?: 0}")
                                
                                if (forecastDays != null && forecastDays.isNotEmpty()) {
                                    val firstDay = forecastDays.firstOrNull()
                                    println("DEBUG: Horas no primeiro dia: ${firstDay?.hour?.size ?: 0}")
                                    
                                    if (firstDay != null && firstDay.hour.isNotEmpty()) {
                                        // Calcular a hora atual baseada no fuso horário da localização
                                        val cal = java.util.Calendar.getInstance(timezone)
                                        cal.time = Date(currentWeather.location.localtime_epoch * 1000)
                                        val currentHour = cal.get(java.util.Calendar.HOUR_OF_DAY)
                                        val currentMinute = cal.get(java.util.Calendar.MINUTE)
                                        
                                        // Adicionar log para debug de timezone
                                        println("DEBUG: Hora atual no fuso ${timezone.id}: $currentHour:$currentMinute, Timestamp: ${currentWeather.location.localtime_epoch}")
                                        
                                        // Obter as horas futuras do primeiro dia
                                        var upcomingHours = firstDay.hour.filter {
                                            try {
                                                // Criar um calendário baseado no timestamp da API com o fuso horário correto
                                                val apiCal = java.util.Calendar.getInstance(timezone)
                                                apiCal.time = Date(currentWeather.location.localtime_epoch * 1000)

                                                // Criar um calendário para a hora a ser verificada no mesmo fuso horário
                                                val hourCal = java.util.Calendar.getInstance(timezone)
                                                hourCal.timeInMillis = it.time_epoch * 1000

                                                // Verificar se a hora é futura ou atual em relação ao horário local da localização
                                                hourCal.timeInMillis >= apiCal.timeInMillis
                                            } catch (e: Exception) {
                                                println("DEBUG: Erro ao comparar horas: ${e.message}")
                                                // Fallback: usar timestamp simples
                                                it.time_epoch >= currentWeather.location.localtime_epoch
                                            }
                                        }
                                        
                                        println("DEBUG: Horas futuras encontradas: ${upcomingHours.size}")
                                        
                                        // Se não temos horas suficientes, pegamos algumas do próximo dia
                                        if (upcomingHours.size < 6 && forecastDays.size > 1) {
                                            val nextDay = forecastDays[1]
                                            println("DEBUG: Horas no segundo dia: ${nextDay.hour.size}")
                                            
                                            // Pegamos apenas as horas necessárias do próximo dia
                                            val hoursNeeded = 6 - upcomingHours.size
                                            val nextDayHours = nextDay.hour.take(hoursNeeded)
                                            
                                            // Adicionar log para verificar as horas do próximo dia
                                            nextDayHours.forEach { hour ->
                                                // Obter o calendário da hora da previsão para confirmar o fuso horário
                                                val hourCal = java.util.Calendar.getInstance(timezone)
                                                hourCal.timeInMillis = hour.time_epoch * 1000
                                                val hourStr = hourCal.get(java.util.Calendar.HOUR_OF_DAY)
                                                val minuteStr = hourCal.get(java.util.Calendar.MINUTE)
                                                
                                                println("DEBUG: Próximo dia - hora: ${hour.time}, hora formatada: $hourStr:$minuteStr, epoch: ${hour.time_epoch}")
                                            }
                                            
                                            upcomingHours = upcomingHours + nextDayHours
                                            println("DEBUG: Após adicionar horas do segundo dia: ${upcomingHours.size}")
                                        }
                                        
                                        // Garantir no máximo 6 horas
                                        hoursToDisplay = upcomingHours.take(6)
                                    } else {
                                        println("DEBUG: Não há horas no primeiro dia ou a lista está vazia")
                                    }
                                } else {
                                    println("DEBUG: Não há dias de previsão disponíveis")
                                }
                                
                                // Sempre exibir o card, mesmo que a lista esteja vazia
                                HourlyForecastCard(
                                    hours = if (hoursToDisplay.isEmpty()) createPlaceholderHourlyData(timezone) else hoursToDisplay,
                                    timezone = timezone,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // 7-day forecast (excluindo o dia atual)
                                if (forecastDays != null && forecastDays.size > 1) {
                                    val futureDays = forecastDays.drop(1).take(7)
                                    if (futureDays.isNotEmpty()) {
                                        DailyForecastCard(
                                            forecastDays = futureDays,
                                            timezone = timezone,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                    }
                                }
                            } else if (forecastState is ForecastUIState.Error) {
                                val errorState = forecastState as ForecastUIState.Error
                                ErrorView(
                                    message = errorState.message,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else if (forecastState is ForecastUIState.Loading) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }
}

// Função auxiliar para criar dados de exemplo para previsão horária
private fun createPlaceholderHourlyData(timezone: TimeZone = TimeZone.getDefault()): List<Hour> {
    // Obter o tempo atual no fuso horário especificado
    val calendar = java.util.Calendar.getInstance(timezone)
    
    // Ajustar para o início da hora atual
    calendar.set(java.util.Calendar.MINUTE, 0)
    calendar.set(java.util.Calendar.SECOND, 0)
    calendar.set(java.util.Calendar.MILLISECOND, 0)
    
    val currentTime = calendar.timeInMillis
    val oneHourMillis = 60 * 60 * 1000L
    
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    dateFormat.timeZone = timezone
    
    return List(6) { index ->
        // Começar com a hora atual e adicionar 1 hora por elemento
        val timeEpoch = currentTime + (index * oneHourMillis)
        val timeString = dateFormat.format(Date(timeEpoch))
        
        Hour(
            time_epoch = timeEpoch / 1000,
            time = timeString,
            temp_c = 20.0 + index,
            temp_f = 68.0 + (index * 1.8),
            is_day = 1,
            condition = Condition(
                text = "Partly cloudy",
                icon = "//cdn.weatherapi.com/weather/64x64/day/116.png",
                code = 1003
            ),
            wind_mph = 5.6,
            wind_kph = 9.0,
            wind_degree = 220,
            wind_dir = "SW",
            pressure_mb = 1012.0,
            pressure_in = 29.88,
            precip_mm = 0.0,
            precip_in = 0.0,
            humidity = 70 - (index * 3),
            cloud = 25,
            feelslike_c = 21.0 + index,
            feelslike_f = 69.8 + (index * 1.8),
            windchill_c = 21.0 + index,
            windchill_f = 69.8 + (index * 1.8),
            heatindex_c = 21.0 + index,
            heatindex_f = 69.8 + (index * 1.8),
            dewpoint_c = 14.8,
            dewpoint_f = 58.6,
            will_it_rain = 0,
            chance_of_rain = 10 + (index * 5),
            will_it_snow = 0,
            chance_of_snow = 0,
            vis_km = 10.0,
            vis_miles = 6.0,
            gust_mph = 8.1,
            gust_kph = 13.0,
            uv = 5.0
        )
    }
} 