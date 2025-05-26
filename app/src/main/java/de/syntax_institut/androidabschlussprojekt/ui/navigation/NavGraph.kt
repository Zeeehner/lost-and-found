package de.syntax_institut.androidabschlussprojekt.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import de.syntax_institut.androidabschlussprojekt.ui.screen.LoginScreen
import de.syntax_institut.androidabschlussprojekt.ui.screen.OnboardingScreen
import de.syntax_institut.androidabschlussprojekt.ui.screen.SettingsScreen
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import de.syntax_institut.androidabschlussprojekt.ui.screen.DetailScreen
import de.syntax_institut.androidabschlussprojekt.ui.screen.ItemCreateScreen
import de.syntax_institut.androidabschlussprojekt.ui.screen.MapScreen
import de.syntax_institut.androidabschlussprojekt.ui.screen.*

@Composable
fun NavGraph(modifier: Modifier = Modifier) {

    val rootNavController: NavHostController = rememberNavController()

    NavHost(
        navController = rootNavController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {
        // Login-Screen
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    rootNavController.navigate(Screen.List.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToOnboarding = {
                    rootNavController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Onboarding nach Registrierung
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    rootNavController.navigate(Screen.List.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        //  MainNavigation (BottomNav)
        composable(Screen.List.route) {
            MainNavigation(rootNavController)
        }

        //  Create-Screen
        composable(Screen.Create.route) {
            ItemCreateScreen(navController = rootNavController)
        }

        //  Kartenansicht (allgemein)
        composable(Screen.Map.route) {
            MapScreen(navController = rootNavController)
        }

        //  Einstellungen
        composable(Screen.Settings.route) {
            SettingsScreen(navController = rootNavController)
        }

        // Detailansicht eines Items mit Übergabe der itemId
        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
            DetailScreen(itemId = itemId, navController = rootNavController)
        }

        // Kartenansicht mit spezifischer Location
        composable(
            route = Screen.MapWithLocation.route,
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType },
                navArgument("lon") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull()
            val lon = backStackEntry.arguments?.getString("lon")?.toDoubleOrNull()
            MapScreen(navController = rootNavController, lat = lat, lon = lon)
        }

        // Chat-Screen mit Übergabe von itemId, userId, userName
        composable(
            route = Screen.Chat.route,
            arguments = listOf(
                navArgument("itemId") { type = NavType.StringType },
                navArgument("userId") { type = NavType.StringType },
                navArgument("userName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            val userName = backStackEntry.arguments?.getString("userName") ?: return@composable
            ChatDetailScreen(itemId = itemId, userId = userId, userName = userName)
        }
    }
}
