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
import com.example.servitrack_movil.Network.ApiClient
import com.example.servitrack_movil.Network.EmpresaResponse
import com.example.servitrack_movil.databinding.FragmentListaEmpresaBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListaEmpresaFragment : Fragment() {

    private lateinit var binding: FragmentListaEmpresaBinding

    // Lista original completa para filtrar
    private var listaEmpresasOriginal = listOf<Empresa>()
    // Lista para mostrar en el adaptador (mutable)
    private var listaEmpresasFiltradas = mutableListOf<Empresa>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListaEmpresaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerTickets.layoutManager = LinearLayoutManager(requireContext())

        cargarEmpresasDesdeAPI()

        // Configurar los botones para filtrar
        binding.btnActivas.setOnClickListener {
            mostrarEmpresas(listaEmpresasOriginal.filter { it.estatus })
        }

        binding.btnInactivas.setOnClickListener {
            mostrarEmpresas(listaEmpresasOriginal.filter { !it.estatus })
        }


        binding.btnTodas.setOnClickListener {
            mostrarEmpresas(listaEmpresasOriginal)
        }
    }

    private fun cargarEmpresasDesdeAPI() {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null) ?: ""
        val authHeader = "Bearer $token"

        ApiClient.retrofit.getEmpresas(authHeader)
            .enqueue(object : Callback<List<EmpresaResponse>> {
                override fun onResponse(
                    call: Call<List<EmpresaResponse>>,
                    response: Response<List<EmpresaResponse>>
                ) {
                    if (response.isSuccessful) {
                        val empresas = response.body() ?: emptyList()
                        listaEmpresasOriginal = empresas.map {
                            Empresa(
                                id = it.id.toString(),
                                nombre = it.nombre,
                                estatus = it.estatus,
                                tipo_poliza = it.tipo_poliza,
                                fecha_inicio_poliza = it.fecha_inicio_poliza.toString().substring(0, 10),
                                fecha_fin_poliza = it.fecha_fin_poliza.toString().substring(0, 10)
                            )
                        }
                        mostrarEmpresas(listaEmpresasOriginal)
                    } else {
                        Log.e("API", "Error en la respuesta: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<EmpresaResponse>>, t: Throwable) {
                    Log.e("API", "Error de red: ${t.localizedMessage}")
                }
            })
    }

    private fun mostrarEmpresas(empresas: List<Empresa>) {
        listaEmpresasFiltradas.clear()
        listaEmpresasFiltradas.addAll(empresas)
        val adapter = EmpresaAdapter(listaEmpresasFiltradas) { empresaSeleccionada ->
            val action = ListaEmpresaFragmentDirections
                .actionListaEmpresasFragmentToDetalleEmpresasFragment(empresaSeleccionada)
            findNavController().navigate(action)
        }
        binding.recyclerTickets.adapter = adapter
    }
}
