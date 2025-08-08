package com.example.servitrack_movil.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.servitrack_movil.Network.UbicacionResponse
import com.example.servitrack_movil.Ubicacion
import com.example.servitrack_movil.R

class UbicacionAdapter(
    private val ubicaciones: List<Ubicacion>,
    private val onItemClick: (Ubicacion) -> Unit
) : RecyclerView.Adapter<UbicacionAdapter.UbicacionViewHolder>() {

    inner class UbicacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(ubicacion: Ubicacion) {
            itemView.findViewById<TextView>(R.id.tVNombreUbicacion).text = ubicacion.nombre
            itemView.setOnClickListener {
                onItemClick(ubicacion)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UbicacionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ubicacion, parent, false)
        return UbicacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: UbicacionViewHolder, position: Int) {
        holder.bind(ubicaciones[position])
    }

    override fun getItemCount(): Int = ubicaciones.size
}
