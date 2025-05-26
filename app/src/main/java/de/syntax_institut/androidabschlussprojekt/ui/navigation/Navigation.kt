package de.syntax_institut.androidabschlussprojekt.ui.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Onboarding : Screen("onboarding")
    object List : Screen("list")
    object Create : Screen("create")
    object Map : Screen("map")
    object Settings : Screen("settings")
    object Detail : Screen("detail/{itemId}")
    object MapWithLocation : Screen("map/{lat}/{lon}") {
        fun createRoute(lat: Double, lon: Double): String = "map/$lat/$lon"
    }
    companion object {
        fun edit(itemId: String) = "edit/$itemId"
    }
    object PrivateChat : Screen("private_chat/{partnerId}/{partnerName}") {
        fun createRoute(partnerId: String, partnerName: String): String =
            "private_chat/$partnerId/${Uri.encode(partnerName)}"
    }
}