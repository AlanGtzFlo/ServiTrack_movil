package com.example.servitrack_movil

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.servitrack_movil.Network.ApiClient
import com.example.servitrack_movil.Network.ReporteResponse
import com.example.servitrack_movil.databinding.FragmentListaReporteBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Context

class ListaReporteFragment : Fragment() {

    private lateinit var binding: FragmentListaReporteBinding
    private val listaReportes = mutableListOf<Reporte>()
    private lateinit var adapter: ReporteAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentListaReporteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ReporteAdapter(listaReportes) { reporteSeleccionado ->
            val action = ListaReporteFragmentDirections
                .actionNavListaReportesToDetalleReportesFragment(reporteSeleccionado)
            findNavController().navigate(action)
        }

        binding.recyclerTickets.adapter = adapter
        binding.recyclerTickets.layoutManager = LinearLayoutManager(requireContext())

        binding.btnCrear.setOnClickListener {
            val action = ListaReporteFragmentDirections.actionListaReporteFragmentToGenerarReporteFragment()
            findNavController().navigate(action)
        }

        obtenerReportesDesdeAPI()
    }

    private fun obtenerReportesDesdeAPI() {

        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null) ?: ""
        val authHeader = "Bearer $token"

        val call = ApiClient.retrofit.getReportesByUser("Bearer $token")

        call.enqueue(object : Callback<List<ReporteResponse>> {
            override fun onResponse(call: Call<List<ReporteResponse>>, response: Response<List<ReporteResponse>>) {
                if (response.isSuccessful && response.body() != null) {

                    Log.d("API_REPORTES_DATA", response.body().toString())
                    listaReportes.clear()
                    listaReportes.addAll(response.body()!!.map { reporte ->
                        Reporte(
                            id = reporte.id,
                            ticket_title = reporte.ticket_title ?: "Sin título",
                            created_at = (reporte.created_at ?: "Sin fecha").take(10),
                            cantidadMensajes = reporte.messages.size,
                            mensajes = reporte.messages.map { msg ->
                                MensajeParcelable(
                                    id = msg.id,
                                    message = msg.message,
                                    image = msg.image,
                                    created_at = msg.created_at
                                )
                            }
                        )
                    })
                    adapter.notifyDataSetChanged()
                } else {
                    Log.e("API_REPORTES", "Error en respuesta: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<ReporteResponse>>, t: Throwable) {
                Log.e("API_REPORTES", "Error de red: ${t.message}", t)
            }
        })
    }
}
