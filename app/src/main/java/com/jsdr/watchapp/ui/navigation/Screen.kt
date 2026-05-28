package com.jsdr.watchapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val icon: ImageVector?) {
    object Home : Screen("home", Icons.Default.Home)
    object Search : Screen("search", Icons.Default.Search)
    object Lists : Screen("lists", Icons.AutoMirrored.Filled.List)
    object Profile : Screen("profile", Icons.Default.Person)
    object Settings : Screen("settings", null)
    object Statistics : Screen("statistics", null)
    object ListDetails : Screen("list_details/{listName}/{listDescription}", null) {

        fun createRoute(
            listName: String,
            listDescription: String
        ): String {

            return "list_details/$listName/$listDescription"
        }
    }
    object MovieDetails : Screen("media_details/{movieId}", null) {

        fun createRoute(movieId: Int): String {
            return "media_details/$movieId"
        }
    }
}