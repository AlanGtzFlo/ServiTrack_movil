package com.example.servitrack_movil.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.servitrack_movil.ConsejoAdapter
import com.example.servitrack_movil.Consejos
import com.example.servitrack_movil.R
import com.example.servitrack_movil.databinding.FragmentHomeBinding
import com.example.servitrack_movil.Network.ApiClient
import com.example.servitrack_movil.Network.ConteoTicketsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val locale = Locale("es", "MX")
        val dateFormat = SimpleDateFormat("EEEE d 'de' MMMM", locale)
        val todayFormatted = dateFormat.format(Date()).replaceFirstChar { it.uppercase(locale) }

        // SharedPreferences
        val prefs = requireContext().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
        val nombre = prefs.getString("nombre", "Usuario")
        val id = prefs.getInt("id", 0)
        val token = prefs.getString("access_token", null)

        // Saludos y fecha
        binding.txtSaludo.text = "¡Bienvenido, $nombre!"
        binding.txtMatricula.text = "$todayFormatted \n\nID: $id"

        // Adaptador de consejos
        val listaConsejos = listOf(
            Consejos("Recuerda llevar tu herramienta", R.drawable.work_tools),
            Consejos("Usa tus protecciones", R.drawable.la_seguridad),
            Consejos("Verifica el ticket", R.drawable.ticket_de_soporte),
            Consejos("Contacta al cliente", R.drawable.contact_information),
            Consejos("Registra tu entrada y salida", R.drawable.time_and_calendar),
            Consejos("No olvides tu gafete", R.drawable.id_insignia)
        )
        binding.recyclerConsejos.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerConsejos.adapter = ConsejoAdapter(listaConsejos)

        // Conteo de tickets
        if (!token.isNullOrEmpty()) {
            ApiClient.retrofit.getTicketCountByTechnician("Bearer $token")
                .enqueue(object : Callback<ConteoTicketsResponse> {
                    override fun onResponse(
                        call: Call<ConteoTicketsResponse>,
                        response: Response<ConteoTicketsResponse>
                    ) {
                        if (response.isSuccessful) {
                            val conteo = response.body()
                            val pendientes = conteo?.pendiente ?: 0
                            binding.txtTicketsCard.text = "Tienes $pendientes tickets por resolver"
                        } else {
                            binding.txtTicketsCard.text = "No se pudo cargar el conteo"
                        }
                    }

                    override fun onFailure(call: Call<ConteoTicketsResponse>, t: Throwable) {
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
