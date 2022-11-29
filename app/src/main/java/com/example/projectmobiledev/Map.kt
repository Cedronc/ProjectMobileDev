package com.example.projectmobiledev

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.VolumeShaper.Configuration
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.Marker

import org.osmdroid.config.Configuration.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import com.google.android.gms.internal.maps.zzaa
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlay
import org.osmdroid.views.overlay.OverlayItem

import java.util.ArrayList

class Map : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private lateinit var map : MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        setContentView(R.layout.activity_map)

        map = findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)

        //set the zoom level
        map.controller.setZoom(16.0)
        //set the center point
        map.controller.setCenter(GeoPoint(51.219076, 4.414370))
        getToilets()
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

    private fun setMarkers(toilets: List<PublicToilet>?){
        val markers = ArrayList<OverlayItem>()
        if (toilets == null)
            return
        for (toilet in toilets){
            Log.d("markers", toilet.LAT.toString() + " " + toilet.LONG.toString())
            val marker = OverlayItem(toilet.DOELGROEP, toilet.STRAAT, GeoPoint(toilet.LAT, toilet.LONG))
            markers.add(marker)
        }

        val locationOverlay = ItemizedIconOverlay(markers, null, applicationContext)
        this.map.overlays?.add(locationOverlay)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionsToRequest = ArrayList<String>()
        var i = 0
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i])
            i++
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE)
        }
    }

    fun requestPermissionsIfNecessary(permissions: Array<String>) {
        val permissionsToRequest = ArrayList<String>()
        permissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission)
            }
        }
        /*if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.add(0.toString()),
                REQUEST_PERMISSIONS_REQUEST_CODE)
        }*/
    }

    private fun getToilets(){
        val firebaseDb = Firebase.database("https://mobiledevproject-e36ca-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = firebaseDb.getReference("features/features")
        database = firebaseDb.reference

        val toilets = ArrayList<PublicToilet>()

        database.child("Toilets").get().addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d("Firebase", it.result.toString())
                it.result.children.forEach { toilet ->
                    val temp = toilet.getValue(PublicToilet::class.java)
                    toilets.add(temp!!)
                }
                setMarkers(toilets)
            } else {
                Log.d("ToiletFinder", it.exception?.message.toString())
            }
        }
    }
}