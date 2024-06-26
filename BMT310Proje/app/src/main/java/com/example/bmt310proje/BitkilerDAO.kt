package com.example.bmt310proje

import android.content.ContentValues

class BitkilerDAO {

    fun bitkiEkle(
        vt: VeritabaniYardimcisi,
        bitki_kategori: String,
        bitki_fotograf_yolu: String,
        bitki_konum: String,
        bitki_sonuc: String
    ) {

        val db = vt.writableDatabase
        val values = ContentValues()

        values.put("bitki_kategori", bitki_kategori)
        values.put("bitki_fotograf_yolu", bitki_fotograf_yolu)
        values.put("bitki_konum", bitki_konum)
        values.put("bitki_sonuc", bitki_sonuc)

        db.insertOrThrow("kayitlar", null, values)
        db.close()
    }

    fun tumBitkiler(vt: VeritabaniYardimcisi): ArrayList<Bitkiler> {

        val bitkilerArrayList = ArrayList<Bitkiler>()

        val db = vt.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM kayitlar", null)

        while (cursor.moveToNext()) {

            val bitki = Bitkiler(
                cursor.getInt(cursor.getColumnIndexOrThrow("bitki_no")),
                cursor.getString(cursor.getColumnIndexOrThrow("bitki_kategori")),
                cursor.getString(cursor.getColumnIndexOrThrow("bitki_fotograf_yolu")),
                cursor.getString(cursor.getColumnIndexOrThrow("bitki_konum")),
                cursor.getString(cursor.getColumnIndexOrThrow("bitki_sonuc"))
                )

            bitkilerArrayList.add(bitki)
        }

        return bitkilerArrayList
    }

    fun bitkiGuncelle(
        vt: VeritabaniYardimcisi,
        bitki_no: Int,
        bitki_kategori: String,
        bitki_fotograf_yolu: String,
        bitki_konum: String,
        bitki_sonuc: String
    ) {
        val db = vt.writableDatabase
        val values = ContentValues()

        values.put("bitki_kategori", bitki_kategori)
        values.put("bitki_fotograf_yolu", bitki_fotograf_yolu)
        values.put("bitki_konum", bitki_konum)
        values.put("bitki_sonuc", bitki_sonuc)

        db.update("kayitlar", values, "bitki_no=?", arrayOf(bitki_no.toString()))
        db.close()
    }

    fun bitkiSil(vt: VeritabaniYardimcisi, bitki_no: Int) {
        val db = vt.writableDatabase
        db.delete("kayitlar", "bitki_no=?", arrayOf(bitki_no.toString()))
        db.close()
    }
}