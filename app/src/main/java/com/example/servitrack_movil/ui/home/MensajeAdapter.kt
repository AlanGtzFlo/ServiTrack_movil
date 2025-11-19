package com.example.servitrack_movil.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.servitrack_movil.R
import com.example.servitrack_movil.Network.MensajeResponse

class MensajeAdapter(
    private val mensajes: List<MensajeResponse>,
    private val onClick: (MensajeResponse) -> Unit
) : RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder>() {

    inner class MensajeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtIdReporte: TextView = view.findViewById(R.id.txtIdReporte)
        val txtFecha: TextView = view.findViewById(R.id.txtFecha)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onClick(mensajes[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MensajeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mensaje, parent, false)
        return MensajeViewHolder(view)
    }

    override fun onBindViewHolder(holder: MensajeViewHolder, position: Int) {
        val mensaje = mensajes[position]
        holder.txtIdReporte.text = "Mensaje ID: ${mensaje.id}"
        holder.txtFecha.text = mensaje.created_at ?: ""
    }

    override fun getItemCount(): Int = mensajes.size
}
