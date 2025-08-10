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
    private val categoriasBackend = listOf("en_asignacion", "preventivo", "predictivo", "correctivo")

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
        val etTecnico = view.findViewById<EditText>(R.id.etTecnico) // Campo editable para técnico
        val spUbicacion = view.findViewById<Spinner>(R.id.spUbicacion)
        val spEmpresa = view.findViewById<Spinner>(R.id.spEmpresa)
        val spTipoPoliza = view.findViewById<Spinner>(R.id.spTipoPoliza)
        val spCategoria = view.findViewById<Spinner>(R.id.spCategoria)
        val descripcion = view.findViewById<EditText>(R.id.etDescripcion)
        val infoReporte = view.findViewById<EditText>(R.id.etInfoReporte)
        val tienePoliza = view.findViewById<Switch>(R.id.switchPoliza)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardar)

        // Opcional: Puedes poner un texto inicial en técnico si quieres
        // etTecnico.setText("Nombre del técnico")

        // Inicializar spinners tipo póliza y categoría con opciones fijas
        spTipoPoliza.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, tiposPoliza)
        spCategoria.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categoriasVisibles)

        // Mostrar/ocultar spinner Tipo póliza según switch
        tienePoliza.setOnCheckedChangeListener { _, isChecked ->
            spTipoPoliza.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (!isChecked) spTipoPoliza.setSelection(0)
        }
        spTipoPoliza.visibility = if (tienePoliza.isChecked) View.VISIBLE else View.GONE

        // Cargar datos dinámicos para Tickets, Ubicaciones, Empresas
        cargarTickets(token, spTicket)
        cargarUbicaciones(token, spUbicacion)
        cargarEmpresas(token, spEmpresa)

        btnGuardar.setOnClickListener {
            if (!validarCampos(spTicket, spUbicacion, spEmpresa, descripcion, infoReporte, tienePoliza, spTipoPoliza, etTecnico)) return@setOnClickListener

            val ticketId = tickets[spTicket.selectedItemPosition].id.toString().toRequestBody()
            val tecnicoNombre = etTecnico.text.toString().toRequestBody() // texto manual
            val ubicacionId = ubicaciones[spUbicacion.selectedItemPosition].id.toString().toRequestBody()
            val empresaId = empresas[spEmpresa.selectedItemPosition].id.toString().toRequestBody()
            val descripcionBody = descripcion.text.toString().toRequestBody()
            val categoriaBody = categoriasBackend[spCategoria.selectedItemPosition].toRequestBody()
            val infoReporteBody = infoReporte.text.toString().toRequestBody()
            val esPolizaBody = tienePoliza.isChecked.toString().toRequestBody()
            val tipoPolizaBody = if (tienePoliza.isChecked) tiposPoliza[spTipoPoliza.selectedItemPosition].toRequestBody() else null

            ApiClient.retrofit.createReporte(
                token = "Bearer $token",
                ticket = ticketId,
                tecnico = tecnicoNombre,
                ubicacion = ubicacionId,
                empresa = empresaId,
                descripcion = descripcionBody,
                esPoliza = esPolizaBody,
                tipoPoliza = tipoPolizaBody,
                categoria = categoriaBody,
                informacionReporte = infoReporteBody,
                foto = null // Aquí puedes agregar la lógica para manejar fotos si es necesario
            ).enqueue(object : Callback<ReporteResponse> {
                override fun onResponse(call: Call<ReporteResponse>, response: Response<ReporteResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Reporte guardado correctamente", Toast.LENGTH_SHORT).show()
                        limpiarFormulario(view)
                    } else {
                        Log.e("API", "Error en respuesta: ${response.code()} - ${response.errorBody()?.string()}")
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

    private fun cargarTickets(token: String, spinner: Spinner) {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null) ?: ""
        val authHeader = "Bearer $token"
        ApiClient.retrofit.getTickets(authHeader).enqueue(object : Callback<List<TicketResponse>> {
            override fun onResponse(call: Call<List<TicketResponse>>, response: Response<List<TicketResponse>>) {
                if (response.isSuccessful) {
                    tickets = response.body() ?: emptyList()
                    val nombres = tickets.map { it.titulo ?: "Ticket ${it.id}" }
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
                    val nombres = ubicaciones.map { it.nombre ?: "Ubicación ${it.id}" }
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
                    val nombres = empresas.map { it.nombre ?: "Empresa ${it.id}" }
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
        view.findViewById<Spinner>(R.id.spUbicacion).setSelection(0)
        view.findViewById<Spinner>(R.id.spEmpresa).setSelection(0)
        view.findViewById<EditText>(R.id.etTecnico).setText("")
        view.findViewById<EditText>(R.id.etDescripcion).setText("")
        view.findViewById<Switch>(R.id.switchPoliza).isChecked = false
        view.findViewById<Spinner>(R.id.spTipoPoliza).setSelection(0)
        view.findViewById<Spinner>(R.id.spCategoria).setSelection(0)
        view.findViewById<EditText>(R.id.etInfoReporte).setText("")
    }
}