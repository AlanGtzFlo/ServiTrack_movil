package com.example.servitrack_movil

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.servitrack_movil.Network.ApiClient
import com.example.servitrack_movil.Network.ReporteResponse
import com.example.servitrack_movil.Network.MensajeResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.net.Uri
import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import java.io.InputStream

class GenerarMensaje : Fragment() {

    private val args: GenerarMensajeArgs by navArgs()
    private val PICK_IMAGE_REQUEST = 1001

    private lateinit var spinnerReporte: Spinner
    private lateinit var etDescripcion: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnImagen: Button
    private lateinit var imgMensaje: ImageView

    private var listaReportes = listOf<ReporteResponse>()
    private var reporteSeleccionadoId: Int? = null
    private var reporteNombre: String? = null
    private var imagenSeleccionadaUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_generar_mensaje, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnImagen = view.findViewById(R.id.btnImagen)
        imgMensaje = view.findViewById(R.id.imgMensaje)
        spinnerReporte = view.findViewById(R.id.spinnerReporte)
        etDescripcion = view.findViewById(R.id.etDescripcion)
        btnGuardar = view.findViewById(R.id.btnGuardar)

        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null) ?: ""

        reporteSeleccionadoId = args.reporteId
        reporteNombre = args.reporteNombre

        cargarReportes("Bearer $token")

        btnImagen.setOnClickListener { abrirGaleria() }

        btnGuardar.setOnClickListener {
            val descripcion = etDescripcion.text.toString().trim()

            if (reporteSeleccionadoId == null) {
                Toast.makeText(requireContext(), "Seleccione un reporte", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (descripcion.isEmpty()) {
                Toast.makeText(requireContext(), "Ingrese un mensaje", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            enviarMensaje(
                reporteId = reporteSeleccionadoId!!,
                descripcion = descripcion,
                token = token
            )
        }
    }

    private fun cargarReportes(token: String) {
        ApiClient.retrofit.getReportes(token)
            .enqueue(object : Callback<List<ReporteResponse>> {
                override fun onResponse(
                    call: Call<List<ReporteResponse>>,
                    response: Response<List<ReporteResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        listaReportes = response.body()!!
                        val nombres = listaReportes.map { it.ticket }

                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            nombres
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerReporte.adapter = adapter

                        val index = listaReportes.indexOfFirst { it.id == reporteSeleccionadoId }
                        if (index != -1) spinnerReporte.setSelection(index)

                        spinnerReporte.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    reporteSeleccionadoId = listaReportes[position].id
                                }

                                override fun onNothingSelected(parent: AdapterView<*>) {
                                    reporteSeleccionadoId = null
                                }
                            }

                    } else {
                        Toast.makeText(requireContext(), "Error al cargar reportes", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<ReporteResponse>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun enviarMensaje(reporteId: Int, descripcion: String, token: String) {
        Log.d("DEBUG_IMG", "Iniciando envío. URI: $imagenSeleccionadaUri")

        // 1. Preparar el Texto
        val messagePart: RequestBody = descripcion.toRequestBody("text/plain".toMediaType())

        // 2. Preparar la Imagen
        var photoPart: MultipartBody.Part? = null

        imagenSeleccionadaUri?.let { uri ->
            try {
                val contentResolver = requireContext().contentResolver
                // Obtener el tipo real de la imagen (ej. image/jpeg o image/png)
                val type = contentResolver.getType(uri) ?: "image/jpeg"

                val inputStream = contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close() // Importante cerrar el stream

                if (bytes != null) {
                    Log.d("DEBUG_IMG", "Imagen leída. Bytes: ${bytes.size}, Tipo: $type")

                    // Usar el tipo real en lugar de "image/*"
                    val requestFile = bytes.toRequestBody(type.toMediaTypeOrNull())

                    // "photo" es el nombre del campo que espera el servidor.
                    // Asegúrate que en Postman/Backend se llame así.
                    photoPart = MultipartBody.Part.createFormData(
                        "image",
                        "upload.jpg", // Nombre de archivo genérico
                        requestFile
                    )
                } else {
                    Log.e("DEBUG_IMG", "Error: Los bytes de la imagen son null")
                }
            } catch (e: Exception) {
                Log.e("DEBUG_IMG", "Error leyendo imagen: ${e.message}")
            }
        }

        // 3. Llamada a Retrofit
        ApiClient.retrofit.addMessageToReport(
            token = "Bearer $token",
            reportId = reporteId,
            message = messagePart,
            image = photoPart
        ).enqueue(object : Callback<MensajeResponse> {
            override fun onResponse(call: Call<MensajeResponse>, response: Response<MensajeResponse>) {
                if (response.isSuccessful) {
                    Log.d("DEBUG_API", "Éxito: ${response.body()}")
                    Toast.makeText(requireContext(), "Enviado correctamente", Toast.LENGTH_SHORT).show()
                    // Limpiar UI
                    etDescripcion.text.clear()
                    imagenSeleccionadaUri = null
                    imgMensaje.setImageResource(R.drawable.icono_nuevomensaje) // Asegúrate que este recurso exista
                } else {
                    // IMPRIMIR EL ERROR DEL SERVIDOR
                    val errorBody = response.errorBody()?.string()
                    Log.e("DEBUG_API", "Error del servidor (${response.code()}): $errorBody")
                    Toast.makeText(requireContext(), "Error: $errorBody", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<MensajeResponse>, t: Throwable) {
                Log.e("DEBUG_API", "Fallo de red: ${t.message}")
                t.printStackTrace()
            }
        })
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                imagenSeleccionadaUri = it
                imgMensaje.setImageURI(it)
            }
        }
    }
}
