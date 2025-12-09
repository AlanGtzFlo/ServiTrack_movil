package com.example.servitrack_movil.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.servitrack_movil.databinding.FragmentHomeBinding
import com.example.servitrack_movil.Network.ApiClient
import com.example.servitrack_movil.Network.ConteoTicketsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.servitrack_movil.Network.TicketResponse
import com.example.servitrack_movil.Network.UbicacionResponse
import android.content.Context

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun cargarProximoTicket(token: String) {

        ApiClient.retrofit.getTicketsByUser("Bearer $token")
            .enqueue(object : Callback<List<TicketResponse>> {
                override fun onResponse(
                    call: Call<List<TicketResponse>>,
                    response: Response<List<TicketResponse>>
                ) {
                    if (!isAdded || _binding == null) return

                    if (response.isSuccessful) {
                        val listaTickets = response.body() ?: emptyList()
                        if (listaTickets.isEmpty()) return

                        val ticket = listaTickets.firstOrNull {
                            it.status.equals("Abierto", ignoreCase = true)
                        } ?: listaTickets.first()

                        val idUbicacion = ticket.location.toInt()

                        obtenerNombreUbicacion(idUbicacion) { nombreUbicacion ->

                            if (!isAdded || _binding == null) return@obtenerNombreUbicacion

                            val lista = listOf(
                                ProximoTicket(
                                    ubicacion = nombreUbicacion,
                                    problema = ticket.description,
                                    fecha = formatearFecha(ticket.start_time)
                                )
                            )

                            val adapter = ProximoTicketAdapter(lista)
                            binding.recyclerConsejos.adapter = adapter
                            binding.recyclerConsejos.layoutManager =
                                LinearLayoutManager(requireContext())
                        }
                    }
                }

                override fun onFailure(call: Call<List<TicketResponse>>, t: Throwable) {
                    if (!isAdded || _binding == null) return
                }
            })
    }

    private fun formatearFecha(fecha: Date?): String {
        return try {
            if (fecha == null) return "Sin fecha"
            val formatter = SimpleDateFormat("dd MMM yyyy", Locale("es", "MX"))
            formatter.format(fecha)
        } catch (e: Exception) {
            "Sin fecha"
        }
    }

    private fun obtenerNombreUbicacion(id: Int, callback: (String) -> Unit) {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null) ?: ""
        val authHeader = "Bearer $token"

        ApiClient.retrofit.getUbicaciones(authHeader)
            .enqueue(object : Callback<List<UbicacionResponse>> {
                override fun onResponse(
                    call: Call<List<UbicacionResponse>>,
                    response: Response<List<UbicacionResponse>>
                ) {
                    if (!isAdded) return

                    val ubicacion = if (response.isSuccessful) {
                        response.body()?.find { it.id == id }?.name ?: "Oficina 3"
                    } else "Oficina 3"

                    callback(ubicacion)
                }

                override fun onFailure(call: Call<List<UbicacionResponse>>, t: Throwable) {
                    if (!isAdded) return
                    callback("Error")
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val locale = Locale("es", "MX")
        val dateFormat = SimpleDateFormat("EEEE d 'de' MMMM", locale)
        val todayFormatted = dateFormat.format(Date()).replaceFirstChar { it.uppercase(locale) }

        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val nombre = prefs.getString("nombre", "Usuario")
        val apellido = prefs.getString("apellido", "")
        val token = prefs.getString("access_token", null)

        if (!token.isNullOrEmpty()) {
            cargarProximoTicket(token)
        }

        binding.txtSaludo.text = "¡Bienvenido"
        binding.txtMatricula.text = "$nombre $apellido \n\n$todayFormatted"

        if (!token.isNullOrEmpty()) {
            ApiClient.retrofit.getTicketCountByTechnician("Bearer $token")
                .enqueue(object : Callback<ConteoTicketsResponse> {
                    override fun onResponse(
                        call: Call<ConteoTicketsResponse>,
                        response: Response<ConteoTicketsResponse>
                    ) {
                        if (!isAdded || _binding == null) return

                        val conteo = response.body()
                        binding.txtTicketsCard.text =
                            "Tienes ${conteo?.abierto ?: 0} tickets por resolver"
                    }

                    override fun onFailure(call: Call<ConteoTicketsResponse>, t: Throwable) {
                        if (!isAdded || _binding == null) return
                        binding.txtTicketsCard.text = "Error de conexión"
                    }
                })
        } else {
            binding.txtTicketsCard.text = "No hay sesión activa"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
