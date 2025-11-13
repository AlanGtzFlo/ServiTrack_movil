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
import com.bumptech.glide.load.engine.DiskCacheStrategy

class UsuarioFragment : Fragment() {

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null
    private lateinit var imgUsuario: ImageView

    // --- AÑADIDO: Tag para logs ---
    private val FRAGMENT_TAG = "UsuarioFragment"

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

                    cambiarFotoUsuario(uri)
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

        imgUsuario = view.findViewById(R.id.imgUsuario)
        val txtUsuario = view.findViewById<TextView>(R.id.txtUsuario)
        val txtCorreo = view.findViewById<TextView>(R.id.txtCorreo)
        val txtIdUsuario = view.findViewById<TextView>(R.id.txtIdUsuario)
        val txtNombreUsuario = view.findViewById<TextView>(R.id.txtNombreUsuario)
        val txtCorreoUsuario = view.findViewById<TextView>(R.id.txtCorreoUsuario)
        val txtPuestoUsuario = view.findViewById<TextView>(R.id.txtPuestoUsuario)
        val txtTelefono = view.findViewById<TextView>(R.id.txtTelefonoUsuario)
        val txtDomicilio = view.findViewById<TextView>(R.id.txtDomicilioUsuario)

        // --- AÑADIDOS: Logs para verificar lectura de SharedPreferences ---
        Log.d(FRAGMENT_TAG, "Token recuperado de SharedPreferences: $token")
        Log.d(FRAGMENT_TAG, "ID recuperado de SharedPreferences: $id")
        // -----------------------------------------------------------------

        if (!token.isNullOrEmpty() && id != -1) {
            Log.d(FRAGMENT_TAG, "Iniciando carga de datos de usuario...")
            cargarDatosUsuario(id, token,
                onSuccess = { user ->
                    Log.d(FRAGMENT_TAG, "Datos de usuario cargados exitosamente: ${user.first_name}")
                    txtUsuario.text = user.first_name
                    txtCorreo.text = user.email
                    txtIdUsuario.text = "Matrícula:\n${user.id}"
                    txtNombreUsuario.text = "Nombre:\n${user.first_name}"
                    txtCorreoUsuario.text = "Correo:\n${user.email}"
                    txtPuestoUsuario.text = "Puesto:\n${user.user_type}"
                    txtTelefono.text = "Teléfono:\n${user.phone}"
                    txtDomicilio.text = "Domicilio:\n${user.address}"

                    Log.d(FRAGMENT_TAG, "URL foto usuario: ${user.photo}")

                    if (!user.photo.isNullOrEmpty()) {
                        Glide.with(requireContext())
                            .load(user.photo)
                            .placeholder(R.drawable.ic_user)
                            .error(R.drawable.ic_user)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(imgUsuario)
                    } else {
                        Log.d(FRAGMENT_TAG, "Usuario no tiene foto, mostrando placeholder.")
                        imgUsuario.setImageResource(R.drawable.ic_user)
                    }
                },
                onError = {
                    Log.e(FRAGMENT_TAG, "Llamada a 'onError' en cargarDatosUsuario.")
                    Toast.makeText(requireContext(), "Error al cargar usuario", Toast.LENGTH_SHORT).show()
                    imgUsuario.setImageResource(R.drawable.ic_user)
                }
            )
        } else {
            // --- AÑADIDO: Log para saber si el IF falló ---
            Log.e(FRAGMENT_TAG, "Token o ID no son válidos. No se cargarán datos.")
            Toast.makeText(requireContext(), "Error de sesión. Inicia sesión de nuevo.", Toast.LENGTH_LONG).show()
        }

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

    private fun cargarDatosUsuario(id: Int, token: String, onSuccess: (User) -> Unit, onError: () -> Unit) {
        // --- AÑADIDO: Log para verificar los parámetros de la API ---
        Log.d(FRAGMENT_TAG, "Llamando a obtenerUsuarioPorId con ID: $id y Token: Bearer $token")

        ApiClient.retrofit.obtenerUsuarioPorId(id, "Bearer $token")
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            onSuccess(it)
                        } ?: run {
                            // --- AÑADIDO: Error detallado ---
                            Log.e(FRAGMENT_TAG, "Respuesta exitosa (200) pero el cuerpo es NULO.")
                            onError()
                        }
                    } else {
                        // --- AÑADIDO: Error detallado ---
                        val errorCode = response.code()
                        val errorBody = try { response.errorBody()?.string() } catch (e: Exception) { "No se pudo leer el cuerpo del error" }
                        Log.e(FRAGMENT_TAG, "Error en la respuesta de la API. Código: $errorCode")
                        Log.e(FRAGMENT_TAG, "Cuerpo del error: $errorBody")
                        onError()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    // --- AÑADIDO: Error detallado ---
                    Log.e(FRAGMENT_TAG, "Fallo en la conexión (onFailure): ${t.message}", t)
                    onError()
                }
            })
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

            ApiClient.retrofit.cambiarFoto("Bearer $token", id, body).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Foto actualizada correctamente", Toast.LENGTH_SHORT).show()

                        // Volver a cargar datos usuario para actualizar imagen
                        cargarDatosUsuario(id, token,
                            onSuccess = { user ->
                                if (!user.photo.isNullOrEmpty()) {
                                    Glide.with(this@UsuarioFragment)
                                        .load(user.photo)
                                        .placeholder(R.drawable.ic_user)
                                        .error(R.drawable.ic_user)
                                        .circleCrop()
                                        .skipMemoryCache(true)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .into(imgUsuario)
                                }
                            },
                            onError = {
                                Toast.makeText(context, "Error al actualizar imagen", Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        // --- AÑADIDO: Error detallado ---
                        Log.e(FRAGMENT_TAG, "Error al cambiar foto. Código: ${response.code()}. Body: ${response.errorBody()?.string()}")
                        Toast.makeText(context, "Error al actualizar foto: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    // --- AÑADIDO: Error detallado ---
                    Log.e(FRAGMENT_TAG, "Fallo de conexión al cambiar foto: ${t.message}", t)
                    Toast.makeText(context, "Fallo de conexión", Toast.LENGTH_SHORT).show()
                }
            })

        } catch (e: Exception) {
            Log.e(FRAGMENT_TAG, "Error al preparar imagen: ${e.message}", e)
            Toast.makeText(context, "Error al preparar imagen: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}