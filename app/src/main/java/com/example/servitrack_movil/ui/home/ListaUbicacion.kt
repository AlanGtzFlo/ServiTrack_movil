package com.example.servitrack_movil.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.servitrack_movil.Network.ApiClient
import com.example.servitrack_movil.R
import com.example.servitrack_movil.Network.UbicacionResponse
import com.example.servitrack_movil.Ubicacion
import androidx.recyclerview.widget.GridLayoutManager
import androidx.navigation.fragment.findNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Context

class ListaUbicacion : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UbicacionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lista_ubicacion, container, false)
        recyclerView = view.findViewById(R.id.recyclerTickets)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        cargarUbicaciones()

        return view
    }

    fun UbicacionResponse.toUbicacion(): Ubicacion {
        return Ubicacion(
            id = this.id,
            name = this.name ?: "Sin nombre",
            address = this.address ?: "Sin dirección",
            company = this.company?: 0
        )
    }

    private fun cargarUbicaciones() {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null) ?: ""
        val authHeader = "Bearer $token"
        ApiClient.retrofit.getUbicaciones(authHeader).enqueue(object : Callback<List<UbicacionResponse>> {
            override fun onResponse(
                call: Call<List<UbicacionResponse>>,
                response: Response<List<UbicacionResponse>>
            ) {
                if (response.isSuccessful) {
                    val ubicaciones = response.body()?.map { it.toUbicacion() } ?: emptyList()
                    val adapter = UbicacionAdapter(ubicaciones) { ubicacion ->
                        val action = ListaUbicacionDirections
                            .actionListaUbicacionesFragmentToDetalleUbicacionFragment(ubicacion)
                        findNavController().navigate(action)
                    }
                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(requireContext(), "Error al obtener ubicaciones", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<UbicacionResponse>>, t: Throwable) {
                Toast.makeText(requireContext(), "Fallo de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
