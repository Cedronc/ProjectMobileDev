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
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.Map

class AddToilet : AppCompatActivity() {
  var ref: DatabaseReference? = null
  var uid: String? = null
  val firebaseDb = Firebase.database("https://mobiledevproject-e36ca-default-rtdb.europe-west1.firebasedatabase.app/")

  var integraalToegankelijk = false
  var luiertafel = false
  var betalend = false

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
    val postcode = findViewById<View>(R.id.add_toilet_edittext_postcode) as EditText
    val doelgroep = findViewById<View>(R.id.add_toilet_edittext_doelgroep) as EditText
    val openingsuren = findViewById<View>(R.id.add_toilet_edittext_openingsuren) as EditText
    var maxGebruikersNr = 0

    val button = findViewById<Button>(R.id.button_1)

    ref = FirebaseDatabase.getInstance().reference.child("Toilets")
    ref!!.addValueEventListener(object : ValueEventListener {
      override fun onDataChange(dataSnapshot: DataSnapshot) {
        if (dataSnapshot.exists()) {
          maxGebruikersNr = (dataSnapshot.childrenCount).toInt()
        }
      }

      override fun onCancelled(databaseError: DatabaseError) {}
    })


    button.setOnClickListener {
      Log.d("addtoilet", "add toilet button clicked")
      ref = FirebaseDatabase.getInstance().reference.child("Toilets")
      ref!!.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
          Log.d("addtoilet", "niet ver: $maxGebruikersNr")
          if (dataSnapshot.exists()) {
            val toilet = PublicToilet()
            toilet.ID = maxGebruikersNr
            toilet.OMSCHRIJVING = omschrijving.text.toString()
            toilet.BETLAND = betalend.toString()
            toilet.STRAAT = straatnaam.text.toString()
            toilet.HUISNUMMER = huisnummer.text.toString()
            toilet.DISTRICT = district.text.toString()
            //TODO: null check
            toilet.POSTCODE = postcode.text.toString().toInt()
            toilet.DOELGROEP = doelgroep.text.toString()
            toilet.OPENINGSUREN_OPM = openingsuren.text.toString()
            toilet.INTEGRAAL_TOEGANKELIJK = integraalToegankelijk.toString()
            toilet.LUIERTAFEL = luiertafel.toString()

            Log.d("addtoilet", "toilet: $toilet")

            val reff = FirebaseDatabase.getInstance().reference.child("Toilets")
            reff.child(maxGebruikersNr.toString()).setValue(toilet)
            Log.d("addtoilet", "ver: $maxGebruikersNr")

            val newUUID = UUID.randomUUID().toString()
            setNewUUIDFirebase(newUUID)
            saveUUID(newUUID)

            val mainActivity = Intent(this@AddToilet, Map::class.java)
            mainActivity.putExtra("UID", uid)
            startActivity(mainActivity)
          }
        }

        override fun onCancelled(databaseError: DatabaseError) {
          Toast.makeText(applicationContext, "ERROR", Toast.LENGTH_SHORT).show()
        }
      })
    }
  }


  fun test(){
    Log.d("addtoilet", "test")
  }


  private fun setNewUUIDFirebase(newUUID: String) {
    val database = firebaseDb.reference
    database.child("ToiletUUID").setValue(newUUID)
  }

  private fun saveUUID(uuid: String) {
    val sharedPref = this.getSharedPreferences("toilet", Context.MODE_PRIVATE)
    with (sharedPref.edit()) {
      putString(getString(R.string.toiletUUID), uuid)
      apply()
    }
  }

  fun getCurrentUUID(): String {
    val uuid = this.getSharedPreferences("toilet", Context.MODE_PRIVATE)
    return uuid.getString("ToiletUUID", null).toString()
  }

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
}
