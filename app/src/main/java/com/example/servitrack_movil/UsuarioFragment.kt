package com.example.servitrack_movil

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController


class UsuarioFragment : Fragment() {

    private val viewModel: UsuarioViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_usuario, container, false)

        val btnCambiarContrasena: Button = view.findViewById(R.id.btnCambiarContrasena)
        btnCambiarContrasena.setOnClickListener {
            findNavController().navigate(R.id.action_usuarioFragment_to_changePasswordFragment)
        }

        return view
    }
}