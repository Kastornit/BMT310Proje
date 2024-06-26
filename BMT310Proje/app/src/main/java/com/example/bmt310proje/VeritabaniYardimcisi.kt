package com.example.bmt310proje

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class VeritabaniYardimcisi(context: Context) : SQLiteOpenHelper(context, "kayitlar", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE kayitlar(bitki_no INTEGER PRIMARY KEY AUTOINCREMENT, bitki_kategori TEXT, bitki_fotograf_yolu TEXT, bitki_konum TEXT, bitki_sonuc TEXT);")

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS kayitlar")
        onCreate(db)
    }
}