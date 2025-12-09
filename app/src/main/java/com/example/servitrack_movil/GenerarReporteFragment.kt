package com.example.servitrack_movil

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.servitrack_movil.Network.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GenerarReporteFragment : Fragment() {

    private lateinit var tickets: List<TicketResponse>
    private lateinit var ubicaciones: List<UbicacionResponse>
    private lateinit var empresas: List<EmpresaResponse>

    private val tiposPoliza = listOf("Completa", "Preventivos", "Correctivos")

    private val categoriasVisibles = listOf("En asignación", "Preventivo", "Predictivo", "Correctivo")
    private val categoriasBackend = listOf("en asignación", "preventivo", "predictivo", "correctivo")

    // Extensión para convertir String a RequestBody
    private fun String.toRequestBody(): RequestBody = this.toRequestBody("text/plain".toMediaType())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_generar_reporte, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null)

        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "No se encontró sesión activa", Toast.LENGTH_SHORT).show()
            return
        }

        // Referencias a vistas
        val spTicket = view.findViewById<Spinner>(R.id.spTicket)
        val descripcion = view.findViewById<EditText>(R.id.etDescripcion)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardar)


        cargarTickets(token, spTicket)

        btnGuardar.setOnClickListener {

            val ticketSeleccionado = tickets[spTicket.selectedItemPosition].id
            val ticketIdBody = ticketSeleccionado.toString().toRequestBody()

            // Usamos el campo "Descripción" como ticket_title
            val ticketTitleBody = descripcion.text.toString().toRequestBody()

            // Generamos created_at automáticamente
            val fechaActual = java.time.OffsetDateTime.now().toString()
            val createdAtBody = fechaActual.toRequestBody()

            // Al crear un reporte el backend espera una lista vacía
            val messagesBody = "[]".toRequestBody()

            ApiClient.retrofit.createReporte(
                token = "Bearer $token",
                ticket = ticketIdBody,
                ticketTitle = ticketTitleBody,
                createdAt = createdAtBody,
                messages = messagesBody
            ).enqueue(object : Callback<ReporteResponse> {
                override fun onResponse(
                    call: Call<ReporteResponse>,
                    response: Response<ReporteResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Reporte creado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("API", "Error: ${response.code()} - ${response.errorBody()?.string()}")
                        Toast.makeText(requireContext(), "Error al crear reporte", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ReporteResponse>, t: Throwable) {
                    Log.e("API", "Fallo: ${t.message}")
                    Toast.makeText(requireContext(), "Error en la conexión", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun cargarTickets(token: String, spinner: Spinner) {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null) ?: ""
        val authHeader = "Bearer $token"
        ApiClient.retrofit.getTickets(authHeader).enqueue(object : Callback<List<TicketResponse>> {
            override fun onResponse(call: Call<List<TicketResponse>>, response: Response<List<TicketResponse>>) {
                if (response.isSuccessful) {
                    tickets = response.body() ?: emptyList()
                    val nombres = tickets.map { it.title ?: "Ticket ${it.id}" }
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, nombres)
                    spinner.adapter = adapter
                } else {
                    Toast.makeText(requireContext(), "Error cargando tickets", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<TicketResponse>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error cargando tickets", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cargarUbicaciones(token: String, spinner: Spinner) {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null) ?: ""
        val authHeader = "Bearer $token"
        ApiClient.retrofit.getUbicaciones(authHeader).enqueue(object : Callback<List<UbicacionResponse>> {
            override fun onResponse(call: Call<List<UbicacionResponse>>, response: Response<List<UbicacionResponse>>) {
                if (response.isSuccessful) {
                    ubicaciones = response.body() ?: emptyList()
                    val nombres = ubicaciones.map { it.name ?: "Ubicación ${it.id}" }
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, nombres)
                    spinner.adapter = adapter
                } else {
                    Toast.makeText(requireContext(), "Error cargando ubicaciones", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<UbicacionResponse>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error cargando ubicaciones", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun cargarEmpresas(token: String, spinner: Spinner) {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null) ?: ""
        val authHeader = "Bearer $token"
        ApiClient.retrofit.getEmpresas(authHeader).enqueue(object : Callback<List<EmpresaResponse>> {
            override fun onResponse(call: Call<List<EmpresaResponse>>, response: Response<List<EmpresaResponse>>) {
                if (response.isSuccessful) {
                    empresas = response.body() ?: emptyList()
                    val nombres = empresas.map { it.name ?: "Empresa ${it.id}" }
                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, nombres)
                    spinner.adapter = adapter
                } else {
                    Toast.makeText(requireContext(), "Error cargando empresas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<EmpresaResponse>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error cargando empresas", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun validarCampos(
        spTicket: Spinner,
        spUbicacion: Spinner,
        spEmpresa: Spinner,
        descripcion: EditText,
        infoReporte: EditText,
        switchPoliza: Switch,
        spTipoPoliza: Spinner,
        etTecnico: EditText
    ): Boolean {
        if (spTicket.selectedItemPosition == AdapterView.INVALID_POSITION) {
            Toast.makeText(requireContext(), "Selecciona un ticket", Toast.LENGTH_SHORT).show()
            return false
        }
        if (spUbicacion.selectedItemPosition == AdapterView.INVALID_POSITION) {
            Toast.makeText(requireContext(), "Selecciona una ubicación", Toast.LENGTH_SHORT).show()
            return false
        }
        if (spEmpresa.selectedItemPosition == AdapterView.INVALID_POSITION) {
            Toast.makeText(requireContext(), "Selecciona una empresa", Toast.LENGTH_SHORT).show()
            return false
        }
        if (descripcion.text.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Ingresa una descripción", Toast.LENGTH_SHORT).show()
            return false
        }
        if (infoReporte.text.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Ingresa información del reporte", Toast.LENGTH_SHORT).show()
            return false
        }
        if (switchPoliza.isChecked && spTipoPoliza.selectedItemPosition == AdapterView.INVALID_POSITION) {
            Toast.makeText(requireContext(), "Selecciona un tipo de póliza", Toast.LENGTH_SHORT).show()
            return false
        }
        if (etTecnico.text.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Ingresa el nombre del técnico", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun limpiarFormulario(view: View) {
        view.findViewById<Spinner>(R.id.spTicket).setSelection(0)
        view.findViewById<EditText>(R.id.etDescripcion).setText("")
    }
}