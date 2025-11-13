package com.example.servitrack_movil

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.servitrack_movil.Ticket


class TicketAdapter(
    private val tickets: List<Ticket>,
    private val onItemClick: (Ticket) -> Unit
) : RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    inner class TicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtTitulo: TextView = itemView.findViewById(R.id.txtTituloTicket)
        // private val txtEstado: TextView = itemView.findViewById(R.id.txtEstadoTicket)
        private val txtFecha: TextView = itemView.findViewById(R.id.txtFechaTicket)

        fun bind(ticket: Ticket) {
            txtTitulo.text = ticket.titulo
            txtFecha.text = ticket.fecha

            itemView.setOnClickListener {
                onItemClick(ticket)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ticket, parent, false)
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        holder.bind(tickets[position])
    }

    override fun getItemCount(): Int = tickets.size
}
