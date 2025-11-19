package com.example.servitrack_movil

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.servitrack_movil.Network.ApiClient
import com.example.servitrack_movil.Network.MensajeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.ImageView
import com.bumptech.glide.Glide



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
    ): View? {
        return inflater.inflate(R.layout.fragment_detalle_mensaje, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Referencias a los TextViews
        txtMensajeId = view.findViewById(R.id.txtMensajeId)
        txtNombreReporte = view.findViewById(R.id.txtNombreReporte)
        txtMensaje = view.findViewById(R.id.txtMensaje)
        txtFecha = view.findViewById(R.id.txtFecha)
        imgMensaje = view.findViewById(R.id.imgMensaje)


        val mensajeIdStr = args.mensajeId
        val mensajeId = mensajeIdStr.toIntOrNull()

        if (mensajeId == null) {
            Toast.makeText(requireContext(), "ID de mensaje inválido", Toast.LENGTH_SHORT).show()
            return
        }

        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null)

        if (token == null) {
            Toast.makeText(requireContext(), "Token no disponible", Toast.LENGTH_SHORT).show()
            return
        }

        obtenerDetalleMensaje(mensajeId, "Bearer $token")
    }

    private fun obtenerDetalleMensaje(id: Int, token: String) {
        ApiClient.retrofit.getMensajePorId(id, token).enqueue(object : Callback<MensajeResponse> {
            override fun onResponse(
                call: Call<MensajeResponse>,
                response: Response<MensajeResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val mensaje = response.body()!!
                    mostrarDatos(mensaje)
                } else {
                    Toast.makeText(requireContext(), "Error al cargar mensaje", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MensajeResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("DetalleMensaje", "Error: ${t.message}")
            }
        })
    }

    private fun mostrarDatos(mensaje: MensajeResponse) {
        txtMensajeId.text = "ID:\n${mensaje.id}"
        txtNombreReporte.text = "Reporte:\n${args.nombreReporte ?: "No disponible"}"
        txtMensaje.text = "Mensaje:\n${mensaje.message}"
        txtFecha.text = "Fecha:\n${mensaje.created_at}"

        /*val urlImagen = mensaje.imagen
        Log.d("DetalleMensaje", "URL imagen: $urlImagen")

        if (!urlImagen.isNullOrEmpty()) {
            Glide.with(this)
                .load(urlImagen)
                .placeholder(R.drawable.ic_menu_camera)
                .error(R.drawable.ic_menu_camera)
                .into(imgMensaje)
        } else {
            imgMensaje.setImageResource(R.drawable.ic_menu_camera)
        }*/
    }



}
