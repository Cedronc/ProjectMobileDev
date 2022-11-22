package com.example.projectmobiledev

import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Serializable
class PublicToilet()
{
    val id: Int = 0
    val category: String = ""
    val extraDescription: String = ""
    val paying: String = ""
    val street: String = ""
    val houseNr: String = ""
    val city: String= ""
    val zipcode: Int = 0
    val targetAudience: String= ""
    val operatingHours: String = ""


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