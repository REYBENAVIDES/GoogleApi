package com.example.googleapi

import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import com.example.googleapi.Modelo.DatosParaEnviar
import com.example.googleapi.Modelo.RespuestaJson
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList
import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST


class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private lateinit var mMap: GoogleMap
    private var marker: Marker? = null
    val posiciones = ArrayList<LatLng>()
    var lineas: PolylineOptions? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mapFragment: SupportMapFragment? = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        if (mapFragment != null) {
            mapFragment.getMapAsync(this)
        }
        lineas = PolylineOptions()
        lineas!!.width(8F)
        lineas!!.color(Color.RED)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.setOnMapClickListener(this)

        val madrid = LatLng(40.417325, -3.683081)
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(madrid, 14f))
    }
    override fun onMapClick(latLng: LatLng) {
        val punto = LatLng(
            latLng.latitude,
            latLng.longitude
        )
        mMap?.addMarker(
            MarkerOptions().position(punto)
                .title("Marker in Sydney")
        )
        lineas?.add(latLng)
        posiciones.add(latLng)
        makeApiCall()
        if (lineas?.getPoints()?.size === 6) {
            lineas!!.add(lineas!!.getPoints().get(0))
            mMap?.addPolyline(lineas)
            lineas!!.getPoints().clear()
        }
    }

    interface ApiService {

        @POST("maps/api/distancematrix/json")
        fun getDistanceMatrix(
            @Body request: DatosParaEnviar,
            @Header("Authorization") token: String
        ): Call<ResponseBody>
    }

    fun makeApiCall() {
        val apiKey = "AIzaSyDMmRXHBYOjJyXZruXemR11tl7uiJ2T_Q8"
        val destinations = "-1.012376454390002,-79.47095985875173"
        val origins = "-1.0126720015962505,-79.46716425226802"
        val units = "meters"
        val bearerToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZHVzciI6NDYsImVtYWlsIjoiY2FybG9zQGdtYWlsLmNvbSIsInRpcG9fdXNlciI6MywiZXN0YWJsZWNpbWllbnRvX2lkIjoxLCJiZF9ub21icmUiOiJ1Nzg3OTU2NDQyX2FwaSIsImJkX3VzdWFyaW8iOiJ1Nzg3OTU2NDQyX2FwaSIsImJkX2NsYXZlIjoiVXRlcTIwMjIqIiwiYmRfaG9zdCI6ImxvY2FsaG9zdCIsImJkX2lwIjoiIiwiaWF0IjoxNjg4MzQ0MDcyLCJleHAiOjE2ODg3MDQwNzJ9.9MgrjWMz07BLMq9KLekiiNNDkBa70VhO-G-FPNW29m8"

        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val request = DatosParaEnviar(apiKey, destinations, origins, units)
        val call = apiService.getDistanceMatrix(request, "Bearer $bearerToken")
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    val jsonResponseN = response.body()
                    val jsonResponse = response.body()?.string()
                    Toast.makeText(applicationContext, "JSON Response: $jsonResponse", Toast.LENGTH_LONG).show()
                    val gson = Gson()
                    val parsearJson = gson.fromJson(jsonResponse, RespuestaJson::class.java)

                    // Obtener el valor de "text" en el objeto "distance"
                    val textValue = parsearJson.rows[0].elements[0].distance.text
                    Toast.makeText(applicationContext, "JSON Response: $textValue", Toast.LENGTH_LONG).show()
                } else {
                    // Manejar errores de respuesta
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Manejar errores de conexión
            }
        })
    }
}