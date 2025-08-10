package com.example.servitrack_movil

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.servitrack_movil.Network.EmpresaResponse
import com.example.servitrack_movil.Network.ApiClient
import com.example.servitrack_movil.databinding.FragmentListaEmpresaBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Context

class ListaEmpresaFragment : Fragment() {

    private lateinit var binding: FragmentListaEmpresaBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentListaEmpresaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerTickets.layoutManager = LinearLayoutManager(requireContext())

        cargarEmpresasDesdeAPI()
    }

    private fun cargarEmpresasDesdeAPI() {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null) ?: ""
        val authHeader = "Bearer $token"

        val call = ApiClient.retrofit.getEmpresas(authHeader)

        call.enqueue(object : Callback<List<EmpresaResponse>> {
            override fun onResponse(call: Call<List<EmpresaResponse>>, response: Response<List<EmpresaResponse>>) {
                if (response.isSuccessful) {
                    val empresas = response.body() ?: emptyList()

                    val adapter = EmpresaAdapter(empresas.map {
                        Empresa(
                            id = it.id.toString(),
                            nombre = it.nombre,
                            estatus = if (it.estatus) "Activa" else "Inactiva",
                            tipo_poliza = it.tipo_poliza,
                            fecha_inicio_poliza = it.fecha_inicio_poliza.toString().substring(0, 10),
                            fecha_fin_poliza = it.fecha_fin_poliza.toString().substring(0, 10)
                        )
                    }) { empresaSeleccionada ->
                        val action = ListaEmpresaFragmentDirections
                            .actionListaEmpresasFragmentToDetalleEmpresasFragment(empresaSeleccionada)
                        findNavController().navigate(action)
                    }

                    binding.recyclerTickets.adapter = adapter

                } else {
                    Log.e("API", "Error en la respuesta: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<EmpresaResponse>>, t: Throwable) {
                Log.e("API", "Error de red: ${t.localizedMessage}")
            }
        })
    }
}
