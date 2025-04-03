package com.example.apptempocerto.di

import com.example.apptempocerto.data.local.WeatherDatabase
import com.example.apptempocerto.data.location.LocationService
import com.example.apptempocerto.data.remote.WeatherApi
import com.example.apptempocerto.data.repository.WeatherRepository
import com.example.apptempocerto.ui.viewmodel.WeatherViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Http Client
    single {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
        }
    }
    
    // API
    single { WeatherApi(get()) }
    
    // Database
    single { WeatherDatabase.getDatabase(androidContext()) }
    single { get<WeatherDatabase>().favoriteCityDao() }
    
    // Localização
    single { LocationService(androidContext()) }
    
    // Repositório
    single { WeatherRepository(get(), get()) }
    
    // ViewModel
    viewModel { WeatherViewModel(get(), get()) }
} 