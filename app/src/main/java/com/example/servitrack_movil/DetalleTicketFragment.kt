package com.example.servitrack_movil

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.servitrack_movil.Network.ApiClient
import com.example.servitrack_movil.Network.TicketResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class DetalleTicketFragment : Fragment() {

    private lateinit var txtTicketId: TextView
    private lateinit var txtTitulo: TextView
    private lateinit var txtDescripcion: TextView
    private lateinit var txtPrioridad: TextView
    private lateinit var txtEstado: TextView
    private lateinit var txtFecha: TextView
    private lateinit var txtUbicacion: TextView
    private lateinit var txtCreador: TextView
    private lateinit var txtTecnico: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_detalle_ticket, container, false)

        // Vincular vistas
        txtTicketId = view.findViewById(R.id.txtTicketId)
        txtTitulo = view.findViewById(R.id.txtTitulo)
        txtDescripcion = view.findViewById(R.id.txtDescripcion)
        txtPrioridad = view.findViewById(R.id.txtPrioridad)
        txtEstado = view.findViewById(R.id.txtEstado)
        txtFecha = view.findViewById(R.id.txtFecha)
        txtUbicacion = view.findViewById(R.id.txtUbicacion)
        txtCreador = view.findViewById(R.id.txtCreador)
        txtTecnico = view.findViewById(R.id.txtTecnico)

        val ticketId = arguments?.getString("ticketId")
        ticketId?.let {
            obtenerTicketPorId(it.toInt())
        }

        return view
    }

    private fun obtenerTicketPorId(id: Int) {
        val call = ApiClient.retrofit.getTicketById(id) // <-- Asegúrate de tener este método en tu interfaz

        call.enqueue(object : Callback<TicketResponse> {
            override fun onResponse(call: Call<TicketResponse>, response: Response<TicketResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val ticket = response.body()!!
                    mostrarDatos(ticket)
                } else {
                    Log.e("DETALLE_API", "Respuesta no exitosa: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<TicketResponse>, t: Throwable) {
                Log.e("DETALLE_API", "Error de red: ${t.message}", t)
            }
        })
    }

    private fun mostrarDatos(ticket: TicketResponse) {
        txtTicketId.text = "ID: ${ticket.id}"
        txtTitulo.text = "Título: ${ticket.titulo}"
        txtDescripcion.text = "Descripción: ${ticket.descripcion}"
        txtPrioridad.text = "Prioridad: ${ticket.prioridad}"
        txtEstado.text = "Estado: ${ticket.estado}"
        txtFecha.text = "Fecha límite: ${formatearFecha(ticket.fecha_limite)}"
        txtUbicacion.text = "Ubicación: ${ticket.ubicacion}"
        txtCreador.text = "Creador: ${ticket.usuario_creador}"
        txtTecnico.text = "Técnico: ${ticket.tecnico_asignado}"
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
