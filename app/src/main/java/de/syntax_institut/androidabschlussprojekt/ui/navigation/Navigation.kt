package de.syntax_institut.androidabschlussprojekt.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Onboarding : Screen("onboarding")
    object List : Screen("list")
    object Settings : Screen("settings")
}