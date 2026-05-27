package com.jsdr.watchapp
import com.jsdr.watchapp.ui.navigation.Screen
import com.jsdr.watchapp.ui.screens.profile.ProfileScreen
import com.jsdr.watchapp.ui.screens.profile.StatisticsScreen
import com.jsdr.watchapp.ui.components.WatchAppTopBar
import com.jsdr.watchapp.ui.components.WatchAppBottomBar
import com.jsdr.watchapp.data.models.entities.UserList
import com.jsdr.watchapp.ui.screens.home.HomeScreen
import com.jsdr.watchapp.ui.screens.lists.ListsScreen
import com.jsdr.watchapp.ui.screens.lists.ListDetailsScreen
import com.jsdr.watchapp.ui.screens.search.SearchScreen
import com.jsdr.watchapp.ui.screens.movie.MovieDetailsScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jsdr.watchapp.ui.screens.settings.SettingsScreen
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

val DarkBackground = Color(0xFF212121)
val BrandPurple = Color(0xFF7F52FF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    WatchApp()
                }
            }
        }
    }
}

@Composable
fun WatchApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = DarkBackground,

        topBar = {

            // Ukrywamy topbar na ekranie filmu
            if (currentRoute != Screen.MovieDetails.route) {

                WatchAppTopBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        },
        bottomBar = {
            WatchAppBottomBar(
                navController = navController,
                currentRoute = currentRoute
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(Screen.Search.route) {
                SearchScreen(navController = navController)
            }
            composable(Screen.Lists.route) {
                ListsScreen(navController = navController)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(navController = navController)
            }
            composable(Screen.Statistics.route) {
                StatisticsScreen(navController = navController)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(navController = navController)
            }
            composable(Screen.ListDetails.route) { backStackEntry ->

                val listName =
                    backStackEntry.arguments?.getString("listName") ?: "Lista"

                val listDescription =
                    backStackEntry.arguments?.getString("listDescription") ?: ""

                ListDetailsScreen(
                    movieList = UserList(
                        name = listName,
                        description = listDescription
                    ),
                    navController = navController
                )
            }
            composable(Screen.MovieDetails.route) { backStackEntry ->

                val movieName =
                    backStackEntry.arguments?.getString("movieName") ?: "Film"

                MovieDetailsScreen(
                    movieName = movieName,
                    navController = navController
                )
            }
        }
    }
}
