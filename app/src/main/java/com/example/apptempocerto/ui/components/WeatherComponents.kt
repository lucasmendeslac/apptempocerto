package com.example.apptempocerto.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.apptempocerto.R
import com.example.apptempocerto.data.model.AirQuality
import com.example.apptempocerto.data.model.Condition
import com.example.apptempocerto.data.model.ForecastDay
import com.example.apptempocerto.data.model.Hour
import com.example.apptempocerto.data.model.WeatherResponse
import com.example.apptempocerto.ui.theme.ApptempocertoTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext


@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ErrorView(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_error),
            contentDescription = "Erro",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}

// Função para traduzir as condições climáticas
fun traduzirCondicao(condicao: String): String {
    return when (condicao.lowercase()) {
        "clear" -> "Céu Limpo"
        "sunny" -> "Ensolarado"
        "partly cloudy" -> "Parcialmente Nublado"
        "cloudy" -> "Nublado"
        "overcast" -> "Encoberto"
        "mist" -> "Névoa"
        "fog" -> "Neblina"
        "freezing fog" -> "Neblina Congelante"
        "patchy rain possible" -> "Possibilidade de Chuva Irregular"
        "patchy snow possible" -> "Possibilidade de Neve Irregular"
        "patchy sleet possible" -> "Possibilidade de Granizo Irregular"
        "patchy freezing drizzle possible" -> "Possibilidade de Garoa Congelante Irregular"
        "thundery outbreaks possible" -> "Possibilidade de Trovoadas"
        "blowing snow" -> "Neve com Vento"
        "blizzard" -> "Nevasca"
        "rain" -> "Chuva"
        "moderate rain" -> "Chuva Moderada"
        "heavy rain" -> "Chuva Forte"
        "freezing rain" -> "Chuva Congelante"
        "light rain" -> "Chuva Leve"
        "light drizzle" -> "Garoa Leve"
        "moderate or heavy rain with thunder" -> "Chuva Moderada ou Forte com Trovoadas"
        "patchy light rain with thunder" -> "Chuva Leve Irregular com Trovoadas"
        "moderate or heavy showers of ice pellets" -> "Chuva Moderada ou Forte de Granizo"
        "light showers of ice pellets" -> "Chuva Leve de Granizo"
        "moderate or heavy snow with thunder" -> "Neve Moderada ou Forte com Trovoadas"
        "patchy light snow with thunder" -> "Neve Leve Irregular com Trovoadas"
        "patchy light rain" -> "Chuva Leve Irregular"
        "patchy moderate rain" -> "Chuva Moderada Irregular"
        "patchy heavy rain" -> "Chuva Forte Irregular"
        "patchy light snow" -> "Neve Leve Irregular"
        "patchy moderate snow" -> "Neve Moderada Irregular"
        "patchy heavy snow" -> "Neve Forte Irregular"
        else -> condicao
    }
}

// Função para avaliar a qualidade do ar
fun avaliarQualidadeDoAr(indice: Int): Pair<String, Color> {
    return when (indice) {
        1 -> Pair("Boa", Color(0xFF4CAF50)) // Verde
        2 -> Pair("Moderada", Color(0xFFFFEB3B)) // Amarelo
        3 -> Pair("Insalubre para Grupos Sensíveis", Color(0xFFFF9800)) // Laranja
        4 -> Pair("Insalubre", Color(0xFFFF5722)) // Vermelho claro
        5 -> Pair("Muito Insalubre", Color(0xFFE91E63)) // Rosa/Roxo
        else -> Pair("Perigosa", Color(0xFF9C27B0)) // Roxo escuro
    }
}

