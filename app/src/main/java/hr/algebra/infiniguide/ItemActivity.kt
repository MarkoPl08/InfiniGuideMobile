package hr.algebra.infiniguide

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.maps.route.extensions.drawRouteOnMap
import com.maps.route.extensions.moveCameraOnMap
import com.maps.route.model.TravelMode
import hr.algebra.infiniguide.ItemAdapter.RouteViewHolder.Companion.ROUTE_ABOUT
import hr.algebra.infiniguide.ItemAdapter.RouteViewHolder.Companion.ROUTE_ID_KEY
import hr.algebra.infiniguide.ItemAdapter.RouteViewHolder.Companion.ROUTE_NAME
import hr.algebra.infiniguide.api.Api
import hr.algebra.infiniguide.model.Account
import hr.algebra.infiniguide.model.RouteMonument
import kotlinx.android.synthetic.main.item.*
import kotlinx.android.synthetic.main.item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ItemActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps: Location? = null
    private var locationNetwork: Location? = null

    lateinit var routeMonuments: List<RouteMonument>
    private var routeSize: Int = 3
    private var currentPossition: Int = 1

    var currentLatLng: LatLng = LatLng(42.640278, 18.10833)

    private val PERMISSION_REQUEST = 10
    private var permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

    private val LOCATION_PERMISSION_REQUEST = 1

    lateinit var nextMonument: RouteMonument

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.fragment_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission(permissions)) {
            } else {
                requestPermissions(permissions, PERMISSION_REQUEST)
            }
        } else {
        }


        btnSoundGuide.setOnClickListener { // Handler code here.
            val intent = Intent(this@ItemActivity, SoundGuide::class.java);
            startActivity(intent);
        }
        btnReadAbout.setOnClickListener { // Handler code here.
            val intent = Intent(this@ItemActivity, ReadAbout::class.java);
            startActivity(intent);
        }
