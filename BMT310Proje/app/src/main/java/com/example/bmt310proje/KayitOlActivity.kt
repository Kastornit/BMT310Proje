package com.example.bmt310proje

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.bmt310proje.databinding.ActivityKayitOlBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

class KayitOlActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKayitOlBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKayitOlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, GirisYapActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val passDogrula = binding.confirmPassEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && passDogrula.isNotEmpty()) {
                if (pass == passDogrula) {

                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {

                            val intent = Intent(this, GirisYapActivity::class.java)
                            startActivity(intent)

                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()

                        }
                    }

                } else {
                    Toast.makeText(this, "Şifre eşleşmiyor!..", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Tüm alanları doldurunuz!..", Toast.LENGTH_LONG).show()
            }

        }
    }
}