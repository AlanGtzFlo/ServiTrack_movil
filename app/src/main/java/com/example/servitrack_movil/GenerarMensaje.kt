package com.example.servitrack_movil

import android.content.Context
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.servitrack_movil.Network.ApiClient
import com.example.servitrack_movil.Network.ReporteResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
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
    private var imagenBase64: String? = null

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

        btnImagen.setOnClickListener {
            abrirGaleria()
        }

        btnGuardar.setOnClickListener {
            if (reporteSeleccionadoId == null) {
                Toast.makeText(requireContext(), "Seleccione un reporte", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val descripcion = etDescripcion.text.toString().trim()
            if (descripcion.isEmpty()) {
                Toast.makeText(requireContext(), "Ingrese el mensaje", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            enviarMensaje(reporteSeleccionadoId!!, descripcion, token)
        }
    }

    private fun cargarReportes(token: String) {
        ApiClient.retrofit.getReportes(token).enqueue(object : Callback<List<ReporteResponse>> {
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
                    if (index != -1) {
                        spinnerReporte.setSelection(index)
                    }

                    spinnerReporte.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                reporteSeleccionadoId = listaReportes[position].id
                                reporteNombre = listaReportes[position].ticket.toString()
                            }

                            override fun onNothingSelected(parent: AdapterView<*>) {
                                reporteSeleccionadoId = null
                                reporteNombre = null
                            }
                        }

                } else {
                    Toast.makeText(requireContext(), "Error al cargar reportes", Toast.LENGTH_SHORT).show()
                    Log.e("GenerarMensaje", "Error cargar reportes: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<ReporteResponse>>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    "Fallo de conexión: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("GenerarMensaje", "Error cargar reportes: ${t.message}")
            }
        })
    }

    private fun enviarMensaje(reporteId: Int, descripcion: String, token: String) {
        val reportePart = reporteId.toString().toRequestBody("text/plain".toMediaType())
        val mensajePart = descripcion.toRequestBody("text/plain".toMediaType())

        val imagenPart: MultipartBody.Part? = if (imagenSeleccionadaUri != null) {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(imagenSeleccionadaUri!!)
            val bytes = inputStream?.readBytes()
            val requestFile = bytes?.toRequestBody("image/*".toMediaTypeOrNull())
            if (requestFile != null) {
                MultipartBody.Part.createFormData("imagen", "imagen.jpg", requestFile)
            } else {
                null
            }
        } else {
            null
        }

        ApiClient.retrofit.createMensaje("Bearer $token", reportePart, mensajePart, imagenPart)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Mensaje guardado", Toast.LENGTH_SHORT).show()
                        etDescripcion.text.clear()
                        imgMensaje.setImageResource(R.drawable.icon_message)
                        imagenSeleccionadaUri = null
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("GenerarMensaje", "Error en response: $errorBody")
                        Toast.makeText(requireContext(), "Error al guardar mensaje", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("GenerarMensaje", "Error de conexión", t)
                }
            })
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                imagenSeleccionadaUri = uri
                imgMensaje.setImageURI(uri)

                val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                if (bytes != null) {
                    imagenBase64 = Base64.encodeToString(bytes, Base64.DEFAULT)
                    Log.d("GenerarMensaje", "Imagen convertida a base64, tamaño: ${imagenBase64?.length}")
                }
            }
        }
    }
}
