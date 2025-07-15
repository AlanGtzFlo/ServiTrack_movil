package com.example.servitrack_movil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class CCFragment : Fragment() {

    private lateinit var edtNueva: EditText
    private lateinit var edtConfirmar: EditText
    private lateinit var btnGuardar: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_c_c, container, false)

        // Asocia las vistas con el layout
        edtNueva = view.findViewById(R.id.edtPasswordNueva)
        edtConfirmar = view.findViewById(R.id.edtPasswordConfirmar)
        btnGuardar = view.findViewById(R.id.btnGuardarCambio)

        btnGuardar.setOnClickListener {
            val nueva = edtNueva.text.toString()
            val confirmar = edtConfirmar.text.toString()

            if (nueva != confirmar) {
                Toast.makeText(requireContext(), "Las nuevas contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Aquí iría la lógica real de actualización
            Toast.makeText(requireContext(), "Contraseña actualizada correctamente", Toast.LENGTH_LONG).show()

            // Opcional: cerrar fragmento o navegar atrás
            parentFragmentManager.popBackStack()
        }

        return view
    }
}