@Composable
fun CurrentWeatherCard(
    temperature: Double,
    condition: Condition,
    location: String,
    date: String,
    feelsLike: Double,
    humidity: Int,
    uvIndex: Double,
    airQuality: AirQuality?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = location,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = date,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = "https:${condition.icon}",
                    contentDescription = condition.text,
                    modifier = Modifier.size(80.dp)
                )
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${temperature.toInt()}°C",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = traduzirCondicao(condition.text),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherInfoItem(
                    icon = R.drawable.ic_temperature,
                    label = "Sensação",
                    value = "${feelsLike.toInt()}°C"
                )
                
                WeatherInfoItem(
                    icon = R.drawable.ic_humidity,
                    label = "Umidade",
                    value = "$humidity%"
                )
                
                WeatherInfoItem(
                    icon = R.drawable.ic_uv,
                    label = "UV",
                    value = uvIndex.toString()
                )
            }
            
            if (airQuality != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Qualidade do Ar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                val qualidadeDoAr = avaliarQualidadeDoAr(airQuality.us_epa_index)
                Text(
                    text = qualidadeDoAr.first,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = qualidadeDoAr.second
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AirQualityInfoItem(label = "PM2.5", value = String.format("%.1f", airQuality.pm2_5))
                    AirQualityInfoItem(label = "PM10", value = String.format("%.1f", airQuality.pm10))
                    AirQualityInfoItem(label = "CO", value = String.format("%.1f", airQuality.co))
                    AirQualityInfoItem(label = "NO2", value = String.format("%.1f", airQuality.no2))
                }
            }
        }
    }
}

