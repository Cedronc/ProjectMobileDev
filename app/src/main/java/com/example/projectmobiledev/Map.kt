package com.example.projectmobiledev

import android.Manifest
import android.R.attr.*
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.osmdroid.config.Configuration.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class Map : AppCompatActivity() {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1

    private lateinit var database: DatabaseReference
    private lateinit var map : MapView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    private val toilets = ArrayList<PublicToilet>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var lastLocation: GeoPoint

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        navView = findViewById<NavigationView>(R.id.navView)

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

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        getLocation()
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
        val markers = ArrayList<OverlayItem>()
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


            this.map.overlays?.add(marker)
        }

        //val locationOverlay = ItemizedIconOverlay(markers, null, applicationContext)
        //this.map.overlays?.add(locationOverlay)
    }

    private fun zoomToMarker(markerID: Number){
        val selected = toilets.find {
            it.ID == markerID
        } ?: return

        map.controller.setZoom(18.5)
        //set the center point to selected toilet
        map.controller.setCenter(GeoPoint(selected.LAT, selected.LONG))
    }

    private fun getToilets(){
        val firebaseDb = Firebase.database("https://mobiledevproject-e36ca-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = firebaseDb.getReference("features/features")
        database = firebaseDb.reference

        database.child("Toilets").get().addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("Firebase", it.result.toString())
                it.result.children.forEach { result ->
                    val toilet = result.getValue(PublicToilet::class.java)
                    toilets.add(toilet!!)
                    orderList()
                    navView.menu.add(toilet.ID, toilet.ID, getDistanceToUser(toilet).toInt(), toilet.OMSCHRIJVING)
                }
                setMarkers(toilets)
            } else {
                Log.d("ToiletFinder", it.exception?.message.toString())
            }
        }
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
        getToilets()

        if(hasPermission){
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
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

    private fun orderList(){
        Log.d("OrderCheck", "Ordering list")
        toilets.sortWith(compareBy { getDistanceToUser(it) })
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