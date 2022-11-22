package com.example.projectmobiledev

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import okhttp3.*
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val firebaseDb = Firebase.database("https://mobiledevproject-e36ca-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = firebaseDb.getReference("features")
        database = firebaseDb.reference

        var toilets: List<PublicToilet>?

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                toilets = dataSnapshot.getValue<List<PublicToilet>>()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("Testing", "Failed to read value.", error.toException())
                Toast.makeText(this@MainActivity, "Failed to connect to database.", Toast.LENGTH_SHORT).show()
            }
        })

        getToilets() // werkt goed
    }

    fun getToilets(){
        //get toilets from json
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://geodata.antwerpen.be/arcgissql/rest/services/P_Portal/portal_publiek1/MapServer/8/query?outFields=*&where=1%3D1&f=geojson")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("OUR_APP", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    PublicToilet.decodeJson(PublicToilet.getJson(response.body!!.string()))
                }
            }
        })
    }
}