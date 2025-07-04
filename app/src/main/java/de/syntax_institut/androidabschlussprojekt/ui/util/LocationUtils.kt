package de.syntax_institut.androidabschlussprojekt.ui.util

import android.Manifest
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Utility-Objekt für standortbezogene Funktionen.
 */
object LocationUtils {

    /**
     * Ermittelt den aktuellen Standort des Geräts und ruft [onSuccess] mit der Location auf.
     *
     * Benötigt die Berechtigungen [Manifest.permission.ACCESS_FINE_LOCATION] und
     * [Manifest.permission.ACCESS_COARSE_LOCATION].
     *
     * @param context Context zur Initialisierung der Location Services.
     * @param onSuccess Callback, der mit der ermittelten Location aufgerufen wird.
     */
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun getLocation(
        context: Context,
        onSuccess: (Location) -> Unit
    ) {
        val cancellationTokenSource = CancellationTokenSource()

        LocationServices.getFusedLocationProviderClient(context).getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location ->
            onSuccess(location)
        }.addOnFailureListener { exception ->
            Log.e("LocationUtils", "getLocation failed", exception)
        }
    }

    /**
     * Suspendierende Funktion, die den aktuellen Standort ermittelt.
     *
     * Benötigt die Berechtigungen [Manifest.permission.ACCESS_FINE_LOCATION] und
     * [Manifest.permission.ACCESS_COARSE_LOCATION].
     *
     * @param context Context zur Initialisierung der Location Services.
     * @return Die ermittelte [Location].
     */
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun getCurrentLocation(context: Context): Location =
        suspendCancellableCoroutine { cont ->
            getLocation(context) { location ->
                cont.resume(location)
            }
        }
}