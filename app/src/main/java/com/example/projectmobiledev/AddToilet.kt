package com.example.projectmobiledev

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class AddToilet : AppCompatActivity() {

    val firebaseDb = Firebase.database("https://mobiledevproject-e36ca-default-rtdb.europe-west1.firebasedatabase.app/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_toilet)



        findViewById<Button>(R.id.push_toilet_btn).setOnClickListener {
            //TODO: push toilet to database

            val newUUID = UUID.randomUUID().toString()
            setNewUUIDFirebase(newUUID)
        }
    }

    private fun setNewUUIDFirebase(newUUID: String) {
        val database = firebaseDb.reference
        database.child("ToiletUUID").setValue(newUUID)
    }

    private fun saveUUID() {
        val sharedPref = this.getSharedPreferences("toilet", Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putString(getString(R.string.toiletUUID), "TESTOSTERONE-426c-4043-9026-dde7a9b3beb0")
            apply()
        }
    }

    fun getCurrentUUID(): String {
        val uuid = this.getSharedPreferences("toilet", Context.MODE_PRIVATE)
        return uuid.getString("ToiletUUID", null).toString()
    }
}