package com.example.servitrack_movil

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide

class UsuarioFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_usuario, container, false)

        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        val id = prefs.getInt("id", -1)
        val nombre = prefs.getString("nombre", "Sin nombre")
        val correo = prefs.getString("correo", "Sin correo")
        val rol = prefs.getString("rol", "Sin rol")
        val fecha = prefs.getString("fecha", "Sin fecha")
        val imagen = prefs.getString("imagen", null)

        // Referenciar los elementos del layout
        val txtIdUsuario = view.findViewById<TextView>(R.id.txtIdUsuario)
        val txtNombreUsuario = view.findViewById<TextView>(R.id.txtNombreUsuario)
        val txtCorreoUsuario = view.findViewById<TextView>(R.id.txtCorreoUsuario)
        val txtPuestoUsuario = view.findViewById<TextView>(R.id.txtPuestoUsuario)
        val txtFechaIngreso = view.findViewById<TextView>(R.id.txtFechaIngresoUsuario)
        val imgUsuario = view.findViewById<ImageView>(R.id.imgUsuario)

        txtIdUsuario.text = "Matrícula: $id"
        txtNombreUsuario.text = "Nombre: $nombre"
        txtCorreoUsuario.text = "Correo: $correo"
        txtPuestoUsuario.text = "Puesto: $rol"
        txtFechaIngreso.text = "Ingreso: ${fecha?.substringBefore("T") ?: "Sin fecha"}"


        // Si tienes una URL en imagen, podrías cargarla con Glide o similar
        if (!imagen.isNullOrEmpty()) {
            Glide.with(this)
                .load(imagen)
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .circleCrop()
                .into(imgUsuario)
        }

        // Acción del botón
        val btnCambiarContrasena: Button = view.findViewById(R.id.btnCambiarContrasena)
        btnCambiarContrasena.setOnClickListener {
            findNavController().navigate(R.id.action_usuarioFragment_to_changePasswordFragment)
        }

        return view
    }
}
