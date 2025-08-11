package com.example.servitrack_movil

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.servitrack_movil.Network.ApiClient
import com.example.servitrack_movil.Network.MensajeResponse
import com.example.servitrack_movil.ui.home.MensajeAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.appcompat.widget.AppCompatButton

class ListaMensajes : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MensajeAdapter
    private var mensajesList = mutableListOf<MensajeResponse>()

    private val args: ListaMensajesArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_lista_mensajes, container, false)

        recyclerView = view.findViewById(R.id.recyclerTickets)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val reporteNombre = args.reporteNombre ?: ""  // <- Aquí se obtiene de args

        adapter = MensajeAdapter(mensajesList) { mensaje ->
            val action = ListaMensajesDirections
                .actionListaMensajesFragmentToDetalleMensajeFragment(
                    mensajeId = mensaje.id.toString(),
                    nombreReporte = reporteNombre
                )
            findNavController().navigate(action)
        }
        recyclerView.adapter = adapter

        val reporteId = args.reporteId ?: 0  // Maneja caso null
        val btnCrear = view.findViewById<AppCompatButton>(R.id.btnCrear)

        btnCrear.setOnClickListener {
            findNavController().navigate(ListaMensajesDirections.actionListaMensajesFragmentToGenerarMensajeFragment(reporteId, reporteNombre))
        }


        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null) ?: ""
        val bearerToken = "Bearer $token"

        fetchMensajes(reporteId, bearerToken)

        return view
    }


    private fun fetchMensajes(reporteId: Int, token: String) {
        ApiClient.retrofit.getMensajesPorReporteId(reporteId, token)
            .enqueue(object : Callback<List<MensajeResponse>> {
                override fun onResponse(
                    call: Call<List<MensajeResponse>>,
                    response: Response<List<MensajeResponse>>
                ) {
                    if (response.isSuccessful) {
                        mensajesList.clear()
                        mensajesList.addAll(response.body() ?: emptyList())
                        adapter.notifyDataSetChanged()
                        Log.d("ListaMensajes", "Mensajes recibidos: ${response.body()}")
                    } else {
                        Log.e("ListaMensajes", "Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<MensajeResponse>>, t: Throwable) {
                    Log.e("ListaMensajes", "Fallo: ${t.message}")
                }
            })
    }
}
