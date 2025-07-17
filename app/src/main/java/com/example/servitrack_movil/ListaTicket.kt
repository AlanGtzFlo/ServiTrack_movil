package com.example.servitrack_movil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import android.util.Log

class ListaTicketFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TicketAdapter
    private lateinit var barChart: BarChart

    // Simulando datos
    private val tickets = listOf(
        Ticket("1", "Error en servidor", "Abierto", "13 jul 2025"),
        Ticket("2", "Problema de red", "Cerrado", "12 jul 2025"),
        Ticket("3", "Sin internet", "En proceso", "11 jul 2025"),
        Ticket("4", "Pantalla azul", "Abierto", "10 jul 2025")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_lista_ticket, container, false)

        recyclerView = view.findViewById(R.id.recyclerTickets)
        barChart = view.findViewById(R.id.barChart)

        // Configura RecyclerView
        adapter = TicketAdapter(tickets) { ticket ->
            val action = ListaTicketFragmentDirections
                .actionListaTicketFragmentToDetalleTicketFragment(ticket.id)
            findNavController().navigate(action)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Llenar la gráfica con datos
        mostrarGrafica(tickets)

        return view
    }

    private fun mostrarGrafica(tickets: List<Ticket>) {
        val abiertos = tickets.count { it.estado.equals("Abierto", true) }.takeIf { it > 0 } ?: 2
        val enProceso = tickets.count { it.estado.equals("En proceso", true) }.takeIf { it > 0 } ?: 1
        val cerrados = tickets.count {
            it.estado.equals("Cerrado", true) || it.estado.equals("Resuelto", true)
        }.takeIf { it > 0 } ?: 3

        val entries = listOf(
            BarEntry(0f, abiertos.toFloat()),
            BarEntry(1f, enProceso.toFloat()),
            BarEntry(2f, cerrados.toFloat())
        )

        Log.d("GraficaDebug", "Abiertos: $abiertos, En Proceso: $enProceso, Cerrados: $cerrados")
        Log.d("GraficaDebug", "Entries: ${entries.joinToString()}")

        val dataSet = BarDataSet(entries, "Tickets")
        dataSet.colors = listOf(
            android.graphics.Color.parseColor("#FF8C00"),
            android.graphics.Color.parseColor("#4682B4"),
            android.graphics.Color.parseColor("#3CB371")
        )

        val data = BarData(dataSet)
        data.barWidth = 0.9f

        barChart.data = data
        barChart.setFitBars(true)
        barChart.description.isEnabled = false

        barChart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(listOf("Abiertos", "En proceso", "Cerrados"))
            granularity = 1f
            isGranularityEnabled = true
            setDrawGridLines(false)
            position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        }

        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.isEnabled = false
        barChart.legend.isEnabled = false

        barChart.animateY(1000)
        barChart.invalidate()
    }


}
