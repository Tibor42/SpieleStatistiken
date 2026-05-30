package com.example.spiele_statistiken

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.spiele_statistiken.ui.EventsScreen
import com.example.spiele_statistiken.ui.NeuesEventScreen
import com.example.spiele_statistiken.ui.SpielTypScreen
import com.example.spiele_statistiken.ui.SpielerDetailScreen
import com.example.spiele_statistiken.ui.StatistikScreen
import com.example.spiele_statistiken.ui.theme.SpieleStatistikenTheme
import com.example.spiele_statistiken.viewmodel.SpielerStatistikViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpieleStatistikenTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val viewModel: SpielerStatistikViewModel = viewModel()

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "neues_event"
        ) {
            composable("neues_event") {
                NeuesEventScreen(viewModel = viewModel, innerPadding = innerPadding, onSpielerClick = { spielerId -> navController.navigate("spieler_detail/$spielerId")})
            }
            composable("events") {
                EventsScreen(viewModel = viewModel, innerPadding = innerPadding)
            }
            composable("statistik") {
                StatistikScreen(viewModel = viewModel, innerPadding = innerPadding)
            }
            composable("spiel_typen") {
                SpielTypScreen(viewModel = viewModel, innerPadding = innerPadding)
            }
            composable("spieler_detail/{spielerId}") { backStackEntry ->
                val spielerId = backStackEntry.arguments?.getString("spielerId")?.toLong() ?:0L
                SpielerDetailScreen(
                    spielerId = spielerId,
                    viewModel = viewModel,
                    innerPadding = innerPadding,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val aktuelleRoute = backStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Add, contentDescription = "Neues Event") },
            label = { Text("Neues Event") },
            selected = aktuelleRoute == "neues_event",
            onClick = { navController.navigate("neues_event") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.List, contentDescription = "Events") },
            label = { Text("Events") },
            selected = aktuelleRoute == "events",
            onClick = { navController.navigate("events") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Star, contentDescription = "Statistik") },
            label = { Text("Statistik") },
            selected = aktuelleRoute == "statistik",
            onClick = { navController.navigate("statistik") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Spiel-Typen") },
            label = { Text("Spiele") },
            selected = aktuelleRoute == "spiel_typen",
            onClick = { navController.navigate("spiel_typen") }
        )
    }
}