@Composable
fun WeatherInfoItem(
    icon: Int,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AirQualityInfoItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun HourlyForecastCard(
    hours: List<Hour>,
    modifier: Modifier = Modifier
) {
    // Vamos exibir o card mesmo se não houver horas disponíveis
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(280.dp), // altura fixa para garantir visibilidade
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp), // Aumentar sombra para destacar
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f) // Fundo mais distinto
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PREVISÃO POR HORA",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f)
                )
                
                Icon(
                    painter = painterResource(id = R.drawable.ic_time),
                    contentDescription = "Previsão Horária",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Divider(
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f),
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            if (hours.isEmpty()) {
                // Exibir uma mensagem ou um estado vazio quando não houver dados
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_time),
                            contentDescription = "Sem dados",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier.size(40.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Nenhuma previsão disponível",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                // Exibir as horas disponíveis
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp),
                    userScrollEnabled = true
                ) {
                    items(hours) { hour ->
                        Card(
                            modifier = Modifier
                                .width(110.dp)
                                .height(180.dp),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            HourForecastItem(hour = hour)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HourForecastItem(hour: Hour, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .width(100.dp)
    ) {
        // Formatação da hora (apenas a hora, sem a data)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = Date(hour.time_epoch * 1000)
        val formattedTime = timeFormat.format(date)
        
        Text(
            text = formattedTime,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Ícone da condição
        val iconUrl = "https:${hour.condition.icon}"
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(iconUrl)
                .crossfade(true)
                .build(),
            contentDescription = hour.condition.text,
            modifier = Modifier.size(48.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Temperatura
        Text(
            text = "${hour.temp_c.toInt()}°C",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Condição traduzida
        Text(
            text = translateCondition(hour.condition.text),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        
        // Probabilidade de chuva (se > 0%)
        if (hour.chance_of_rain > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_rain),
                    contentDescription = "Chuva",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${hour.chance_of_rain}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// Função para traduzir condições para português
private fun translateCondition(condition: String): String {
    return when (condition.lowercase()) {
        "sunny" -> "Ensolarado"
        "partly cloudy" -> "Parcialmente nublado"
        "cloudy" -> "Nublado"
        "overcast" -> "Encoberto"
        "mist" -> "Névoa"
        "patchy rain possible" -> "Possível chuva isolada"
        "patchy snow possible" -> "Possível neve isolada"
        "patchy sleet possible" -> "Possível granizo isolado"
        "patchy freezing drizzle possible" -> "Possível garoa congelante"
        "thundery outbreaks possible" -> "Possíveis trovoadas"
        "blowing snow" -> "Rajadas de neve"
        "blizzard" -> "Nevasca"
        "fog" -> "Nevoeiro"
        "freezing fog" -> "Nevoeiro congelante"
        "patchy light drizzle" -> "Garoa leve isolada"
        "light drizzle" -> "Garoa leve"
        "freezing drizzle" -> "Garoa congelante"
        "heavy freezing drizzle" -> "Garoa congelante forte"
        "patchy light rain" -> "Chuva leve isolada"
        "light rain" -> "Chuva leve"
        "moderate rain at times" -> "Chuva moderada às vezes"
        "moderate rain" -> "Chuva moderada"
        "heavy rain at times" -> "Chuva forte às vezes"
        "heavy rain" -> "Chuva forte"
        "light freezing rain" -> "Chuva congelante leve"
        "moderate or heavy freezing rain" -> "Chuva congelante moderada a forte"
        "light sleet" -> "Granizo leve"
        "moderate or heavy sleet" -> "Granizo moderado a forte"
        "patchy light snow" -> "Neve leve isolada"
        "light snow" -> "Neve leve"
        "patchy moderate snow" -> "Neve moderada isolada"
        "moderate snow" -> "Neve moderada"
        "patchy heavy snow" -> "Neve forte isolada"
        "heavy snow" -> "Neve forte"
        "ice pellets" -> "Pelotas de gelo"
        "light rain shower" -> "Pancada de chuva leve"
        "moderate or heavy rain shower" -> "Pancada de chuva moderada a forte"
        "torrential rain shower" -> "Pancada de chuva torrencial"
        "light sleet showers" -> "Pancada de granizo leve"
        "moderate or heavy sleet showers" -> "Pancada de granizo moderada a forte"
        "light snow showers" -> "Pancada de neve leve"
        "moderate or heavy snow showers" -> "Pancada de neve moderada a forte"
        "light showers of ice pellets" -> "Pancada leve de pelotas de gelo"
        "moderate or heavy showers of ice pellets" -> "Pancada moderada a forte de pelotas de gelo"
        "patchy light rain with thunder" -> "Chuva leve isolada com trovoadas"
        "moderate or heavy rain with thunder" -> "Chuva moderada a forte com trovoadas"
        "patchy light snow with thunder" -> "Neve leve isolada com trovoadas"
        "moderate or heavy snow with thunder" -> "Neve moderada a forte com trovoadas"
        "clear" -> "Céu limpo"
        else -> condition
    }
}

@Composable
fun DailyForecastCard(
    forecastDays: List<ForecastDay>,
    timezone: TimeZone = TimeZone.getDefault(),
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Previsão para 7 Dias",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Pular o primeiro dia (hoje) e pegar os próximos 7 dias
            val diasFuturos = forecastDays.take(7)
            
            diasFuturos.forEach { forecastDay ->
                DailyForecastItem(
                    forecastDay = forecastDay,
                    timezone = timezone
                )
                if (forecastDay != diasFuturos.last()) {
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@Composable
fun DailyForecastItem(
    forecastDay: ForecastDay,
    timezone: TimeZone = TimeZone.getDefault(),
    modifier: Modifier = Modifier
) {
    val dateFormatter = SimpleDateFormat("EEE, dd/MM", Locale("pt", "BR"))
    dateFormatter.timeZone = timezone
    val date = dateFormatter.format(Date(forecastDay.date_epoch * 1000))
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = date,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        
        AsyncImage(
            model = "https:${forecastDay.day.condition.icon}",
            contentDescription = forecastDay.day.condition.text,
            modifier = Modifier.size(40.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "${forecastDay.day.maxtemp_c.toInt()}°C / ${forecastDay.day.mintemp_c.toInt()}°C",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = traduzirCondicao(forecastDay.day.condition.text),
                style = MaterialTheme.typography.bodySmall,
            )
            
            if (forecastDay.day.daily_chance_of_rain > 0) {
                Text(
                    text = "Chuva: ${forecastDay.day.daily_chance_of_rain}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (forecastDay.day.daily_chance_of_rain > 50) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            Text(
                text = "Umidade: ${forecastDay.day.avghumidity.toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun CityListItem(
    name: String,
    region: String,
    country: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_location),
            contentDescription = "Localização",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            
            if (region.isNotEmpty() && country.isNotEmpty()) {
                Text(
                    text = "$region, $country",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun WeatherDetailsCard(weather: WeatherResponse, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Detalhes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(modifier = Modifier.fillMaxWidth()) {
                // Coluna 1
                Column(modifier = Modifier.weight(1f)) {
                    WeatherDetailItem(
                        icon = R.drawable.ic_thermometer,
                        title = "Sensação",
                        value = "${weather.current.feelslike_c.toInt()}°C"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    WeatherDetailItem(
                        icon = R.drawable.ic_humidity,
                        title = "Umidade",
                        value = "${weather.current.humidity}%"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    WeatherDetailItem(
                        icon = R.drawable.ic_rain,
                        title = "Precipitação",
                        value = "${weather.current.precip_mm} mm"
                    )
                }
                
                // Coluna 2
                Column(modifier = Modifier.weight(1f)) {
                    WeatherDetailItem(
                        icon = R.drawable.ic_wind,
                        title = "Vento",
                        value = "${weather.current.wind_kph} km/h"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    WeatherDetailItem(
                        icon = R.drawable.ic_pressure,
                        title = "Pressão",
                        value = "${weather.current.pressure_mb} mb"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    WeatherDetailItem(
                        icon = R.drawable.ic_uv,
                        title = "Índice UV",
                        value = "${weather.current.uv.toInt()}"
                    )
                }
            }
            
            // Qualidade do ar (se disponível)
            weather.current.air_quality?.let { airQuality ->
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
                AirQualityIndicator(airQuality = airQuality)
            }
        }
    }
}

@Composable
fun AirQualityIndicator(airQuality: AirQuality, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Qualidade do Ar",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Calcular o nível de qualidade do ar baseado no US EPA Index
        val aqiLevel = when {
            airQuality.us_epa_index <= 1 -> AirQualityLevel.GOOD
            airQuality.us_epa_index <= 2 -> AirQualityLevel.MODERATE
            airQuality.us_epa_index <= 3 -> AirQualityLevel.UNHEALTHY_SENSITIVE
            airQuality.us_epa_index <= 4 -> AirQualityLevel.UNHEALTHY
            airQuality.us_epa_index <= 5 -> AirQualityLevel.VERY_UNHEALTHY
            else -> AirQualityLevel.HAZARDOUS
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            // Indicador visual da qualidade do ar
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(aqiLevel.color, shape = CircleShape)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = aqiLevel.label,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = aqiLevel.color
                )
                
                Text(
                    text = aqiLevel.description,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        
        // Detalhes específicos dos poluentes
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            // Coluna 1
            Column(modifier = Modifier.weight(1f)) {
                AirQualityDetailItem(
                    title = "CO",
                    value = String.format("%.1f", airQuality.co)
                )
                AirQualityDetailItem(
                    title = "O₃",
                    value = String.format("%.1f µg/m³", airQuality.o3)
                )
            }
            
            // Coluna 2
            Column(modifier = Modifier.weight(1f)) {
                AirQualityDetailItem(
                    title = "NO₂",
                    value = String.format("%.1f µg/m³", airQuality.no2)
                )
                AirQualityDetailItem(
                    title = "PM2.5",
                    value = String.format("%.1f µg/m³", airQuality.pm2_5)
                )
            }
        }
    }
}

@Composable
fun AirQualityDetailItem(title: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(50.dp)
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun WeatherDetailItem(
    icon: Int,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

enum class AirQualityLevel(
    val label: String,
    val description: String,
    val color: Color
) {
    GOOD(
        label = "Boa",
        description = "Qualidade do ar considerada satisfatória",
        color = Color(0xFF4CAF50) // Verde
    ),
    MODERATE(
        label = "Moderada",
        description = "Qualidade do ar aceitável",
        color = Color(0xFFFFEB3B) // Amarelo
    ),
    UNHEALTHY_SENSITIVE(
        label = "Insalubre para Sensíveis",
        description = "Membros de grupos sensíveis podem sofrer efeitos na saúde",
        color = Color(0xFFFF9800) // Laranja
    ),
    UNHEALTHY(
        label = "Insalubre",
        description = "Todos podem começar a sentir efeitos na saúde",
        color = Color(0xFFF44336) // Vermelho
    ),
    VERY_UNHEALTHY(
        label = "Muito Insalubre",
        description = "Alerta de saúde: todos podem experimentar efeitos graves",
        color = Color(0xFF9C27B0) // Roxo
    ),
    HAZARDOUS(
        label = "Perigosa",
        description = "Alerta de saúde: todos podem sofrer efeitos graves",
        color = Color(0xFF7E0023) // Vermelho escuro
    )
} 