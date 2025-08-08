package com.example.servitrack_movil

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.servitrack_movil.Network.ApiClient
import com.example.servitrack_movil.Network.ReporteResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GenerarReporteFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_generar_reporte, container, false)
    }

    // Extensión para convertir String a RequestBody
    private fun String.toRequestBody(): RequestBody {
        return this.toRequestBody("text/plain".toMediaType())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "No se encontró sesión activa", Toast.LENGTH_SHORT).show()
            return
        }

        // Referencias a los elementos del layout
        val idTicket = view.findViewById<EditText>(R.id.etIdTicket)
        val matricula = view.findViewById<EditText>(R.id.etMatricula)
        val idUbicacion = view.findViewById<EditText>(R.id.etIdUbicacion)
        val idEmpresa = view.findViewById<EditText>(R.id.etIdEmpresa)
        val descripcion = view.findViewById<EditText>(R.id.etDescripcion)
        val tipoPoliza = view.findViewById<EditText>(R.id.etTipoPoliza)
        val categoria = view.findViewById<EditText>(R.id.etCategoria)
        val infoReporte = view.findViewById<EditText>(R.id.etInfoReporte)
        val prioridad = view.findViewById<Spinner>(R.id.spPrioridad)
        val estado = view.findViewById<Spinner>(R.id.spEstado)
        val tienePoliza = view.findViewById<Switch>(R.id.switchPoliza)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardar)

        // Configuración de los spinners
        val opcionesPrioridad = arrayOf("Baja", "Media", "Alta")
        prioridad.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, opcionesPrioridad)

        val opcionesEstado = arrayOf("Abierto", "En proceso", "Cerrado")
        estado.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, opcionesEstado)

        // Mostrar/ocultar campo tipoPoliza según switch
        tienePoliza.setOnCheckedChangeListener { _, isChecked ->
            tipoPoliza.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (!isChecked) tipoPoliza.setText("")
        }

        // Acción botón guardar
        btnGuardar.setOnClickListener {
            // Validaciones
            if (
                idTicket.text.isNullOrBlank() ||
                matricula.text.isNullOrBlank() ||
                idUbicacion.text.isNullOrBlank() ||
                idEmpresa.text.isNullOrBlank() ||
                descripcion.text.isNullOrBlank() ||
                categoria.text.isNullOrBlank() ||
                infoReporte.text.isNullOrBlank()
            ) {
                Toast.makeText(requireContext(), "Por favor llena todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ticket = idTicket.text.toString().toRequestBody()
            val tecnico = matricula.text.toString().toRequestBody()
            val ubicacion = idUbicacion.text.toString().toRequestBody()
            val empresa = idEmpresa.text.toString().toRequestBody()
            val descripcionBody = descripcion.text.toString().toRequestBody()
            val categoriaBody = categoria.text.toString().toRequestBody()
            val infoReporteBody = infoReporte.text.toString().toRequestBody()
            val esPolizaBody = tienePoliza.isChecked.toString().toRequestBody()
            val tipoPolizaBody = tipoPoliza.text.toString().takeIf { it.isNotBlank() }?.toRequestBody()

            // Llamada a la API
            val call = ApiClient.retrofit.createReporte(
                token = "Bearer $token",
                ticket = ticket,
                tecnico = tecnico,
                ubicacion = ubicacion,
                empresa = empresa,
                descripcion = descripcionBody,
                esPoliza = esPolizaBody,
                tipoPoliza = tipoPolizaBody,
                categoria = categoriaBody,
                informacionReporte = infoReporteBody
            )

            call.enqueue(object : Callback<ReporteResponse> {
                override fun onResponse(call: Call<ReporteResponse>, response: Response<ReporteResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Reporte guardado correctamente", Toast.LENGTH_SHORT).show()
                        limpiarFormulario(view)
                    } else {
                        Log.e("API", "Error en la respuesta: ${response.code()} - ${response.errorBody()?.string()}")
                        Toast.makeText(requireContext(), "Error al guardar el reporte", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ReporteResponse>, t: Throwable) {
                    Log.e("API", "Fallo en la petición: ${t.message}")
                    Toast.makeText(requireContext(), "Fallo en la conexión", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    // Función para limpiar los campos del formulario
    private fun limpiarFormulario(view: View) {
        view.findViewById<EditText>(R.id.etIdTicket).setText("")
        view.findViewById<EditText>(R.id.etMatricula).setText("")
        view.findViewById<EditText>(R.id.etIdUbicacion).setText("")
        view.findViewById<EditText>(R.id.etIdEmpresa).setText("")
        view.findViewById<EditText>(R.id.etDescripcion).setText("")
        view.findViewById<EditText>(R.id.etTipoPoliza).setText("")
        view.findViewById<EditText>(R.id.etCategoria).setText("")
        view.findViewById<EditText>(R.id.etInfoReporte).setText("")
        view.findViewById<Switch>(R.id.switchPoliza).isChecked = false
        view.findViewById<Spinner>(R.id.spPrioridad).setSelection(0)
        view.findViewById<Spinner>(R.id.spEstado).setSelection(0)
    }
}
