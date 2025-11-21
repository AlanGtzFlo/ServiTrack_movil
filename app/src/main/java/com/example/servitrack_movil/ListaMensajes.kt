package com.example.servitrack_movil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.servitrack_movil.Network.MensajeResponse
import com.example.servitrack_movil.ui.home.MensajeAdapter
import androidx.appcompat.widget.AppCompatButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ListaMensajes : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MensajeAdapter
    private val mensajesList = mutableListOf<MensajeResponse>()

    private val args: ListaMensajesArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_lista_mensajes, container, false)

        recyclerView = view.findViewById(R.id.recyclerTickets)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val reporteNombre = args.reporteNombre ?: ""
        val reporteId = args.reporteId ?: 0

        // Adaptador
        adapter = MensajeAdapter(mensajesList) { mensaje ->
            val action = ListaMensajesDirections
                .actionListaMensajesFragmentToDetalleMensajeFragment(
                    mensaje = MensajeParcelable(
                        id = mensaje.id,
                        message = mensaje.message,
                        image = mensaje.image,
                        created_at = mensaje.created_at
                    ),
                    nombreReporte = reporteNombre
                )
            findNavController().navigate(action)
        }

        recyclerView.adapter = adapter

        // Botón para crear mensaje
        view.findViewById<AppCompatButton>(R.id.btnCrear).setOnClickListener {
            findNavController().navigate(
                ListaMensajesDirections.actionListaMensajesFragmentToGenerarMensajeFragment(
                    reporteId,
                    reporteNombre
                )
            )
        }

        // Recuperar mensajes enviados desde el fragment anterior
        val mensajesInicialesParcelable = args.mensajes?.toList() ?: emptyList()

        mensajesList.clear()

        val convertidos = mensajesInicialesParcelable.map { convertirParcelableAResponse(it) }
        mensajesList.addAll(convertidos)

        adapter.notifyDataSetChanged()

        return view
    }

    private fun convertirParcelableAResponse(orig: MensajeParcelable): MensajeResponse {

        val formatoServidor = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", Locale.US)

        val fechaDate: Date? = try {
            formatoServidor.parse(orig.created_at)
        } catch (e: Exception) {
            null
        }

        val fechaFormateada = formatearFecha(fechaDate)

        return MensajeResponse(
            id = orig.id,
            message = orig.message,
            image = orig.image,
            created_at = fechaFormateada   // ← ya va formateado
        )
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
}
