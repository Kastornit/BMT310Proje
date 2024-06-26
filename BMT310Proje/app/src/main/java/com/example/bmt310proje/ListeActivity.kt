package com.example.bmt310proje

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bmt310proje.databinding.ActivityListeBinding

class ListeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListeBinding
    private lateinit var bitkilerList: ArrayList<Bitkiler>
    private lateinit var adapter: RecyclerViewAdapterBitkiler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        val vt = VeritabaniYardimcisi(this)

        bitkilerList = ArrayList<Bitkiler>()

        bitkilerList = BitkilerDAO().tumBitkiler(vt)

        adapter = RecyclerViewAdapterBitkiler(this, bitkilerList)

        binding.recyclerView.adapter = adapter

    }
}
