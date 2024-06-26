package com.example.bmt310proje

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.bmt310proje.databinding.ActivityGirisYapBinding
import com.google.firebase.auth.FirebaseAuth

class GirisYapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGirisYapBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGirisYapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, KayitOlActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)

                    } else {
                        Toast.makeText(this, "Hatalı Bilgi!..", Toast.LENGTH_LONG).show()

                    }
                }


            } else {
                Toast.makeText(this, "Tüm alanları doldurunuz!..", Toast.LENGTH_LONG).show()
            }
        }
    }
}