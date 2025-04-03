package com.example.apptempocerto.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.apptempocerto.ui.screens.FavoritesScreen
import com.example.apptempocerto.ui.screens.HomeScreen
import com.example.apptempocerto.ui.viewmodel.WeatherViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: WeatherViewModel = koinViewModel()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                navigateToFavorites = {
                    navController.navigate(Screen.Favorites.route)
                }
            )
        }
        
        composable(route = Screen.Favorites.route) {
            FavoritesScreen(
                viewModel = viewModel,
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Favorites : Screen("favorites")
} 