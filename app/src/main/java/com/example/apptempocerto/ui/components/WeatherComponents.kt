package com.example.apptempocerto.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.Brush
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
import java.util.Calendar


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
        "partly cloudy" -> "Pouco Nublado"
        "cloudy" -> "Nublado"
        "overcast" -> "Encoberto"
        "mist" -> "Névoa"
        "fog" -> "Neblina"
        "freezing fog" -> "Neblina Congelante"
        "patchy rain possible" -> "Possível Chuva"
        "patchy snow possible" -> "Possível Neve"
        "patchy sleet possible" -> "Possível Granizo"
        "patchy freezing drizzle possible" -> "Possível Garoa Gelada"
        "thundery outbreaks possible" -> "Possíveis Trovoadas"
        "blowing snow" -> "Neve com Vento"
        "blizzard" -> "Nevasca"
        "rain" -> "Chuva"
        "moderate rain" -> "Chuva Moderada"
        "heavy rain" -> "Chuva Forte"
        "freezing rain" -> "Chuva Congelante"
        "light rain" -> "Chuva Leve"
        "light drizzle" -> "Garoa Leve"
        "moderate or heavy rain with thunder" -> "Chuva com Trovoadas"
        "patchy light rain with thunder" -> "Chuva Leve com Trovoadas"
        "moderate or heavy showers of ice pellets" -> "Granizo Forte"
        "light showers of ice pellets" -> "Granizo Leve"
        "moderate or heavy snow with thunder" -> "Neve com Trovoadas"
        "patchy light snow with thunder" -> "Neve Leve com Trovoadas"
        "patchy light rain" -> "Chuva Leve Isolada"
        "patchy moderate rain" -> "Chuva Moderada Isolada"
        "patchy heavy rain" -> "Chuva Forte Isolada"
        "patchy light snow" -> "Neve Leve Isolada"
        "patchy moderate snow" -> "Neve Moderada Isolada"
        "patchy heavy snow" -> "Neve Forte Isolada"
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
    ) {
        // Gradiente de fundo baseado na condição climática
        val gradientColors = getGradientColorsForCondition(condition.text)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = gradientColors
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = location,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
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
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = traduzirCondicao(condition.text),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.White.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WeatherInfoItem(
                        icon = R.drawable.ic_temperature,
                        label = "Sensação",
                        value = "${feelsLike.toInt()}°C",
                        tintColor = Color.White
                    )
                    
                    WeatherInfoItem(
                        icon = R.drawable.ic_humidity,
                        label = "Umidade",
                        value = "$humidity%",
                        tintColor = Color.White
                    )
                    
                    WeatherInfoItem(
                        icon = R.drawable.ic_uv,
                        label = "UV",
                        value = uvIndex.toString(),
                        tintColor = Color.White
                    )
                }
                
                if (airQuality != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.White.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Qualidade do Ar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
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
                        AirQualityInfoItem(
                            label = "PM2.5", 
                            value = String.format("%.1f", airQuality.pm2_5),
                            textColor = Color.White
                        )
                        AirQualityInfoItem(
                            label = "PM10", 
                            value = String.format("%.1f", airQuality.pm10),
                            textColor = Color.White
                        )
                        AirQualityInfoItem(
                            label = "CO", 
                            value = String.format("%.1f", airQuality.co),
                            textColor = Color.White
                        )
                        AirQualityInfoItem(
                            label = "NO2", 
                            value = String.format("%.1f", airQuality.no2),
                            textColor = Color.White
                        )
                    }
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
    tintColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = tintColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = tintColor.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = tintColor
        )
    }
}