//-----------------------------------------
        btn_start_route.setOnClickListener {
            invertButtons()
            resetRoute()
            callApi()
            getLocation()
        }

        val imageView = findViewById<ImageView>(R.id.routeBackButton)
        imageView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val imageView1 = findViewById<ImageView>(R.id.profile)
        imageView1.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        val routeName = intent.getStringExtra(ROUTE_NAME)
        val route_about = intent.getStringExtra(ROUTE_ABOUT)

        val textView = findViewById<TextView>(R.id.routeName)
        textView.setText(routeName)
        val textView1 = findViewById<TextView>(R.id.routeAbout)
        textView1.setText(route_about)

        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString("RouteID", intent.getStringExtra(ROUTE_ID_KEY))
        editor.commit()

        btn_save_route.setOnClickListener{
            val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
            val id = sharedPreference.getString("RouteID", "23")
            val email = sharedPreference.getString("Username", "Username")
            Api().saveRoute(id!!, email!!).enqueue(object : Callback<Account> {

                override fun onFailure(call: Call<Account>, t: Throwable) {
                    TODO("Not yet implemented")
                }

                override fun onResponse(call: Call<Account>, response: Response<Account>) {
                    Toast.makeText(applicationContext, "Route Saved", Toast.LENGTH_LONG).show()
                    val result = response.body()
                }
            })
        }
    }

    private fun resetRoute() {
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString("CurrentPossition", 0.toString())
        editor.putString("MonumentName", "Monument Name")
        editor.putString("MonumentAbout", "Monument About")
        editor.putString("MonumentPicturePath", "Picture Path")
        editor.putString("MonumentSound", "1800001")
        editor.commit()
    }

    private fun invertButtons() {
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()

        val started = sharedPreference.getString("RouteStarted", "false")

        if (started == "false") {
            btn_start_route.text = "Stop Route"

            btnSoundGuide.isEnabled = true
            btnReadAbout.isEnabled = true
            editor.putString("RouteStarted", "true")
        } else {
            btn_start_route.text = "Start Route"

            btnSoundGuide.isEnabled = false
            btnReadAbout.isEnabled = false
            editor.putString("RouteStarted", "false")
        }
        editor.commit()
    }

    private fun callApi() {
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val id = sharedPreference.getString("RouteID", "23")
        val monumentIndex = sharedPreference.getString("CurrentPossition", "0")?.toInt()

        Api().getRouteMonuments(id).enqueue(object : Callback<List<RouteMonument>> {
            override fun onFailure(call: Call<List<RouteMonument>>, t: Throwable) {
                //Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<List<RouteMonument>>, response: Response<List<RouteMonument>>) {
                val movies = response.body()
                movies?.let {
                    routeMonuments = it
                    currentPossition = monumentIndex!!
                    nextMonument = routeMonuments[monumentIndex + 1]
                    routeSize = routeMonuments.size
                    updateVariables()
                }
            }
        })
    }

    private fun drawRoute(googleMap: GoogleMap) {

        mMap = googleMap
        
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val dubrovnik = LatLng(42.640278, 18.108334)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dubrovnik, 16f))

        drawRoute(googleMap)
        getLocationAccess()
    }

    private fun getLocationAccess() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        }
        else
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGps || hasNetwork) {

            if (hasGps) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, object : LocationListener {
                    override fun onLocationChanged(p0: Location) {
                        if (p0 != null) {
                            locationGps = p0
                            currentLatLng = LatLng(locationGps!!.latitude, locationGps!!.longitude)
                            updateVariables()
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String) {
                    }

                    override fun onProviderDisabled(provider: String) {
                    }

                })

                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (localGpsLocation != null)
                    locationGps = localGpsLocation
            }
            if (hasNetwork) {
                Log.d("CodeAndroidLocation", "hasGps")
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F, object : LocationListener {
                    override fun onLocationChanged(p0: Location) {
                        if (p0 != null) {
                            locationNetwork = p0
                            updateVariables()
                        }
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                    }

                    override fun onProviderEnabled(provider: String) {

                    }

                    override fun onProviderDisabled(provider: String) {

                    }

                })

                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (localNetworkLocation != null)
                    locationNetwork = localNetworkLocation
            }

            if (locationGps != null && locationNetwork != null) {


                if (locationGps!!.accuracy > locationNetwork!!.accuracy) {
                } else {
                    currentLatLng = LatLng(locationGps!!.latitude, locationGps!!.longitude)
                }
            }

        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    private fun checkPermission(permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices) {
            if (checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST) {
            var allSuccess = true
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allSuccess = false
                    val requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(permissions[i])
                    if (requestAgain) {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Go to settings and enable the permission", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            if (allSuccess) {

            }

        }
    }

    private fun updateVariables() {
        if (distance(currentLatLng.latitude, currentLatLng.longitude, nextMonument.Monument.Lat, nextMonument.Monument.Lng) < 0.02) {
            currentPossition++
            Log.d("Location ", "Current possition: " + currentPossition.toString())
            if (currentPossition < routeSize) {
                nextMonument = routeMonuments[currentPossition]
            }
        }
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        val index = sharedPreference.getString("Index", "0")!!.toInt()
        editor.putString("CurrentPossition", currentPossition.toString())
        if (currentPossition <= 0) {
            editor.putString("MonumentName", routeMonuments[0].Monument.Name)
            editor.putString("MonumentAbout", routeMonuments[0].Monument.About)
            editor.putString("MonumentPicturePath", routeMonuments[0].Monument.MonumentID)
            editor.putString("MonumentSound", routeMonuments[0].Monument.Sound)
        } else if (currentPossition >= routeSize) {
            editor.putString("MonumentName", routeMonuments[routeSize - 1].Monument.Name)
            editor.putString("MonumentAbout", routeMonuments[routeSize - 1].Monument.About)
            editor.putString("MonumentPicturePath", routeMonuments[routeSize - 1].Monument.MonumentID)
            editor.putString("MonumentSound", routeMonuments[routeSize - 1].Monument.Sound)
        } else {
            editor.putString("MonumentName", routeMonuments[currentPossition - 1].Monument.Name)
            editor.putString("MonumentAbout", routeMonuments[currentPossition - 1].Monument.About)
            editor.putString("MonumentPicturePath", routeMonuments[currentPossition - 1].Monument.MonumentID)
            editor.putString("MonumentSound", routeMonuments[currentPossition - 1].Monument.Sound)
        }
        editor.commit()
        updateCamera()
    }

    fun updateCamera(){
        val cameraPosition = CameraPosition.Builder().target(LatLng(locationGps?.latitude!!, locationGps!!.longitude)).zoom(17.0.toFloat()).build()
        val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
        mMap?.moveCamera(cameraUpdate)
    }

    fun distance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val earthRadius = 6371
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val sindLat = Math.sin(dLat / 2)
        val sindLng = Math.sin(dLng / 2)
        val a = Math.pow(sindLat, 2.0) + (Math.pow(sindLng, 2.0)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)))
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        Log.d("Debug", "Distance: " + (earthRadius * c).toString())
        return earthRadius * c
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}