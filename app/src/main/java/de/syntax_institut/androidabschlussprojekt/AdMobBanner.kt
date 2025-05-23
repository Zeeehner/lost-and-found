package de.syntax_institut.androidabschlussprojekt

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun AdMobBanner(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val adView = remember {
        AdView(context).apply { adUnitId = "ca-app-pub-8139090832536639/6363342729"
        }
    }
    val adSize = remember {
        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
            context,
            (Resources.getSystem().displayMetrics.widthPixels / Resources.getSystem().displayMetrics.density).toInt()
        )
    }
    AndroidView(
        factory = {
            adView.setAdSize(adSize)
            adView.loadAd(AdRequest.Builder().build())
            adView
        },
        modifier = modifier
    )
}