@Composable
fun AirQualityInfoItem(
    label: String,
    value: String,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = textColor.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun HourlyForecastCard(
    hours: List<Hour>,
    timezone: TimeZone = TimeZone.getDefault(),
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
    ) {
        // Gradiente de fundo para o card de previsão horária
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    )
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
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Icon(
                        painter = painterResource(id = R.drawable.ic_time),
                        contentDescription = "Previsão Horária",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                Divider(
                    color = Color.White.copy(alpha = 0.3f),
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
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(40.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Text(
                                text = "Nenhuma previsão disponível",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.7f)
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
                                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                                )
                            ) {
                                HourForecastItem(
                                    hour = hour,
                                    timezone = timezone
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HourForecastItem(
    hour: Hour, 
    timezone: TimeZone = TimeZone.getDefault(),
    modifier: Modifier = Modifier
) {
    // Obter o calendário da hora da previsão usando o timestamp da previsão com o fuso horário correto
    val forecastCalendar = Calendar.getInstance(timezone)
    forecastCalendar.timeInMillis = hour.time_epoch * 1000
    
    // Formatação de hora - usar Locale específico para o idioma português
    val timeFormat = SimpleDateFormat("HH:mm", Locale("pt", "BR"))
    timeFormat.timeZone = timezone
    val formattedTime = timeFormat.format(forecastCalendar.time)
    
    // Verificar se é um dia diferente do atual
    val currentCalendar = Calendar.getInstance(timezone)
    val currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH)
    val forecastDay = forecastCalendar.get(Calendar.DAY_OF_MONTH)
    val isDifferentDay = currentDay != forecastDay
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .width(100.dp)
    ) {
        // Se for um dia diferente, mostrar o nome do dia também
        if (isDifferentDay) {
            val dayFormat = SimpleDateFormat("EEE", Locale("pt", "BR"))
            dayFormat.timeZone = timezone
            val dayOfWeek = dayFormat.format(forecastCalendar.time)
            
            Text(
                text = dayOfWeek,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
        
        // Hora
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
        "partly cloudy" -> "Pouco Nublado"
        "cloudy" -> "Nublado"
        "overcast" -> "Encoberto"
        "mist" -> "Névoa"
        "patchy rain possible" -> "Possível Chuva"
        "patchy snow possible" -> "Possível Neve"
        "patchy sleet possible" -> "Possível Granizo"
        "patchy freezing drizzle possible" -> "Possível Garoa Gelada"
        "thundery outbreaks possible" -> "Possíveis Trovoadas"
        "blowing snow" -> "Rajadas de Neve"
        "blizzard" -> "Nevasca"
        "fog" -> "Nevoeiro"
        "freezing fog" -> "Nevoeiro Congelante"
        "patchy light drizzle" -> "Garoa Leve"
        "light drizzle" -> "Garoa Leve"
        "freezing drizzle" -> "Garoa Congelante"
        "heavy freezing drizzle" -> "Garoa Congelante Forte"
        "patchy light rain" -> "Chuva Leve"
        "light rain" -> "Chuva Leve"
        "moderate rain at times" -> "Chuva Moderada"
        "moderate rain" -> "Chuva Moderada"
        "heavy rain at times" -> "Chuva Forte"
        "heavy rain" -> "Chuva Forte"
        "light freezing rain" -> "Chuva Congelante"
        "moderate or heavy freezing rain" -> "Chuva Congelante Forte"
        "light sleet" -> "Granizo Leve"
        "moderate or heavy sleet" -> "Granizo Forte"
        "patchy light snow" -> "Neve Leve"
        "light snow" -> "Neve Leve"
        "patchy moderate snow" -> "Neve Moderada"
        "moderate snow" -> "Neve Moderada"
        "patchy heavy snow" -> "Neve Forte"
        "heavy snow" -> "Neve Forte"
        "ice pellets" -> "Pelotas de Gelo"
        "light rain shower" -> "Pancada de Chuva"
        "moderate or heavy rain shower" -> "Pancada de Chuva Forte"
        "torrential rain shower" -> "Chuva Torrencial"
        "light sleet showers" -> "Granizo Leve"
        "moderate or heavy sleet showers" -> "Granizo Forte"
        "light snow showers" -> "Neve Leve"
        "moderate or heavy snow showers" -> "Neve Forte"
        "light showers of ice pellets" -> "Granizo Leve"
        "moderate or heavy showers of ice pellets" -> "Granizo Forte"
        "patchy light rain with thunder" -> "Chuva com Trovões"
        "moderate or heavy rain with thunder" -> "Chuva Forte com Trovões"
        "patchy light snow with thunder" -> "Neve com Trovões"
        "moderate or heavy snow with thunder" -> "Neve Forte com Trovões"
        "clear" -> "Céu Limpo"
        else -> condition
    }
}

// Função para obter cores de gradiente com base na condição climática
fun getGradientColorsForCondition(condition: String): List<Color> {
    return when (condition.lowercase()) {
        "sunny", "clear" -> listOf(
            Color(0xFF4A90E2), // Azul claro
            Color(0xFF1E3C72)  // Azul escuro
        )
        "partly cloudy" -> listOf(
            Color(0xFF5C6BC0), // Azul médio
            Color(0xFF3949AB)  // Azul índigo
        )
        "cloudy", "overcast" -> listOf(
            Color(0xFF78909C), // Azul acinzentado
            Color(0xFF455A64)  // Azul acinzentado escuro
        )
        "rain", "light rain", "moderate rain", "heavy rain", "patchy rain possible" -> listOf(
            Color(0xFF1A237E), // Azul escuro
            Color(0xFF0D47A1)  // Azul muito escuro
        )
        "snow", "patchy snow possible", "blizzard", "blowing snow" -> listOf(
            Color(0xFF90CAF9), // Azul muito claro
            Color(0xFF42A5F5)  // Azul claro
        )
        "thundery outbreaks possible", "moderate or heavy rain with thunder" -> listOf(
            Color(0xFF303F9F), // Índigo escuro
            Color(0xFF1A237E)  // Índigo muito escuro
        )
        "fog", "mist", "freezing fog" -> listOf(
            Color(0xFF78909C), // Cinza azulado
            Color(0xFF546E7A)  // Cinza azulado escuro
        )
        else -> listOf(
            Color(0xFF4A90E2), // Azul claro (padrão)
            Color(0xFF1E3C72)  // Azul escuro (padrão)
        )
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
    // Imprimir informações para debug
    println("DEBUG: Data do forecast: ${forecastDay.date}, Epoch: ${forecastDay.date_epoch}, Timezone: ${timezone.id}")
    
    // Converter a data do forecast para o fuso horário correto
    val inputFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormatter = SimpleDateFormat("EEE, dd/MM", Locale("pt", "BR"))
    
    // Configurar os fusos horários para ambos formatadores
    inputFormatter.timeZone = timezone
    outputFormatter.timeZone = timezone
    
    // Formatar a data
    val date = try {
        // Criar uma instância de Calendar no fuso horário correto
        val calendar = java.util.Calendar.getInstance(timezone)
        // Configurar a data a partir da string de data do forecastDay
        val parsedDate = inputFormatter.parse(forecastDay.date)
        calendar.time = parsedDate
        
        // Verificar se o dia foi parseado corretamente
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        val month = calendar.get(java.util.Calendar.MONTH) + 1 // mês começa em 0
        
        println("DEBUG: Data parseada do forecast: Dia $day, Mês $month")
        
        // Formatar a data para exibição
        outputFormatter.format(calendar.time)
    } catch (e: Exception) {
        println("DEBUG: Erro ao converter data do forecast: ${e.message}")
        // Fallback: usar o epoch time que é mais confiável
        val fallbackDate = Date(forecastDay.date_epoch * 1000)
        outputFormatter.format(fallbackDate)
    }
    
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