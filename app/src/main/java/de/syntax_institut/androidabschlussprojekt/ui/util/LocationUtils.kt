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

object LocationUtils {

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

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun getCurrentLocation(context: Context): Location =
        suspendCancellableCoroutine { cont ->
            getLocation(context) { location ->
                cont.resume(location)
            }
        }
}
