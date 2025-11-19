package com.example.servitrack_movil

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.servitrack_movil.Network.ApiClient
import com.example.servitrack_movil.Network.ChangePasswordRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CCFragment : Fragment() {

    private lateinit var edtNueva: EditText
    private lateinit var edtConfirmar: EditText
    private lateinit var edtAnterior: EditText
    private lateinit var btnGuardar: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_c_c, container, false)

        edtAnterior = view.findViewById(R.id.edtPasswordAnterior)
        edtNueva = view.findViewById(R.id.edtPasswordNueva)
        edtConfirmar = view.findViewById(R.id.edtPasswordConfirmar)
        btnGuardar = view.findViewById(R.id.btnGuardarCambio)

        btnGuardar.setOnClickListener {
            val anterior = edtAnterior.text.toString()
            val nueva = edtNueva.text.toString()
            val confirmar = edtConfirmar.text.toString()

            if (nueva != confirmar) {
                Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (nueva.length < 8) {
                Toast.makeText(
                    requireContext(),
                    "La contraseña debe tener al menos 8 caracteres",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Recuperar token
            val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val token = prefs.getString("access_token", null)

            if (token.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "No hay sesión activa", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Construcción correcta del request
            val request = ChangePasswordRequest(
                current_password = anterior,
                new_password = nueva,
                confirm_password = confirmar
            )

            ApiClient.retrofit.changePassword("Bearer $token", request)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Contraseña actualizada correctamente",
                                Toast.LENGTH_LONG
                            ).show()
                            parentFragmentManager.popBackStack()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Error al cambiar contraseña (${response.code()})",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(
                            requireContext(),
                            "Error de red: ${t.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
        return view
    }
    }
