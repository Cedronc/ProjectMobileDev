package com.example.projectmobiledev

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class AddToilet : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_toilet)



        
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