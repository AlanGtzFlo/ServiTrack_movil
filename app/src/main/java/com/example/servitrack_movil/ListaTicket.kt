package com.example.servitrack_movil

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.servitrack_movil.Network.ApiClient
import com.example.servitrack_movil.Network.ConteoTicketsResponse
import com.example.servitrack_movil.Network.TicketResponse
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ListaTicketFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TicketAdapter
    private lateinit var barChart: BarChart
    private var listaTickets = mutableListOf<Ticket>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_lista_ticket, container, false)

        recyclerView = view.findViewById(R.id.recyclerTickets)
        barChart = view.findViewById(R.id.barChart)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = TicketAdapter(listaTickets) { ticket ->
            val action = ListaTicketFragmentDirections
                .actionListaTicketFragmentToDetalleTicketFragment(ticket.id)
            findNavController().navigate(action)
        }

        recyclerView.adapter = adapter

        obtenerTicketsDesdeAPI()
        obtenerConteoDesdeAPI()

        return view
    }

    private fun obtenerTicketsDesdeAPI() {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null) ?: ""
        val authHeader = "Bearer $token"
        val call = ApiClient.retrofit.getTickets(authHeader)

        call.enqueue(object : Callback<List<TicketResponse>> {
            override fun onResponse(
                call: Call<List<TicketResponse>>,
                response: Response<List<TicketResponse>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val responseTickets = response.body()!!
                    listaTickets.clear()
                    listaTickets.addAll(responseTickets.map {
                        Ticket(
                            id = it.id.toString(),
                            titulo = it.titulo,
                            estado = it.estado,
                            fecha = formatearFecha(it.fecha_limite)
                        )
                    })
                    adapter.notifyDataSetChanged()
                } else {
                    Log.e("API_ERROR", "Respuesta no exitosa: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<TicketResponse>>, t: Throwable) {
                Log.e("API_ERROR", "Error de red: ${t.message}", t)
            }
        })
    }

    private fun obtenerConteoDesdeAPI() {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null) ?: ""

        if (token.isEmpty()) {
            Log.e("TOKEN_ERROR", "Token vacío. No se puede hacer la solicitud.")
            return
        }

        ApiClient.retrofit.getTicketCountByTechnician("Bearer $token")
            .enqueue(object : Callback<ConteoTicketsResponse> {
                override fun onResponse(
                    call: Call<ConteoTicketsResponse>,
                    response: Response<ConteoTicketsResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val conteo = response.body()!!
                        Log.d("GRAFICA", "Conteo recibido: $conteo")
                        mostrarGraficaDesdeConteo(conteo)
                    } else {
                        Log.e("API_CONTEO_ERROR", "Respuesta no exitosa: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ConteoTicketsResponse>, t: Throwable) {
                    Log.e("API_CONTEO_ERROR", "Error de red: ${t.message}", t)
                }
            })
    }

    private fun mostrarGraficaDesdeConteo(conteo: ConteoTicketsResponse) {
        Log.d("GRAFICA", "Pendiente: ${conteo.pendiente}, En proceso: ${conteo.en_proceso}, Completado: ${conteo.completado}")

        val pendiente = conteo.pendiente.takeIf { it > 0 } ?: 2
        val enProceso = conteo.en_proceso.takeIf { it > 0 } ?: 1
        val completado = conteo.completado.takeIf { it > 0 } ?: 3

        val entries = listOf(
            BarEntry(0f, pendiente.toFloat()),
            BarEntry(1f, enProceso.toFloat()),
            BarEntry(2f, completado.toFloat())
        )

        val dataSet = BarDataSet(entries, "Tickets")
        dataSet.colors = listOf(
            android.graphics.Color.parseColor("#F4A300"), // Pendiente
            android.graphics.Color.parseColor("#006D77"), // En proceso
            android.graphics.Color.parseColor("#43A047")  // Completado
        )

        val data = BarData(dataSet)
        data.barWidth = 0.9f

        barChart.data = data
        barChart.setFitBars(true)
        barChart.description.isEnabled = false

        barChart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(listOf("Abiertos", "En proceso", "Resueltos"))
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

    private fun formatearFecha(fecha: Date): String {
        return try {
            val formatter = SimpleDateFormat("dd MMM yyyy", Locale("es", "MX"))
            formatter.format(fecha)
        } catch (e: Exception) {
            fecha.toString()
        }
    }
}
