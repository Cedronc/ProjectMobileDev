package com.example.projectmobiledev

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class AddToilet : AppCompatActivity() {
  var ref: DatabaseReference? = null
  var uid: String? = null
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

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_add_toilet)

    val intent = intent
    uid = intent.getStringExtra("UID")

    val add_toilet_button = findViewById<View>(R.id.add_toilet_button) as Button
    val omschrijving = findViewById<View>(R.id.add_toilet_edittext_omschrijving) as EditText
    val straatnaam = findViewById<View>(R.id.add_toilet_edittext_straatnaam) as EditText
    val huisnummer = findViewById<View>(R.id.add_toilet_edittext_huisnummer) as EditText
    val district = findViewById<View>(R.id.add_toilet_edittext_district) as EditText
    val postcode = findViewById<View>(R.id.add_toilet_postcode) as EditText
    val doelgroep = findViewById<View>(R.id.add_toilet_edittext_doelgroep) as EditText
    val openingsuren = findViewById<View>(R.id.add_toilet_edittext_openingsuren) as EditText
    var integraalToegankelijk = false
    var luiertafel = false
    var betalend = false

    fun onCheckboxClicked(view: View) {
      if (view is CheckBox) {
        val checked: Boolean = view.isChecked

        when (view.id) {
          R.id.add_toilet_integraal_toegankelijk -> {
            if (checked) {
              integraalToegankelijk = true
            }
          }
          R.id.add_toilet_luiertafel -> {
            if (checked) {
              luiertafel = true
            }
          }
          R.id.add_toilet_betalend -> {
            if (checked) {
              betalend = true
            }
          }
        }
      }
    }

    var maxGebruikersNr = 0

    ref = FirebaseDatabase.getInstance().reference.child("Toilets")
    ref!!.addValueEventListener(object : ValueEventListener {
      override fun onDataChange(dataSnapshot: DataSnapshot) {
        if (dataSnapshot.exists()) {
          maxGebruikersNr = (dataSnapshot.childrenCount + 1).toInt()
        }
      }

      override fun onCancelled(databaseError: DatabaseError) {}
    })

    add_toilet_button.setOnClickListener(View.OnClickListener {
      ref = FirebaseDatabase.getInstance().reference.child("ToiletsTest").child(maxGebruikersNr.toString())
      ref!!.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
          if (dataSnapshot.exists()) {
            val toilet = PublicToilet()
            toilet.ID = maxGebruikersNr
            toilet.OMSCHRIJVING = omschrijving.toString()
            toilet.BETLAND = betalend.toString()
            toilet.STRAAT = straatnaam.toString()
            toilet.HUISNUMMER = huisnummer.toString()
            toilet.DISTRICT = district.toString()
            toilet.POSTCODE = postcode.toString().toInt()
            toilet.DOELGROEP = doelgroep.toString()
            toilet.OPENINGSUREN_OPM = openingsuren.toString()
            toilet.INTEGRAAL_TOEGANKELIJK = integraalToegankelijk.toString()
            toilet.LUIERTAFEL = luiertafel.toString()

            val reff = FirebaseDatabase.getInstance().reference.child("ToiletsTest")
            reff.child(maxGebruikersNr.toString()).setValue(toilet)
            val mainActivity = Intent(this@AddToilet, Map::class.java)
            mainActivity.putExtra("UID", uid)
            startActivity(mainActivity)
          }
        }

        override fun onCancelled(databaseError: DatabaseError) {
          Toast.makeText(applicationContext, "ERROR", Toast.LENGTH_SHORT).show()
        }
      })
    })

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
