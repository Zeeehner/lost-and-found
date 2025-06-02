package de.syntax_institut.androidabschlussprojekt.ui.navigation

import android.net.Uri

/**
 * Repräsentiert alle Navigationsrouten der App.
 * Diese sealed class wird zur Navigation zwischen den verschiedenen Screens verwendet.
 *
 * @property route Der Routenstring, der in der Navigation verwendet wird.
 */
sealed class Screen(val route: String) {

    /** Login-Screen, Startpunkt der App für nicht eingeloggte Nutzer */
    object Login : Screen("login")

    /** Onboarding-Screen, wird nach Registrierung angezeigt */
    object Onboarding : Screen("onboarding")

    /** Hauptliste der App (Startziel nach erfolgreichem Login) */
    object List : Screen("list")

    /** Screen zum Erstellen eines neuen Items */
    object Create : Screen("create")

    /** Allgemeine Kartenansicht */
    object Map : Screen("map")

    /** Einstellungen der App */
    object Settings : Screen("settings")

    /** Detailansicht eines bestimmten Items, erwartet eine itemId als Argument */
    object Detail : Screen("detail/{itemId}")

    /**
     * Kartenansicht mit fokussierter Position (Latitude/Longitude)
     *
     * @param lat Latitude der Zielposition
     * @param lon Longitude der Zielposition
     * @return Komplette Route mit den Koordinaten eingebettet
     */
    object MapWithLocation : Screen("map/{lat}/{lon}") {
        fun createRoute(lat: Double, lon: Double): String = "map/$lat/$lon"
    }

    companion object {
        /**
         * Erzeugt die Route für den Bearbeiten-Screen eines bestimmten Items
         *
         * @param itemId Die ID des zu bearbeitenden Items
         * @return String mit eingebetteter Item-ID
         */
        fun edit(itemId: String) = "edit/$itemId"
    }

    /**
     * Private Chat-Screen zwischen zwei Usern.
     * Erwartet partnerId und partnerName als Argumente.
     *
     * @param partnerId ID des Chatpartners
     * @param partnerName Anzeigename des Chatpartners (URL-encoded)
     * @return Navigationsroute mit eingebetteten Parametern
     */
    object PrivateChat : Screen("private_chat/{partnerId}/{partnerName}") {
        fun createRoute(partnerId: String, partnerName: String): String =
            "private_chat/$partnerId/${Uri.encode(partnerName)}"
    }
}