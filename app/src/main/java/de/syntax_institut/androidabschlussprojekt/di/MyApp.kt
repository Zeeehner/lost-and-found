package de.syntax_institut.androidabschlussprojekt.di

import android.app.Application
import android.util.Log
import com.google.android.gms.ads.MobileAds
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) { it ->
            Log.d("AdMob", "AdMob initialized with status: $it") }
        GlobalContext.startKoin {
            androidContext(this@MyApp)
            modules(appModule)
        }
    }
}