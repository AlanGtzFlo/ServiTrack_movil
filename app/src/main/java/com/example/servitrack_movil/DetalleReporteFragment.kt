package com.example.servitrack_movil

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.servitrack_movil.Network.ApiClient
import com.example.servitrack_movil.databinding.FragmentDetalleReporteBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import android.content.Context
import com.example.servitrack_movil.Network.UbicacionResponse
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.fragment.findNavController



class DetalleReporteFragment : Fragment() {

    private var _binding: FragmentDetalleReporteBinding? = null
    private val binding get() = _binding!!

    private val args: DetalleReporteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalleReporteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reporte = args.reporte

        binding.btnListadoMensajes.setOnClickListener {
            val action = DetalleReporteFragmentDirections
                .actionDetalleReporteFragmentToListadoMensajesFragment(reporteId = reporte.id, reporteNombre = reporte.titulo)
            findNavController().navigate(action)
        }

        binding.txtTicketId.text = "ID:\n${reporte.id}"
        binding.txtTitulo.text = "Título:\n${reporte.titulo}"
        binding.txtDescripcion.text = "Descripción:\n${reporte.descripcion}"
        binding.txtPrioridad.text = "Prioridad:\n${reporte.prioridad}"
        binding.txtEstado.text = "Estado:\n${reporte.estado}"
        binding.txtFecha.text = "Fecha:\n${reporte.fecha}"
        binding.txtCreador.text = "Id del Creador:\n${reporte.creador}"
        binding.txtTecnico.text = "Id del Técnico:\n${reporte.tecnico}"

        obtenerNombreUbicacion(reporte.ubicacion.toInt())

        // Botón para generar PDF
        binding.btnGenerarPDF.setOnClickListener {
            exportarPdf(reporte.id)
        }
    }


    private fun exportarPdf(idReporte: Int) {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null)

        if (token == null) {
            Toast.makeText(requireContext(), "Token no disponible", Toast.LENGTH_SHORT).show()
            return
        }

        val bearerToken = "Bearer $token"

        ApiClient.retrofit.exportarPdf(idReporte, bearerToken)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    // Log para depurar
                    android.util.Log.d("API_RESPONSE", "Código: ${response.code()}")

                    if (response.isSuccessful && response.body() != null) {
                        guardarPdfEnDispositivo(response.body()!!)
                        Toast.makeText(
                            requireContext(),
                            "PDF generado correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error al generar PDF. Código: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Error de conexión: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }


    private fun guardarPdfEnDispositivo(body: ResponseBody) {
        try {
            val file = File(
                requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                "reporte_${System.currentTimeMillis()}.pdf"
            )
            val fos = FileOutputStream(file)
            fos.write(body.bytes())
            fos.close()
            Toast.makeText(
                requireContext(),
                "Archivo guardado en: ${file.absolutePath}",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error al guardar archivo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun obtenerNombreUbicacion(id: Int) {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null) ?: ""
        val authHeader = "Bearer $token"
        ApiClient.retrofit.getUbicaciones(authHeader)
            .enqueue(object : Callback<List<UbicacionResponse>> {
                override fun onResponse(
                    call: Call<List<UbicacionResponse>>,
                    response: Response<List<UbicacionResponse>>
                ) {
                    if (!isAdded || _binding == null) return  // Evita crash si fragmento no está activo

                    if (response.isSuccessful) {
                        val ubicacion = response.body()?.find { it.id == id }
                        binding.txtUbicacion.text =
                            "Ubicación: ${ubicacion?.nombre ?: "Desconocida"}"
                    } else {
                        binding.txtUbicacion.text = "Ubicación: Desconocida"
                    }
                }

                override fun onFailure(call: Call<List<UbicacionResponse>>, t: Throwable) {
                    if (!isAdded || _binding == null) return
                    binding.txtUbicacion.text = "Ubicación: Error"
                }
            })
    }

    // Fuera de todas las funciones, dentro de la clase:
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}