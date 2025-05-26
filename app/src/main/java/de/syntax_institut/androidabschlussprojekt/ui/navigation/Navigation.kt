package de.syntax_institut.androidabschlussprojekt.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Onboarding : Screen("onboarding")
    object List : Screen("list")
    object Detail : Screen("detail/{itemId}")
    object Create : Screen("create")
    object Settings : Screen("settings")

    companion object {
        fun edit(itemId: String) = "edit/$itemId"
    }

    object MapWithLocation : Screen("map/{lat}/{lon}") {
        fun createRoute(lat: Double, lon: Double): String = "map/$lat/$lon"
    }
}