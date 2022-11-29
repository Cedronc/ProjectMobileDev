package com.example.projectmobiledev

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import org.osmdroid.util.GeoPoint
import java.sql.RowId

class PublicToilet(){
    var ID: Int = 0
    var CATEGORIE: String = ""
    var OMSCHRIJVING: String = ""
    var BETLAND: String = ""
    var STRAAT: String = ""
    var HUISNUMMER: String = ""
    var DISTRICT: String= ""
    var POSTCODE: Int = 0
    var DOELGROEP: String= ""
    var OPENINGSUREN_OPM: String = ""
    var LAT: Double = 0.0
    var LONG: Double = 0.0

    companion object {
        fun decodeJson(json: JsonObject): PublicToilet {
            return Json.decodeFromJsonElement(json)
        }
        fun getJson(text: String): JsonObject {
            Log.d("JsonBuilder", Json.parseToJsonElement(text).toString())
            return Json.parseToJsonElement(text).jsonObject
        }
    }
}