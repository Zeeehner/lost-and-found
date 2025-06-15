import java.util.Properties
import java.io.FileInputStream

// Load local.properties file to get local API keys or sensitive data
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

plugins {
    alias(libs.plugins.android.application)                // Android application plugin
    alias(libs.plugins.kotlin.android)                    // Kotlin Android plugin
    alias(libs.plugins.kotlin.compose)                   // Kotlin Compose plugin
    alias(libs.plugins.kotlin.ksp)                      // Kotlin Symbol Processing for Room
    alias(libs.plugins.jetbrains.kotlin.serialization) // Kotlin serialization plugin
}

android {
    namespace = "de.syntax_institut.androidabschlussprojekt"
    compileSdk = 35

    defaultConfig {
        applicationId = "de.syntax_institut.androidabschlussprojekt"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Read OpenCage API key from local.properties, throw if missing
        val apiKey: String = localProperties.getProperty("OPENCAGE_API_KEY")
            ?: throw GradleException("OPENCAGE_API_KEY not found in local.properties")

        // BuildConfig field to expose OpenCage API key to the app code
        buildConfigField("String", "OPENCAGE_API_KEY", "\"$apiKey\"")

        // Read AdMob Ad Unit ID from local.properties
        val adKey: String = localProperties.getProperty("AdKey")
            ?: throw GradleException("AdKey not found in local.properties")

        // BuildConfig field to expose AdMob Ad Unit ID to the app code
        buildConfigField("String", "AdKey", "\"$adKey\"")

        // Read Google Maps API key from local.properties
        val mapsKey: String = localProperties.getProperty("MAPS_API_KEY")
            ?: throw GradleException("MAPS_API_KEY not found in local.properties")

        manifestPlaceholders["MAPS_API_KEY"] = mapsKey

        val admobAppId: String = localProperties.getProperty("ADMOB_APP_ID")
            ?: throw GradleException("ADMOB_APP_ID not found in local.properties")

        manifestPlaceholders["ADMOB_APP_ID"] = admobAppId
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11     // Use Java 11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"                                 // Target JVM 11
    }
    buildFeatures {
        compose = true                                   // Enable Jetpack Compose
        buildConfig = true                               // Enable BuildConfig generation
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"        // Compose Compiler version
    }
}

dependencies {
    // AndroidX and Compose UI dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime.livedata)

    // Navigation Compose for screen navigation
    implementation(libs.androidx.navigation.compose)

    // Google Maps and Location services
    implementation(libs.maps.compose)
    implementation(libs.google.maps)
    implementation(libs.play.services.location)

    // Google AdMob SDK for ads integration
    implementation("com.google.android.gms:play-services-ads:22.6.0")

    // Room database for local persistence with KSP for annotation processing
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Network and JSON serialization libraries
    implementation(libs.kotlinx.serialization)
    implementation(libs.moshi)
    implementation(libs.retrofit)
    implementation(libs.converterMoshi)

    // Image loading libraries Coil + OkHttp
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // DataStore Preferences for key-value storage
    implementation(libs.androidx.datastore.preferences)

    // Koin for Dependency Injection
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // Firebase libraries managed by BOM
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.auth)

    // Unit and UI testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debugging tools for Compose UI
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Lifecycle ViewModel integration and Google Fonts for Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation("androidx.compose.ui:ui-text-google-fonts:1.5.4")
}

// Apply the Google Services plugin to enable Firebase features
apply(plugin = "com.google.gms.google-services")