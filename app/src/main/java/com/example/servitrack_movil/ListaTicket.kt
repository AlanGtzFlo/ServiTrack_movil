package com.example.servitrack_movil

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.servitrack_movil.TicketAdapter



class ListaTicketFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TicketAdapter

    // Simulando datos
    private val tickets = listOf(
        Ticket("1", "Error en servidor", "Abierto", "13 jul 2025"),
        Ticket("2", "Problema de red", "Cerrado", "12 jul 2025")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_lista_ticket, container, false)

        recyclerView = view.findViewById(R.id.recyclerTickets)

        // Navegación al hacer clic
        adapter = TicketAdapter(tickets) { ticket ->
            val action = ListaTicketFragmentDirections
                .actionListaTicketFragmentToDetalleTicketFragment(ticket.id)
            findNavController().navigate(action)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        return view
    }
}
