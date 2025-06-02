package de.syntax_institut.androidabschlussprojekt.di

import android.app.Application
import android.util.Log
import com.google.android.gms.ads.MobileAds
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext

/**
 * Custom Application-Klasse zur Initialisierung globaler Dienste wie AdMob und Koin.
 */
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialisiere Google AdMob
        MobileAds.initialize(this) {
            Log.d("AdMob", "AdMob initialized with status: $it")
        }

        // Starte Koin Dependency Injection
        GlobalContext.startKoin {
            androidContext(this@MyApp)
            modules(appModule)
        }
    }
}