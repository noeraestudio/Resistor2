package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.ResistorViewModel
import com.example.ui.screens.ColorCodeCalculatorScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.InverseCalculatorScreen
import com.example.ui.screens.PowerVoltageCalculatorScreen
import com.example.ui.theme.MyApplicationTheme

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object ColorCode : Screen("color_code", "Kode Warna", Icons.Default.Palette)
    object PowerVoltage : Screen("power_voltage", "Volt & Watt", Icons.Default.ElectricBolt)
    object Inverse : Screen("inverse", "Mode Praktis", Icons.Default.AutoAwesome)
    object History : Screen("history", "Riwayat", Icons.Default.History)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    resistorViewModel: ResistorViewModel = viewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.ColorCode.route

    val calculationResult by resistorViewModel.calculationResult.collectAsStateWithLifecycle()
    val ohmLawResult by resistorViewModel.ohmLawResult.collectAsStateWithLifecycle()
    val selectedBands by resistorViewModel.selectedBands.collectAsStateWithLifecycle()
    val bandCount by resistorViewModel.bandCount.collectAsStateWithLifecycle()
    val customVoltageInput by resistorViewModel.customVoltageInput.collectAsStateWithLifecycle()
    val wattageRating by resistorViewModel.wattageRating.collectAsStateWithLifecycle()

    val inverseInput by resistorViewModel.inverseInputValue.collectAsStateWithLifecycle()
    val inverseTolerance by resistorViewModel.inverseTolerance.collectAsStateWithLifecycle()
    val inverseBandCount by resistorViewModel.inverseBandCount.collectAsStateWithLifecycle()
    val inverseBandsResult by resistorViewModel.inverseBandsResult.collectAsStateWithLifecycle()

    val savedResistors by resistorViewModel.savedResistors.collectAsStateWithLifecycle()

    val screens = listOf(
        Screen.ColorCode,
        Screen.PowerVoltage,
        Screen.Inverse,
        Screen.History
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentRoute) {
                            Screen.ColorCode.route -> "Noerae Resistor"
                            Screen.PowerVoltage.route -> "Daya & Voltase Resistor"
                            Screen.Inverse.route -> "Mode Praktis (Nilai ke Warna)"
                            Screen.History.route -> "Riwayat Resistor"
                            else -> "Noerae Resistor"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ) {
                screens.forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        modifier = Modifier.testTag("nav_${screen.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.ColorCode.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.ColorCode.route) {
                ColorCodeCalculatorScreen(
                    viewModel = resistorViewModel,
                    calculationResult = calculationResult,
                    ohmLawResult = ohmLawResult,
                    selectedBands = selectedBands,
                    bandCount = bandCount,
                    wattageRating = wattageRating,
                    onNavigateToPowerTab = {
                        navController.navigate(Screen.PowerVoltage.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(Screen.PowerVoltage.route) {
                PowerVoltageCalculatorScreen(
                    viewModel = resistorViewModel,
                    calculationResult = calculationResult,
                    ohmLawResult = ohmLawResult,
                    customVoltageInput = customVoltageInput,
                    wattageRating = wattageRating
                )
            }

            composable(Screen.Inverse.route) {
                InverseCalculatorScreen(
                    viewModel = resistorViewModel,
                    inverseInput = inverseInput,
                    inverseTolerance = inverseTolerance,
                    inverseBandCount = inverseBandCount,
                    resultBands = inverseBandsResult
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    viewModel = resistorViewModel,
                    savedResistors = savedResistors,
                    onSelectResistor = { savedItem ->
                        resistorViewModel.loadResistorToCalculator(savedItem)
                        navController.navigate(Screen.ColorCode.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}
