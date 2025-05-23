package de.syntax_institut.androidabschlussprojekt.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import de.syntax_institut.androidabschlussprojekt.ui.screen.LoginScreen
import de.syntax_institut.androidabschlussprojekt.ui.screen.OnboardingScreen
import de.syntax_institut.androidabschlussprojekt.ui.screen.SettingsScreen
import androidx.navigation.compose.composable
import de.syntax_institut.androidabschlussprojekt.ui.screen.ItemCreateScreen

@Composable
fun NavGraph(modifier: Modifier = Modifier) {

    val rootNavController: NavHostController = rememberNavController()

    NavHost(
        navController = rootNavController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {
        // 1️⃣ Login-Screen
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

        // Onboarding after register
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    rootNavController.navigate(Screen.List.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // 2️⃣ MainNavigation (BottomNav)
        composable(Screen.List.route) {
            MainNavigation(rootNavController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(rootNavController)
        }

        // 3️⃣ CreateNavigation
        composable(Screen.Create.route) {
            ItemCreateScreen(rootNavController)
        }
    }
}
