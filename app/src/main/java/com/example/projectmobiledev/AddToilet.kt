package com.example.projectmobiledev

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class AddToilet : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_toilet)



        //saveUUID()

    }

    private fun saveUUID() {
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putString(getString(R.string.toiletUUID), "69bf192e-426c-4043-9026-dde7a9b3beb0")
            apply()
        }
    }

    private fun getCurrentUUID(): String {
        val uuid = this.getPreferences(Context.MODE_PRIVATE)
        return uuid.getString("ToiletUUID", null).toString()
    }
}