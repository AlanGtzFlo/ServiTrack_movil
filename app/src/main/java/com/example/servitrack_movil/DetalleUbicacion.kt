package com.example.servitrack_movil

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.servitrack_movil.Network.ApiClient
import com.example.servitrack_movil.Network.Cliente
import com.example.servitrack_movil.Network.EmpresaResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalleUbicacionFragment : Fragment() {

    private lateinit var ubicacion: Ubicacion

    private lateinit var txtClienteNombre: TextView
    private lateinit var txtEmpresaNombre: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            ubicacion = DetalleUbicacionFragmentArgs.fromBundle(it).ubicacion
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detalle_ubicacion, container, false)

        // Mostrar datos básicos
        view.findViewById<TextView>(R.id.txtUbicacionId).text = "ID:\n${ubicacion.id}"
        view.findViewById<TextView>(R.id.txtNombreDetalle).text = "Nombre:\n${ubicacion.name}"
        view.findViewById<TextView>(R.id.txtDireccionDetalle).text = "Dirección:\n${ubicacion.address}"
        view.findViewById<TextView>(R.id.txtContacto).text = "Contacto:\n${ubicacion.company}"

        // Los TextView donde mostraremos los nombres recuperados
        txtClienteNombre = view.findViewById(R.id.txtCliente_id)
        txtEmpresaNombre = view.findViewById(R.id.txtEmpresa)

        return view
    }

    private fun obtenerNombreCliente(clienteId: Int) {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null) ?: ""
        val authHeader = "Bearer $token"
        ApiClient.retrofit.getCliente(clienteId, authHeader).enqueue(object : Callback<Cliente> {
            override fun onResponse(call: Call<Cliente>, response: Response<Cliente>) {
                if (response.isSuccessful) {
                    val cliente = response.body()
                    txtClienteNombre.text = "Cliente:\n${cliente?.nombre ?: "Desconocido"}"
                } else {
                    txtClienteNombre.text = "Cliente:\nDesconocido"
                }
            }

            override fun onFailure(call: Call<Cliente>, t: Throwable) {
                txtClienteNombre.text = "Cliente: Error"
            }
        })
    }

    private fun obtenerNombreEmpresa(empresaId: Int) {

        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null) ?: ""
        val authHeader = "Bearer $token"

        ApiClient.retrofit.getEmpresas(authHeader).enqueue(object : Callback<List<EmpresaResponse>> {
            override fun onResponse(
                call: Call<List<EmpresaResponse>>,
                response: Response<List<EmpresaResponse>>
            ) {
                if (response.isSuccessful) {
                    val empresa = response.body()?.find { it.id == empresaId }
                    txtEmpresaNombre.text = "Empresa:\n${empresa?.name ?: "Desconocida"}"
                } else {
                    txtEmpresaNombre.text = "Empresa:\nDesconocida"
                }
            }

            override fun onFailure(call: Call<List<EmpresaResponse>>, t: Throwable) {
                txtEmpresaNombre.text = "Empresa: Error"
            }
        })
    }
}

