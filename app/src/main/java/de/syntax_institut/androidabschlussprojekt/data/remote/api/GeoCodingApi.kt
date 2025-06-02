package de.syntax_institut.androidabschlussprojekt.data.remote.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.syntax_institut.androidabschlussprojekt.BuildConfig
import de.syntax_institut.androidabschlussprojekt.data.remote.model.GeocodingResponse
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit-Serviceinterface zur Kommunikation mit der OpenCage Geocoding API.
 */
interface GeocodingApiService {

    /**
     * Führt eine Reverse-Geocoding-Anfrage durch (Koordinaten → Adresse).
     *
     * @param coordinates Breiten- und Längengrad im Format "lat,lng".
     * @param apiKey API-Schlüssel für OpenCage.
     * @param language Sprache der Rückgabe (Standard: Deutsch).
     * @return [GeocodingResponse] mit den passenden Ortsinformationen.
     */
    @GET("json")
    suspend fun reverseGeocode(
        @Query("q") coordinates: String,
        @Query("key") apiKey: String,
        @Query("language") language: String = "de"
    ): GeocodingResponse

    /**
     * Führt eine Forward-Geocoding-Anfrage durch (Adresse → Koordinaten).
     *
     * @param locationName Name oder Beschreibung eines Ortes.
     * @param apiKey API-Schlüssel für OpenCage.
     * @param language Sprache der Rückgabe (Standard: Deutsch).
     * @return [GeocodingResponse] mit den passenden Koordinaten.
     */
    @GET("json")
    suspend fun forwardGeocode(
        @Query("q") locationName: String,
        @Query("key") apiKey: String,
        @Query("language") language: String = "de"
    ): GeocodingResponse
}

/**
 * Singleton-Client zum Bereitstellen des [GeocodingApiService].
 */
object GeocodingApiClient {

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val api: GeocodingApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.opencagedata.com/geocode/v1/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeocodingApiService::class.java)
    }
}

/**
 * Hauptfunktion zu Testzwecken (kann im Debug-Modus verwendet werden).
 */
fun main() = runBlocking {
    val coordinates = "52.5200,13.4050" // Berlin
    val apiKey = BuildConfig.OPENCAGE_API_KEY

    try {
        val response = GeocodingApiClient.api.reverseGeocode(coordinates, apiKey)
        println("Ergebnis:")
        println(response)
    } catch (e: Exception) {
        println("Fehler bei der Anfrage: ${e.message}")
    }
}