package com.example.bmt310proje

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.bmt310proje.databinding.CardViewTasarimiBinding

class RecyclerViewAdapterBitkiler(private val mContext: Context, private var bitkilerListe: List<Bitkiler>) :
    RecyclerView.Adapter<RecyclerViewAdapterBitkiler.CardViewTasarimNesneleriniTutucu>() {

    inner class CardViewTasarimNesneleriniTutucu(val binding: CardViewTasarimiBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewTasarimNesneleriniTutucu {
        val binding =
            CardViewTasarimiBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return CardViewTasarimNesneleriniTutucu(binding)
    }

    override fun getItemCount(): Int {
        return bitkilerListe.size
    }

    override fun onBindViewHolder(holder: CardViewTasarimNesneleriniTutucu, position: Int) {
        val bitki = bitkilerListe[position]

        holder.binding.textViewKategori.text = bitki.bitki_kategori
        holder.binding.textViewSonuc.text = bitki.bitki_sonuc

        val imageView = holder.binding.imageViewBitki
        val imagePath = bitki.bitki_fotograf_yolu
        val bitmap = BitmapFactory.decodeFile(imagePath)
        imageView.setImageBitmap(bitmap)


        holder.binding.satirCardView.setOnClickListener {

        }

        holder.binding.imageViewSil.setOnClickListener {
            val popupMenu = PopupMenu(mContext, holder.binding.imageViewSil)
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

            popupMenu.show()

            popupMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.action_sil -> {
                        val vt = VeritabaniYardimcisi(mContext)
                        BitkilerDAO().bitkiSil(vt, bitki.bitki_no)
                        yeniListe()
                        true
                    }

                    else -> false
                }
            }
        }
    }

    // Yeni liste gelince RecyclerView'ı güncellemek için bu metod kullanılabilir.
    fun yeniListe() {
        bitkilerListe = BitkilerDAO().tumBitkiler(VeritabaniYardimcisi(mContext))
        notifyDataSetChanged()
    }
}
