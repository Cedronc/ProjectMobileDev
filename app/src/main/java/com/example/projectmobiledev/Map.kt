package com.example.projectmobiledev

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DatabaseReference
import org.osmdroid.config.Configuration.*
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class Map : AppCompatActivity() {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1

    private lateinit var database: DatabaseReference
    private lateinit var map : MapView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    private var toilets = ArrayList<PublicToilet>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var lastLocation: GeoPoint
    private lateinit var dbHelper: DatabaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        var uid: String? = null
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        dbHelper = DatabaseHelper(this, "toiletsDB.db")

        val filterBtn = findViewById<Button>(R.id.filter_btn)
        filterBtn.setOnClickListener {
            val intent = Intent(this, FilterActivity::class.java)
            startActivity(intent)
        }

        val addBtn = findViewById<Button>(R.id.add_toilet_btn)
        addBtn.setOnClickListener {
            val intent = Intent(this, AddToilet::class.java)
            startActivity(intent)
        }


        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        navView = findViewById(R.id.navView)



        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.closed)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                it.itemId -> zoomToMarker(it.itemId)
            }
            true
        }

        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        // This won't work unless you have imported this: org.osmdroid.config.Configuration.*
        getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, if you abuse osm's
        //tile servers will get you banned based on this string.

        //inflate and create the map

      val intent = intent
      uid = intent.getStringExtra("UID")

      val user = FirebaseAuth.getInstance().currentUser
      if (user != null) {
        uid = user.uid
        Toast.makeText(
          applicationContext,
          user.email,
          Toast.LENGTH_LONG
        ).show()
      }

    if(uid.equals("")){
      Toast.makeText(
        applicationContext,
        "Niet ingelogd als gebruiker",
        Toast.LENGTH_LONG
      ).show()
    }




    map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        getLocation()
        setVisuals(filterToilets())
    }

    private fun filterToilets(): ArrayList<PublicToilet> {
        val filterArray = getIntentExtras()
        val filteredToilets = ArrayList<PublicToilet>()
        toilets = dbHelper.getToilets()
        //check if filterArray is empty or is not initialized
        Log.d("filter", filterArray.toString())
        if (filterArray.isEmpty()){
            return toilets
        }
        if (filterArray[0] == "not set" && filterArray[1] == "not set" && filterArray[2] == "not set") {
            Log.d("filter", "$toilets")
            return toilets
        }

        when(filterArray[0]){
            "male" -> filteredToilets.addAll(toilets.filter { toilet -> toilet.DOELGROEP.contains("man/vrouw") })
            "female" -> filteredToilets.addAll(toilets.filter { toilet -> toilet.DOELGROEP.contains("vrouw") })
        }

        when(filterArray[1]){
            "disabled" -> filteredToilets.addAll(toilets.filter { toilet -> toilet.INTEGRAAL_TOEGANKELIJK.contains("ja") })
            "notDisabled" ->
                filteredToilets.addAll(toilets.filter {
                        toilet -> toilet.INTEGRAAL_TOEGANKELIJK.contains("ja")
                        || toilet.INTEGRAAL_TOEGANKELIJK.contains("nee") })
        }

        when(filterArray[2]){
            "diaper" -> filteredToilets.addAll(toilets.filter { toilet -> toilet.LUIERTAFEL.contains("ja") })
            "notDiaper" -> filteredToilets.addAll(toilets.filter { toilet -> toilet.LUIERTAFEL.contains("ja") || toilet.LUIERTAFEL.contains("nee") })
        }
        Log.d("filter", "not filtered: $toilets")
        Log.d("filter", "filtered: $filteredToilets")
        return filteredToilets
    }

    private fun getIntentExtras(): ArrayList<String> {
        val filterArray = intent.getStringArrayListExtra("filter")
        if (filterArray != null) {
            return filterArray
        }
        return ArrayList()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onResume() {
        super.onResume()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        getLocation()
        setVisuals(filterToilets())
        map.onResume() //needed for compass, my location overlays, v6.0.0 and up
    }

    override fun onPause() {
        super.onPause()
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause()  //needed for compass, my location overlays, v6.0.0 and up
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setMarkers(toilets: List<PublicToilet>?){
        if (toilets == null)
            return
        for (toilet in toilets){
            Log.d("markers", toilet.LAT.toString() + " " + toilet.LONG.toString())

            val marker = Marker(map)
            marker.position = GeoPoint(toilet.LAT, toilet.LONG)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = toilet.OMSCHRIJVING
            marker.snippet = toilet.STRAAT
            marker.id = toilet.ID.toString()

            if (toilet.DOELGROEP == "man/vrouw")
                marker.icon = resources.getDrawable(R.drawable.manwoman, null)
            if (toilet.DOELGROEP == "vrouw")
                marker.icon = resources.getDrawable(R.drawable.female, null)
            if (toilet.DOELGROEP == "man")
                marker.icon = resources.getDrawable(R.drawable.male, null)

            this.map.overlays?.add(marker)
        }
    }

    private fun zoomToMarker(markerID: Number){
        val selected = toilets.find {
            it.ID == markerID
        } ?: return

        map.controller.setZoom(18.5)
        //set the center point to selected toilet
        map.controller.setCenter(GeoPoint(selected.LAT, selected.LONG))
    }

    private fun setVisuals(toilets: ArrayList<PublicToilet>) {
        navView.menu.clear()
        addToiletsToMenu(toilets)
        setMarkers(toilets)
    }

    private fun addToiletsToMenu(list: ArrayList<PublicToilet>) {
        if (this::lastLocation.isInitialized) {
            orderList(list)
            list.forEach { toilet ->
                Log.d("toilets", toilet.ID.toString())
                navView.menu.add(
                    toilet.ID,
                    toilet.ID,
                    getDistanceToUser(toilet).toInt(),
                    toilet.OMSCHRIJVING
                )
            }
            return
        } else
            list.forEach { toilet ->
                navView.menu.add(
                    toilet.ID,
                    toilet.ID,
                    0,
                    toilet.OMSCHRIJVING
                )
            }
        return

    }

    private fun getLocation(){
        val hasPermission = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if(!hasPermission){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
        //set the zoom level
        map.controller.setZoom(16.0)
        //set the center point
        map.controller.setCenter(GeoPoint(51.219076, 4.414370))

        if(hasPermission){
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            if(!checkLocationEnabled()){
                Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show()
                return
            }
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                if(it == null){
                    Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                lastLocation = GeoPoint(it.latitude, it.longitude)
                map.controller.setCenter(lastLocation)

                val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
                locationOverlay.enableMyLocation()
                map.overlays.add(locationOverlay)
            }.addOnFailureListener{
                Log.d("ToiletFinder", it.toString())
            }
        }
    }

    private fun checkLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun orderList(list: ArrayList<PublicToilet>): ArrayList<PublicToilet> {
        Log.d("OrderCheck", "Ordering list")
        list.sortWith(compareBy { getDistanceToUser(it) })
        return list
    }

    private fun getDistanceToUser(toilet: PublicToilet): Float{
        return convertLatLongToLocation(toilet.LAT, toilet.LONG).distanceTo(convertGeopointToLocation(lastLocation))
    }

    fun convertGeopointToLocation(geoPoint: GeoPoint): Location {
        val location = Location("dummy provider")
        location.latitude = geoPoint.latitude
        location.longitude = geoPoint.longitude
        return location
    }

    fun convertLatLongToLocation(lat: Double, long: Double): Location {
        val location = Location("dummy provider")
        location.latitude = lat
        location.longitude = long
        return location
    }
}
