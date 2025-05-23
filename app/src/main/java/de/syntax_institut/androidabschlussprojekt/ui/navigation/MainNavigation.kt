package de.syntax_institut.androidabschlussprojekt.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.ui.screen.ListScreen
import de.syntax_institut.androidabschlussprojekt.ui.screen.SettingsScreen

sealed class BottomNavScreen(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : BottomNavScreen(Screen.List.route, Icons.Default.Home)
    object Settings : BottomNavScreen(Screen.Settings.route, Icons.Default.Settings)
}

@Composable
fun getLabelRes(screen: BottomNavScreen): Int = when (screen) {
    BottomNavScreen.Home -> R.string.nav_home
    BottomNavScreen.Settings -> R.string.nav_settings
}


@Composable
fun MainNavigation(rootNavController: NavHostController, modifier: Modifier = Modifier) {
    val bottomNavController = rememberNavController()
    val items = listOf(BottomNavScreen.Home, BottomNavScreen.Settings)
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                NavHost(
                    navController = bottomNavController,
                    startDestination = BottomNavScreen.Home.route
                ) {
                    composable(BottomNavScreen.Home.route) {
                        ListScreen(rootNavController)
                    }
                    composable(BottomNavScreen.Settings.route) {
                        SettingsScreen(rootNavController)
                    }
                }
            }

            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = stringResource(getLabelRes(screen))) },
                        label = { Text(stringResource(getLabelRes(screen))) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            bottomNavController.navigate(screen.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    }
}
