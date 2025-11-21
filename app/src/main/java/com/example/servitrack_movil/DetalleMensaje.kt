package com.example.servitrack_movil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetalleMensaje : Fragment() {

    private val args: DetalleMensajeArgs by navArgs()

    private lateinit var txtMensajeId: TextView
    private lateinit var txtNombreReporte: TextView
    private lateinit var txtMensaje: TextView
    private lateinit var txtFecha: TextView
    private lateinit var imgMensaje: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_detalle_mensaje, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Referenciar vistas
        txtMensajeId = view.findViewById(R.id.txtMensajeId)
        txtNombreReporte = view.findViewById(R.id.txtNombreReporte)
        txtMensaje = view.findViewById(R.id.txtMensaje)
        txtFecha = view.findViewById(R.id.txtFecha)
        imgMensaje = view.findViewById(R.id.imgMensaje)

        // Recuperar mensaje del SafeArgs
        val mensaje = args.mensaje
        val nombreReporte = args.nombreReporte ?: "No disponible"

        // Mostrar datos generales
        txtMensajeId.text = "ID:\n${mensaje.id}"
        txtNombreReporte.text = "Reporte:\n$nombreReporte"
        txtMensaje.text = "Mensaje:\n${mensaje.message}"

        // --- FORMATEAR FECHA ---
        val formatoServidor = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", Locale.US)

        val fechaDate: Date? = try {
            formatoServidor.parse(mensaje.created_at)
        } catch (e: Exception) {
            null
        }

        txtFecha.text = "Fecha:\n${mensaje.created_at}"
        // -------------------------

        // Cargar imagen si existe
        if (!mensaje.image.isNullOrEmpty()) {
            Glide.with(this)
                .load(mensaje.image)
                .into(imgMensaje)
        } else {
            imgMensaje.setImageResource(R.drawable.ic_menu_camera)
        }
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
