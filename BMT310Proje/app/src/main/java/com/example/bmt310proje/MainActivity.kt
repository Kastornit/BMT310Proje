package com.example.bmt310proje

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.bmt310proje.databinding.ActivityMainBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.text.Html
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.view.Window
import android.view.WindowManager

private const val LOCATION_PERMISSION_REQUEST_CODE = 100
private const val STORAGE_PERMISSION_REQUEST_CODE = 101
private lateinit var fusedLocationClient: FusedLocationProviderClient

class MainActivity : AppCompatActivity() {
    private val apiUrl = "https://detect.roboflow.com"
    private val apiKey = "ioVWdS3k4CypkaeqgESu" // Replace with your actual API key
    private val modelId = "misirbitkisi/1" // Replace with your actual model ID
    private lateinit var binding: ActivityMainBinding

    private lateinit var formattedResultDeneme: String
    private lateinit var konumBilgisiDeneme: String
    private lateinit var fotografYoluDeneme: Uri
    private lateinit var sonuc: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_REQUEST_CODE)
        }

        binding.buttonSayfaDegis.setOnClickListener {
            startActivity(Intent(this, ListeActivity::class.java))
        }

        binding.textViewFotografYakala.setOnClickListener {
            val options = arrayOf("Kamera", "Galeri")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Fotoğraf Seç")
            builder.setItems(options) { dialog, which ->
                when (which) {
                    0 -> {
                        ImagePicker.with(this)
                            .cameraOnly()
                            .crop()
                            .maxResultSize(1080, 1080)
                            .start(101)
                    }

                    1 -> {
                        ImagePicker.with(this)
                            .galleryOnly()
                            .crop()
                            .maxResultSize(1080, 1080)
                            .start(101)
                    }
                }
            }
            builder.show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocation()
            } else {
                Toast.makeText(this, "Konum izni verilmedi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data

            if (selectedImageUri != null) {
                Log.d("ImagePicker", "Selected Image URI: $selectedImageUri")

                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                binding.imageView.setImageBitmap(bitmap)

                val imageFile = getFileFromUri(selectedImageUri, "selected_image.jpg")
                if (imageFile != null) {
                    // Kalıcı bir konuma kopyala
                    val permanentImageFile = saveImageToPermanentStorage(bitmap)
                    if (permanentImageFile != null) {
                        fotografYoluDeneme = Uri.fromFile(permanentImageFile) // Kalıcı dosya yolunu sakla
                        binding.textViewSonuc.text = ""
                        binding.progressBar.visibility = View.VISIBLE // ProgressBar'ı göster
                        runInference(permanentImageFile)
                    } else {
                        Toast.makeText(this, "Failed to save image file", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "Failed to create image file", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getFileFromUri(uri: Uri, fileName: String): File? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(cacheDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            outputStream.close()
            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun runInference(imageFile: File) {
        val client = OkHttpClient()
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                imageFile.name,
                RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageFile)
            )
            .build()

        val request = Request.Builder()
            .url("$apiUrl/$modelId?api_key=$apiKey")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    binding.progressBar.visibility = View.GONE // ProgressBar'ı gizle
                    binding.textViewSonuc.text = "İşlem Başarısız"
                    Toast.makeText(
                        this@MainActivity,
                        "Request failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { responseBody ->
                    val jsonResponse = JSONObject(responseBody)
//                    sonuc = JSONObject(responseBody).toString()
                    val formattedResult = formatInferenceResult(jsonResponse)
                    formattedResultDeneme = formattedResult
                    runOnUiThread {
                        binding.progressBar.visibility = View.GONE // ProgressBar'ı gizle
                        binding.textViewSonuc.text =
                            Html.fromHtml(formattedResult) // API yanıtını göster

                        binding.buttonKaydet.setOnClickListener {
                            getLocation()
                        }
                    }
                }
            }
        })
    }

    private fun formatInferenceResult(jsonResponse: JSONObject): String {
        val predictedClasses = jsonResponse.getJSONArray("predicted_classes")
        val predictions = jsonResponse.getJSONObject("predictions")

        val predictedClass =
            if (predictedClasses.length() > 0) predictedClasses.getString(0) else "Bilinmiyor"
        val commonRustConfidence =
            predictions.optJSONObject("CommonRust")?.optDouble("confidence", 0.0) ?: 0.0
        val greyLeafSpotConfidence =
            predictions.optJSONObject("GreyLeafSpot")?.optDouble("confidence", 0.0) ?: 0.0
        val healthyConfidence =
            predictions.optJSONObject("Healthy")?.optDouble("confidence", 0.0) ?: 0.0
        val nlbConfidence = predictions.optJSONObject("NLB")?.optDouble("confidence", 0.0) ?: 0.0

        var turkcePredictedClass = ""
        if (predictedClass.equals("NLB"))
            turkcePredictedClass = "Kuzey Yaprak Yanıklığı"
        else if (predictedClass.equals("CommonRust"))
            turkcePredictedClass = "Yaygın Pas"
        else if (predictedClass.equals("Healthy"))
            turkcePredictedClass = "Sağlıklı"
        else
            turkcePredictedClass = "Gri Yaprak Leke Hastalığı"

        sonuc = "Sonuç: $turkcePredictedClass"


        return "<b><big>Sonuç: $turkcePredictedClass</big></b><br/>" +
                "<br/>" +
                "<b>Olasılıklar</b><br/>" +
                "Sağlıklı: %.2f<br/>".format(healthyConfidence) +
                "Gri Yaprak Leke Hastalığı: %.2f<br/>".format(greyLeafSpotConfidence) +
                "Kuzey Yaprak Yanıklığı: %.2f<br/>".format(nlbConfidence) +
                "Yaygın Pas: %.2f".format(commonRustConfidence)
    }

    private fun getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        requestLocation()
    }

    private fun requestLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                val latitude = location?.latitude ?: 0.0
                val longitude = location?.longitude ?: 0.0
                val konumBilgisi = "Latitude: $latitude, Longitude: $longitude"
                konumBilgisiDeneme = konumBilgisi

                val vt = VeritabaniYardimcisi(this)
                BitkilerDAO().bitkiEkle(vt, "Mısır", fotografYoluDeneme.toString(), konumBilgisiDeneme, sonuc)
                Log.e("KONUM", konumBilgisiDeneme.toString())
                Log.e("FOTOGRAF PATH", fotografYoluDeneme.toString())
                Log.e("JSON", sonuc.toString())


            }
            .addOnFailureListener { e ->
                Log.e("Location", "Konum bilgisi alınırken hata oluştu: ${e.message}")
            }
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        if (uri == null) {
            return null
        }
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            return it.getString(columnIndex)
        }
        return null
    }

    private fun saveImageToPermanentStorage(bitmap: Bitmap): File? {
        val permanentDir = File(getExternalFilesDir(null), "permanent_images")
        if (!permanentDir.exists()) {
            val created = permanentDir.mkdirs()
            Log.d("FileSave", "Directory created: $created, Path: ${permanentDir.absolutePath}")
        }

        val fileName = "IMG_${System.currentTimeMillis()}.jpg"
        val file = File(permanentDir, fileName)
        return try {
            val outputStream = FileOutputStream(file)
            val success = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
            if (success) {
                Log.d("FileSave", "File saved successfully: ${file.absolutePath}")
                file
            } else {
                Log.e("FileSave", "Failed to compress and save bitmap")
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("FileSave", "Exception occurred: ${e.message}")
            null
        }
    }

}
