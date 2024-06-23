package com.crp.wikiAppNew

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.crp.wikiAppNew.data.AppDatabase
import com.crp.wikiAppNew.data.PlaceEntity
import com.crp.wikiAppNew.data.VisitSummaryEntity
import com.crp.wikiAppNew.network.WikiAPI
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import com.crp.wikiAppNew.utils.Helper

class LocationService : Service() {

    private val TAG = "LocationService"

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var notificationManager: NotificationManager
    private lateinit var placesClient: PlacesClient
    private var contNotificacion = 2
    private val wikiAPI: WikiAPI by inject()
    private var lastKnownPlace: String? = null
    private var lastNotifiedPlace: String? = null

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Places.initialize(applicationContext, BuildConfig.GOOGLE_API_KEY)
        placesClient = Places.createClient(this)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel()
        this.startForeground(1, createNotification("Service running"))

        requestLocationUpdates()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "locationServiceChannel",
                "Location Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for Location Service"
            }
            notificationManager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(message: String): Notification {
        return NotificationCompat.Builder(this, "locationServiceChannel")
            .setContentTitle("Location Service")
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 30000
        ).apply {
            setMinUpdateIntervalMillis(10000)
        }.build()

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.locations.forEach { location ->
                getPlaceName(location.latitude, location.longitude)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getPlaceName(latitude: Double, longitude: Double) {
        val placeFields: List<Place.Field> = listOf(
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG,
            Place.Field.TYPES
        )

        val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)

        val placeResponse = placesClient.findCurrentPlace(request)
        placeResponse.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val response = task.result
                val topPlace = response.placeLikelihoods.maxByOrNull { it.likelihood }

                topPlace?.let { placeLikelihood ->
                    val currentPlace = placeLikelihood.place.name
                    if (currentPlace != lastKnownPlace) {
                        lastKnownPlace = currentPlace

                        CoroutineScope(Dispatchers.IO).launch {
                            val wikiResponse = wikiAPI.getSearchRespone(
                                currentPlace, "query", "2", "extracts|pageimages|pageterms",
                                "2", "true", "thumbnail", "json", "prefixsearch", "300", "true"
                            )

                            withContext(Dispatchers.Main) {
                                if (wikiResponse.query?.pages?.isNotEmpty() == true) {
                                    val firstPage = wikiResponse.query.pages[0]
                                    val articleTitle = firstPage?.title
                                    val articleExtract = firstPage?.extract
                                    val articleThumbnailUrl = firstPage?.thumbnail?.source

                                    // Logs para depuración
                                    Log.d(TAG, "Artículo encontrado: $articleTitle")
                                    Log.d(TAG, "Extracto del artículo: $articleExtract")
                                    Log.d(TAG, "URL del thumbnail: $articleThumbnailUrl")

                                    if (articleTitle != null) {
                                        val formattedDateTime = Helper.getCurrentFormattedTime()

                                        val db = AppDatabase.getDatabase(applicationContext)

                                        // Mover la lógica de la base de datos a un hilo de fondo
                                        CoroutineScope(Dispatchers.IO).launch {
                                            val newPlaceEntity = PlaceEntity(
                                                coordinates = "$latitude,$longitude",
                                                dateTime = formattedDateTime,
                                                name = articleTitle,
                                                Extract = articleExtract ?: "",
                                                Url = articleThumbnailUrl ?: "",
                                                placeName = currentPlace
                                            )
                                            db.placeDao().insertPlace(newPlaceEntity)

                                            val existingVisitSummary = db.placeDao().getVisitSummaryByPlaceName(currentPlace)
                                            if (existingVisitSummary != null) {
                                                // Update visit count
                                                val updatedVisitSummary = existingVisitSummary.copy(
                                                    visitCount = existingVisitSummary.visitCount + 1
                                                )
                                                db.placeDao().updateVisitSummary(updatedVisitSummary)
                                            } else {
                                                val newVisitSummary = VisitSummaryEntity(
                                                    placeName = currentPlace,
                                                    description = articleExtract ?: "",
                                                    imageUrl = articleThumbnailUrl ?: "",
                                                    visitCount = 1 // Iniciar el contador en 1 para nuevas entradas
                                                )
                                                db.placeDao().insertVisitSummary(newVisitSummary)
                                            }
                                            withContext(Dispatchers.Main) {
                                                sendNotification(currentPlace, articleTitle, placeLikelihood.place.latLng)
                                                verifySavedPlaces()  // Llamada para verificar los lugares guardados
                                            }
                                        }
                                    }
                                } else {
                                    Log.d(TAG, "No se encontraron resultados para $currentPlace")
                                }
                            }
                        }
                    }
                }
            } else {
                val exception = task.exception
                if (exception is ApiException) {
                    Log.e(TAG, "Lugar no encontrado: ${exception.statusCode}")
                }
            }
        }
    }

    private fun verifySavedPlaces() {
        val db = AppDatabase.getDatabase(applicationContext)
        CoroutineScope(Dispatchers.IO).launch {
            val places = db.placeDao().getAllPlaces()
            withContext(Dispatchers.Main) {
                Log.d(TAG, "Saved places: $places")
            }
        }
    }

    private fun sendNotification(placeName: String, articleTitle: String, coordinates: LatLng?) {
        contNotificacion++

        val formattedCoordinates = coordinates?.let {
            String.format("%.6f, %.6f", it.latitude, it.longitude)
        } ?: "Unknown"

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://en.wikipedia.org/wiki/$articleTitle")
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, "locationServiceChannel")
            .setContentTitle("Artículo encontrado")
            .setContentText("Artículo: $articleTitle\nLugar: $placeName\nCoordenadas: $formattedCoordinates")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_menu_view,
                    "Mostrar",
                    pendingIntent
                )
            )
            .build()

        if (placeName != lastNotifiedPlace) {
            notificationManager.notify(contNotificacion, notification)
            lastNotifiedPlace = placeName
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        // No explicit shutdown for placesClient, ensuring the requests are handled
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
