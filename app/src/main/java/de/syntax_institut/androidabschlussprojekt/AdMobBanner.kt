package de.syntax_institut.androidabschlussprojekt

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.*
import androidx.compose.ui.platform.LocalContext

/**
 * Composable, das ein adaptives AdMob Banner anzeigt.
 *
 * Das Banner passt seine Breite dynamisch an die Bildschirmbreite an.
 * Die AdUnit-ID wird über BuildConfig eingebunden (BuildConfig.AdKey).
 *
 * @param modifier Modifier zur Anpassung der Darstellung (Größe, Position, etc.)
 */
@Composable
fun AdMobBanner(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // AdView wird nur einmal erstellt und behalten (remember)
    val adView = remember {
        AdView(context).apply {
            adUnitId = BuildConfig.AdKey // AdUnitId aus BuildConfig laden
        }
    }

    // Berechnet adaptive Bannergröße basierend auf der aktuellen Displaybreite
    val adSize = remember {
        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
            context,
            // Display-Breite in dp
            (Resources.getSystem().displayMetrics.widthPixels / Resources.getSystem().displayMetrics.density).toInt()
        )
    }

    // AndroidView zum Einbetten einer nativen Android View in Compose
    AndroidView(
        factory = {
            adView.setAdSize(adSize) // Bannergröße setzen
            adView.loadAd(AdRequest.Builder().build()) // Ad laden
            adView
        },
        modifier = modifier
    )
}