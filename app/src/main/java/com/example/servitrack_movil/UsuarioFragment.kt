package com.example.servitrack_movil

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.servitrack_movil.Network.ApiClient
import com.example.servitrack_movil.Network.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.bumptech.glide.load.engine.DiskCacheStrategy
import android.widget.Toast

class UsuarioFragment : Fragment() {

    // --- ANULADO: Ya no se usará selección de imagen ---
    // private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    // private var selectedImageUri: Uri? = null
    //----------------------------------------------------

    private lateinit var imgUsuario: ImageView
    private val FRAGMENT_TAG = "UsuarioFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- SECCIÓN COMENTADA: Abrir galería (DESACTIVADA) ---
        /*
        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                selectedImageUri = data?.data

                selectedImageUri?.let { uri ->
                    Glide.with(this)
                        .load(uri)
                        .circleCrop()
                        .into(imgUsuario)

                    // cambiarFotoUsuario(uri)  // También desactivado
                }
            }
        }
        */
        // --------------------------------------------------------
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

        Log.d(FRAGMENT_TAG, "Token recuperado: $token")
        Log.d(FRAGMENT_TAG, "ID recuperado: $id")

        if (!token.isNullOrEmpty() && id != -1) {
            cargarDatosUsuario(id, token,
                onSuccess = { user ->
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
                        imgUsuario.setImageResource(R.drawable.ic_user)
                    }
                },
                onError = {
                    Toast.makeText(requireContext(), "Error al cargar usuario", Toast.LENGTH_SHORT).show()
                    imgUsuario.setImageResource(R.drawable.ic_user)
                }
            )
        } else {
            Log.e(FRAGMENT_TAG, "Token o ID inválidos.")
            Toast.makeText(requireContext(), "Error de sesión. Inicia sesión de nuevo.", Toast.LENGTH_LONG).show()
        }

        // --- SECCIÓN COMENTADA: Click para abrir galería (DESACTIVADO) ---
        /*
        imgUsuario.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }
        */
        // -------------------------------------------------------------------

        val btnCambiarContrasena: Button = view.findViewById(R.id.btnCambiarContrasena)
        btnCambiarContrasena.setOnClickListener {
            findNavController().navigate(R.id.action_usuarioFragment_to_changePasswordFragment)
        }

        return view
    }

    private fun cargarDatosUsuario(
        id: Int,
        token: String,
        onSuccess: (User) -> Unit,
        onError: () -> Unit
    ) {
        ApiClient.retrofit.obtenerUsuarioPorId(id, "Bearer $token")
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            onSuccess(it)
                        } ?: onError()
                    } else {
                        Log.e(FRAGMENT_TAG, "Error ${response.code()}: ${response.errorBody()?.string()}")
                        onError()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e(FRAGMENT_TAG, "Error conexión: ${t.message}")
                    onError()
                }
            })
    }

    // --- FUNCIÓN DE CAMBIO DE FOTO COMPLETAMENTE DESHABILITADA ---
    /*
    private fun cambiarFotoUsuario(uri: Uri) {
        ...
    }
    */
    // ----------------------------------------------------------------
}
