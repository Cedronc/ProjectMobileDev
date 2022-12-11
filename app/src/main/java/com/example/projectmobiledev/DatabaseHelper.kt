package com.example.projectmobiledev

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class DatabaseHelper(context: Context, DATABASE_NAME: String?) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    private val DATABASE_VERSION = 2
    val DATABASE_NAME = "PublicToiletDB.db"
    
    private val TABLE_NAME = "PublicToilet"
    private val COLUMN_ID = "id"
    private val COLUMN_OMSCHRIJVING = "omschrijving"
    private val COLUMN_BETLAND = "betland"
    private val COLUMN_STRAAT = "straat"
    private val COLUMN_HUISNUMMER = "huisnummer"
    private val COLUMN_DISTRICT= "disctrict"
    private val COLUMN_POSTCODE = "postcode"
    private val COLUMN_DOELGROEP= "doelgroep"
    private val COLUMN_OPENINGSUREN_OPM = "openingsuren_opm"
    private val COLUMN_LAT = "lat"
    private val COLUMN_LONG = "long"
    private val COLUMN_INTEGRAAL_TOEGANKELIJK = "integraal_toegankelijk"
    private val COLUMN_LUIERTAFEL = "luiertafel"


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE  $TABLE_NAME (
                 $COLUMN_ID INTEGER PRIMARY KEY NOT NULL,
                 $COLUMN_OMSCHRIJVING TEXT,
                 $COLUMN_BETLAND text,
                 $COLUMN_STRAAT text,
                 $COLUMN_HUISNUMMER text,
                 $COLUMN_DISTRICT text,
                 $COLUMN_POSTCODE int,
                 $COLUMN_DOELGROEP text,
                 $COLUMN_OPENINGSUREN_OPM text,
                 $COLUMN_LAT double,
                 $COLUMN_LONG double,
                 $COLUMN_INTEGRAAL_TOEGANKELIJK text,
                 $COLUMN_LUIERTAFEL text)"""
        )
    }

    fun deleteDB() {
        val db = this.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun addToilet(PublicToilet: PublicToilet) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID, PublicToilet.ID)
            put(COLUMN_DOELGROEP, PublicToilet.DOELGROEP)
            put(COLUMN_DISTRICT, PublicToilet.DISTRICT)
            put(COLUMN_OMSCHRIJVING, PublicToilet.OMSCHRIJVING)
            put(COLUMN_BETLAND, PublicToilet.BETLAND)
            put(COLUMN_STRAAT, PublicToilet.STRAAT)
            put(COLUMN_HUISNUMMER, PublicToilet.HUISNUMMER)
            put(COLUMN_POSTCODE, PublicToilet.POSTCODE)
            put(COLUMN_OPENINGSUREN_OPM, PublicToilet.OPENINGSUREN_OPM)
            put(COLUMN_LAT, PublicToilet.LAT)
            put(COLUMN_LONG, PublicToilet.LONG)
            put(COLUMN_LUIERTAFEL, PublicToilet.LUIERTAFEL)
            put(COLUMN_INTEGRAAL_TOEGANKELIJK, PublicToilet.INTEGRAAL_TOEGANKELIJK)
        }

        db.insert(TABLE_NAME, null, values)
        Log.d("db","TEST HELP ME PLS" +  db.insert(TABLE_NAME, null, values))
        db.close()
    }

    @SuppressLint("Range")
    fun getToilets(): ArrayList<PublicToilet> {
        val PublicToilets = ArrayList<PublicToilet>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                val PublicToilet = PublicToilet()
                PublicToilet.ID = result.getString(result.getColumnIndex(COLUMN_ID)).toInt()
                PublicToilet.OMSCHRIJVING = result.getString(result.getColumnIndex(COLUMN_OMSCHRIJVING))
                PublicToilet.STRAAT = result.getString(result.getColumnIndex(COLUMN_STRAAT))
                PublicToilet.HUISNUMMER = result.getString(result.getColumnIndex(COLUMN_HUISNUMMER))
                PublicToilet.DISTRICT = result.getString(result.getColumnIndex(COLUMN_DISTRICT))
                PublicToilet.POSTCODE = result.getString(result.getColumnIndex(COLUMN_POSTCODE)).toInt()
                PublicToilet.DOELGROEP = result.getString(result.getColumnIndex(COLUMN_DOELGROEP))
                PublicToilet.OPENINGSUREN_OPM = result.getString(result.getColumnIndex(COLUMN_OPENINGSUREN_OPM))
                PublicToilet.LAT = result.getString(result.getColumnIndex(COLUMN_LAT)).toDouble()
                PublicToilet.LONG = result.getString(result.getColumnIndex(COLUMN_LONG)).toDouble()
                PublicToilet.INTEGRAAL_TOEGANKELIJK = result.getString(result.getColumnIndex(COLUMN_INTEGRAAL_TOEGANKELIJK))
                PublicToilet.LUIERTAFEL = result.getString(result.getColumnIndex(COLUMN_LUIERTAFEL))
                PublicToilet.BETLAND = result.getString(result.getColumnIndex(COLUMN_BETLAND))
                PublicToilets.add(PublicToilet)
            } while (result.moveToNext())
        }
        Log.d("db", "Toilets $PublicToilets")
        result.close()
        db.close()
        return PublicToilets
    }
}

