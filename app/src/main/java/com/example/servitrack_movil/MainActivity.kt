package com.example.servitrack_movil

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.example.servitrack_movil.Network.ApiClient
import com.example.servitrack_movil.Network.LoginRequest
import com.example.servitrack_movil.Network.LoginResponse
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    companion object {
        private const val LOGIN_TAG = "MiAppLogin"
    }

    // 1. El "launcher" se declara como una propiedad de la clase, no dentro de onCreate.
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("FCM", "Permiso concedido. Obteniendo token...")
            getAndSendFcmToken()
        } else {
            Log.w("FCM", "Permiso de notificación denegado.")
            // Aún si se deniega el permiso, el usuario debe poder continuar.
            navigateToMenu()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val edtUsuario = findViewById<EditText>(R.id.edtIdUsuario)
        val edtPass = findViewById<EditText>(R.id.edtPass)
        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token = prefs.getString("access_token", null)

        // Si el usuario ya inició sesión, ve directo al menú.
        if (!token.isNullOrEmpty()) {
            navigateToMenu()
            return // Importante para no continuar ejecutando el código de onCreate
        }

        btnIngresar.setOnClickListener {
            val usuario = edtUsuario.text.toString().trim()
            val password = edtPass.text.toString().trim()

            if (usuario.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // *** INICIO DE SESIÓN LOCAL REINTEGRADO ***
            if (usuario == "user" && password == "1234") {
                Toast.makeText(this, "Inicio de sesión local exitoso", Toast.LENGTH_SHORT).show()
                // NOTA: Si quieres que este usuario local también se registre para notificaciones,
                // es importante llamar a esta función aquí también.
                initiateNotificationFlow()
                return@setOnClickListener // Salimos para no hacer la llamada a la API
            }

            val loginRequest = LoginRequest(email = usuario, password = password)
            val call = ApiClient.retrofit.login(loginRequest)

            call.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val loginData = response.body()!!

                        // Guardar datos del usuario y tokens en SharedPreferences
                        val sharedPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                        with(sharedPrefs.edit()) {
                            putInt("id", loginData.user.id)
                            putString("nombre", loginData.user.first_name)
                            putString("correo", loginData.user.email)
                            putString("rol", loginData.user.user_type)
                            putString("access_token", loginData.access)
                            putString("refresh_token", loginData.refresh)
                            apply()
                        }

                        // --- LOGS DE VERIFICACIÓN AÑADIDOS ---
                        // Revisa el Logcat (filtrando por "MiAppLogin") para ver estos valores.
                        Log.d(LOGIN_TAG, "Datos guardados en SharedPreferences:")
                        Log.d(LOGIN_TAG, "ID: ${loginData.user.id}")
                        Log.d(LOGIN_TAG, "Access Token: ${loginData.access}")
                        // ------------------------------------

                        Toast.makeText(this@MainActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

                        // 2. Este es el momento correcto para iniciar el flujo de notificaciones.
                        initiateNotificationFlow()

                    } else {

                        val errorBody = try { response.errorBody()?.string() } catch (e: Exception) { "No se pudo leer el error" }
                        Log.e(LOGIN_TAG, "Login FALLIDO. Código: ${response.code()}")
                        Log.e(LOGIN_TAG, "Cuerpo del error del servidor: $errorBody")

                        Toast.makeText(this@MainActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e(LOGIN_TAG, "Error de conexión: ${t.message}", t)
                    Toast.makeText(this@MainActivity, "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    // 3. Todas estas funciones son ahora métodos de MainActivity.
    private fun initiateNotificationFlow() {
        // En Android 13 (TIRAMISU) o superior, se necesita permiso explícito.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                // El permiso ya está concedido.
                getAndSendFcmToken()
            } else {
                // Solicita el permiso.
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // En versiones anteriores, no se requiere permiso en tiempo de ejecución.
            getAndSendFcmToken()
        }
    }

    private fun getAndSendFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val fcmToken = task.result
                Log.d("FCM", "Token de FCM obtenido: $fcmToken")
                sendTokenToServer(fcmToken)
            } else {
                Log.w("FCM", "No se pudo obtener el token de FCM.", task.exception)
                // Si falla la obtención del token, igual navega al menú para no bloquear al usuario.
                navigateToMenu()
            }
        }
    }

    private fun sendTokenToServer(token: String) {
        // AQUÍ DEBES IMPLEMENTAR LA LLAMADA DE RED CON RETROFIT PARA ENVIAR EL TOKEN
        Log.d("FCM", "Enviando token ($token) al servidor... (Lógica pendiente)")

        // Una vez que la lógica de envío termine (incluso si falla), navega al menú.
        navigateToMenu()
    }

    private fun navigateToMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish() // Cierra MainActivity para que el usuario no pueda volver atrás.
    }
}