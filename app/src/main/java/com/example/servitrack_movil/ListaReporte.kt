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
        val call = ApiClient.retrofit.getReportes()

        call.enqueue(object : Callback<List<ReporteResponse>> {
            override fun onResponse(call: Call<List<ReporteResponse>>, response: Response<List<ReporteResponse>>) {
                if (response.isSuccessful && response.body() != null) {
                    listaReportes.clear()
                    listaReportes.addAll(response.body()!!.map {
                        Reporte(
                            id = it.id,
                            titulo = it.categoria,
                            descripcion = it.descripcion,
                            prioridad = if (it.es_poliza) "Alta" else "Normal",
                            estado = if (it.es_poliza) "Póliza" else "Normal",
                            fecha = it.fecha_creacion.take(10),
                            ubicacion = it.ubicacion.toString(),
                            creador = it.empresa.toString(),   // Asumiendo que "empresa" es el creador o cambiar según corresponda
                            tecnico = it.tecnico.toString()
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
