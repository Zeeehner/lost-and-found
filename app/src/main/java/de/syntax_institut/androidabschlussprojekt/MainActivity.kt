package de.syntax_institut.androidabschlussprojekt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

/**
 * Haupt-Activity der App.
 *
 * Initialisiert das UI und setzt den Inhalt auf das Composable [AppStart].
 * Aktiviert zudem die Edge-to-Edge-Darstellung für ein modernes UI-Layout.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Aktiviert die Edge-to-Edge Darstellung für die Activity
        enableEdgeToEdge()

        // Setzt den Compose-Inhalt der Activity auf das Haupt-Composable AppStart
        setContent {
            AppStart()
        }
    }
}