package com.example.servitrack_movil

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class DetalleUbicacionFragment : Fragment() {

    private lateinit var ubicacion: Ubicacion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Aquí inicializas la variable con los argumentos que llegan del fragmento anterior
        arguments?.let {
            ubicacion = DetalleUbicacionFragmentArgs.fromBundle(it).ubicacion
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detalle_ubicacion, container, false)

        view.findViewById<TextView>(R.id.txtUbicacionId).text = "ID:\n${ubicacion.id}"
        view.findViewById<TextView>(R.id.txtNombreDetalle).text = "Nombre:\n${ubicacion.nombre}"
        view.findViewById<TextView>(R.id.txtDireccionDetalle).text = "Dirección:\n${ubicacion.direccion}"
        view.findViewById<TextView>(R.id.txtCliente_id).text = "Cliente ID:\n${ubicacion.cliente_id}"
        view.findViewById<TextView>(R.id.txtEmpresa).text = "Empresa ID:\n${ubicacion.empresa_id}"
        view.findViewById<TextView>(R.id.txtContacto).text = "Contacto:\n${ubicacion.contacto}"

        return view
    }
}
