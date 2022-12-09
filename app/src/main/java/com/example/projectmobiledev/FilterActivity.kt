package com.example.projectmobiledev

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RadioButton

class FilterActivity : AppCompatActivity() {
    lateinit var intentMap: Intent
    lateinit var filterArray: ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)


        filterArray = ArrayList(3)
        filterArray.add("not set")
        filterArray.add("not set")
        filterArray.add("not set")
        val filterBtn = findViewById<Button>(R.id.set_filter_btn)

        filterBtn.setOnClickListener {
            intentMap.putStringArrayListExtra("filter", filterArray)
            startActivity(intentMap)
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked
            intentMap = Intent(this, Map::class.java)
            // add string array

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.maleRB ->
                    if (checked) {
                        Log.d("radio", "male")
                        filterArray[0] = "male"
                    }
                R.id.femaleRB ->
                    if (checked) {
                        Log.d("radio", "female")
                        filterArray[0] = "female"
                    }
                R.id.HandicapJaRB ->
                    if (checked) {
                        Log.d("radio", "disabled")
                        filterArray[1] = "disabled"
                    }
                R.id.HandicapNeeRB ->
                    if (checked) {
                        Log.d("radio", "disabled")
                        filterArray[1] = "notDisabled"
                    }
                R.id.diaperRB ->
                    if (checked) {
                        Log.d("radio", "diaper")
                        filterArray[2] = "diaper"
                    }
                R.id.noDiaperRB ->
                    if (checked) {
                        Log.d("radio", "no diaper")
                        filterArray[2] = "noDiaper"
                    }
            }
        }
    }
}