package com.example.servitrack_movil

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast

class GenerarReporteFragment : Fragment() {

    companion object {
        fun newInstance() = GenerarReporteFragment()
    }

    private val viewModel: GenerarReporteViewModel by viewModels()

    // ❗️ Esto es lo que te faltaba
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_generar_reporte, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val idTicket = view.findViewById<EditText>(R.id.etIdTicket)
        val matricula = view.findViewById<EditText>(R.id.etMatricula)
        val idUbicacion = view.findViewById<EditText>(R.id.etIdUbicacion)
        val idEmpresa = view.findViewById<EditText>(R.id.etIdEmpresa)
        val idEquipo = view.findViewById<EditText>(R.id.etIdEquipo)
        val descripcion = view.findViewById<EditText>(R.id.etDescripcion)
        val fecha = view.findViewById<EditText>(R.id.etFecha)
        val tipoPoliza = view.findViewById<EditText>(R.id.etTipoPoliza)
        val modelo = view.findViewById<EditText>(R.id.etModelo)
        val marca = view.findViewById<EditText>(R.id.etMarca)
        val titulo = view.findViewById<EditText>(R.id.etTitulo)
        val categoria = view.findViewById<EditText>(R.id.etCategoria)
        val infoReporte = view.findViewById<EditText>(R.id.etInfoReporte)

        val prioridad = view.findViewById<Spinner>(R.id.spPrioridad)
        val estado = view.findViewById<Spinner>(R.id.spEstado)
        val tienePoliza = view.findViewById<Switch>(R.id.switchPoliza)

        val btnGuardar = view.findViewById<Button>(R.id.btnGuardarReporte)

        btnGuardar.setOnClickListener {
            if (
                idTicket.text.isNullOrBlank() ||
                matricula.text.isNullOrBlank() ||
                idUbicacion.text.isNullOrBlank() ||
                idEmpresa.text.isNullOrBlank() ||
                idEquipo.text.isNullOrBlank() ||
                descripcion.text.isNullOrBlank() ||
                fecha.text.isNullOrBlank() ||
                tipoPoliza.text.isNullOrBlank() ||
                modelo.text.isNullOrBlank() ||
                marca.text.isNullOrBlank() ||
                titulo.text.isNullOrBlank() ||
                categoria.text.isNullOrBlank() ||
                infoReporte.text.isNullOrBlank() ||
                prioridad.selectedItem == null ||
                estado.selectedItem == null
            ) {
                Toast.makeText(requireContext(), "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Formulario válido, guardando reporte...", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
