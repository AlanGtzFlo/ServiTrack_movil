package com.example.servitrack_movil

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.servitrack_movil.Network.ApiClient
import com.example.servitrack_movil.Network.User
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.net.URL

class UsuarioFragment : Fragment() {

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null
    private lateinit var imgUsuario: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                selectedImageUri = data?.data

                selectedImageUri?.let { uri ->
                    Glide.with(this)
                        .load(uri)
                        .circleCrop()
                        .into(imgUsuario)

                    cambiarFotoUsuario(uri)  // Llamamos al método corregido
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_usuario, container, false)

        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null)
        val id = prefs.getInt("id", -1)

        // Referenciar elementos UI
        imgUsuario = view.findViewById(R.id.imgUsuario)
        val txtUsuario = view.findViewById<TextView>(R.id.txtUsuario)
        val txtCorreo = view.findViewById<TextView>(R.id.txtCorreo)
        val txtIdUsuario = view.findViewById<TextView>(R.id.txtIdUsuario)
        val txtNombreUsuario = view.findViewById<TextView>(R.id.txtNombreUsuario)
        val txtCorreoUsuario = view.findViewById<TextView>(R.id.txtCorreoUsuario)
        val txtPuestoUsuario = view.findViewById<TextView>(R.id.txtPuestoUsuario)
        val txtFechaIngreso = view.findViewById<TextView>(R.id.txtFechaIngresoUsuario)
        val txtTelefono = view.findViewById<TextView>(R.id.txtTelefonoUsuario)
        val txtDomicilio = view.findViewById<TextView>(R.id.txtDomicilioUsuario)

        // Nombre archivo local para la foto del usuario
        val nombreArchivoFoto = "foto_usuario_${id}.jpg"

        if (!token.isNullOrEmpty() && id != -1) {
            ApiClient.retrofit.obtenerUsuarioPorId(id, "Bearer $token")
                .enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            val user = response.body()

                            txtUsuario.text = user?.nombre ?: "Sin nombre"
                            txtCorreo.text = user?.correo ?: "Sin correo"
                            txtIdUsuario.text = "Matrícula:\n${user?.id ?: "Desconocido"}"
                            txtNombreUsuario.text = "Nombre:\n${user?.nombre ?: "Desconocido"}"
                            txtCorreoUsuario.text = "Correo:\n${user?.correo ?: "Desconocido"}"
                            txtPuestoUsuario.text = "Puesto:\n${user?.rol ?: "Desconocido"}"
                            txtFechaIngreso.text = "Ingreso:\n${user?.fecha_registro?.substringBefore("T") ?: "Desconocido"}"
                            txtTelefono.text = "Teléfono:\n${user?.telefono ?: "Sin teléfono"}"
                            txtDomicilio.text = "Domicilio:\n${user?.direccion ?: "Sin domicilio"}"

                            val archivoLocal = File(requireContext().filesDir, nombreArchivoFoto)
                            if (archivoLocal.exists()) {
                                Log.d("UsuarioFragment", "Archivo local encontrado: ${archivoLocal.absolutePath}, tamaño: ${archivoLocal.length()} bytes")
                                Glide.with(requireContext())
                                    .load(archivoLocal)
                                    .placeholder(R.drawable.ic_user)
                                    .error(R.drawable.ic_user)
                                    .circleCrop()
                                    .into(imgUsuario)
                            } else if (!user?.foto.isNullOrEmpty()) {
                                Log.d("UsuarioFragment", "Archivo local NO existe, cargando desde URL y guardando localmente")
                                Glide.with(requireContext())
                                    .load(user.foto)
                                    .placeholder(R.drawable.ic_user)
                                    .error(R.drawable.ic_user)
                                    .circleCrop()
                                    .into(imgUsuario)

                                // Guardar imagen localmente para la próxima vez
                                guardarImagenLocalmente(requireContext(), user.foto, nombreArchivoFoto) {
                                    Log.d("UsuarioFragment", "Imagen guardada localmente en ${it.absolutePath}")
                                }
                            } else {
                                // No hay foto ni local ni remota, carga placeholder
                                imgUsuario.setImageResource(R.drawable.ic_user)
                            }
                        } else {
                            Log.e("UsuarioFragment", "Error al obtener usuario: ${response.code()}")
                            Toast.makeText(requireContext(), "Error al cargar usuario", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Log.e("UsuarioFragment", "Fallo de conexión: ${t.message}", t)
                        Toast.makeText(requireContext(), "Error de red", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        // Clic para cambiar imagen
        imgUsuario.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        val btnCambiarContrasena: Button = view.findViewById(R.id.btnCambiarContrasena)
        btnCambiarContrasena.setOnClickListener {
            findNavController().navigate(R.id.action_usuarioFragment_to_changePasswordFragment)
        }

        return view
    }

    private fun guardarImagenLocalmente(context: Context, url: String, nombreArchivo: String, onSuccess: (File) -> Unit) {
        Thread {
            try {
                val input = URL(url).openStream()
                val archivo = File(context.filesDir, nombreArchivo)
                archivo.outputStream().use { output ->
                    input.copyTo(output)
                }
                activity?.runOnUiThread {
                    onSuccess(archivo)
                }
            } catch (e: Exception) {
                Log.e("GuardarImagen", "Error: ${e.message}")
            }
        }.start()
    }

    private fun cambiarFotoUsuario(uri: Uri) {
        val context = requireContext()
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null)
        val id = prefs.getInt("id", -1)

        if (token.isNullOrEmpty() || id == -1) {
            Toast.makeText(context, "No hay sesión válida", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("profile", ".jpg", context.cacheDir)
            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("foto", tempFile.name, requestFile)

            val call = ApiClient.retrofit.cambiarFoto("Bearer $token", id, body)
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Foto actualizada correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        val error = response.errorBody()?.string()
                        Log.e("Foto", "Error: ${response.code()} - $error")
                        Toast.makeText(context, "Error al actualizar foto: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("Foto", "Fallo de red: ${t.message}", t)
                    Toast.makeText(context, "Fallo de conexión", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            Toast.makeText(context, "Error al preparar imagen: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

}
