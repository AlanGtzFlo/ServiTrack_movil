package com.example.servitrack_movil.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.servitrack_movil.R


class ProximoTicketAdapter(
    private val items: List<ProximoTicket>
) : RecyclerView.Adapter<ProximoTicketAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtUbicacion = itemView.findViewById<TextView>(R.id.txtUbicacion)
        val txtProblema = itemView.findViewById<TextView>(R.id.txtProblema)
        val txtFecha = itemView.findViewById<TextView>(R.id.txtFecha)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_consejo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.txtUbicacion.text = item.ubicacion
        holder.txtProblema.text = "Problema: ${item.problema}"
        holder.txtFecha.text = "Fecha de servicio: ${item.fecha}"
    }

    override fun getItemCount() = items.size
}
