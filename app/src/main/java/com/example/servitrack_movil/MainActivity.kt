package com.example.servitrack_movil

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.servitrack_movil.Network.ApiClient
import com.example.servitrack_movil.Network.LoginRequest
import com.example.servitrack_movil.Network.LoginResponse

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val edtUsuario = findViewById<EditText>(R.id.edtIdUsuario)
        val edtPass = findViewById<EditText>(R.id.edtPass)
        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token = prefs.getString("access_token", null)

        if (!token.isNullOrEmpty()) {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        }


        btnIngresar.setOnClickListener {
            val usuario = edtUsuario.text.toString().trim()
            val password = edtPass.text.toString().trim()

            if (usuario.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // *** INICIO DE SESIÓN LOCAL ***
            if (usuario == "user" && password == "1234") {
                Toast.makeText(this, "Inicio de sesión local exitoso", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@MainActivity, MenuActivity::class.java)
                startActivity(intent)
                finish()
                return@setOnClickListener
            }

            val loginRequest = LoginRequest(correo = usuario, password = password)

            val call = ApiClient.retrofit.login(loginRequest)
            call.enqueue(object : retrofit2.Callback<LoginResponse> {
                override fun onResponse(
                    call: retrofit2.Call<LoginResponse>,
                    response: retrofit2.Response<LoginResponse>
                ) {
                    if (response.isSuccessful) { // Check only for HTTP success first
                        val loginData = response.body() // loginData is LoginResponse?

                        if (loginData != null) { // Explicitly check if the body is not null
                            val accessToken = loginData.access   // Now, loginData is smart-cast to non-nullable
                            val refreshToken = loginData.refresh // Same here

                            // Guarda los tokens, por ejemplo, en SharedPreferences
                            // val sharedPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
                            // sharedPrefs.edit().putString("access_token", accessToken).apply()
                            // sharedPrefs.edit().putString("refresh_token", refreshToken).apply()

                            val sharedPrefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                            with(sharedPrefs.edit()) {
                                putInt("id", loginData.user.id)
                                putString("nombre", loginData.user.nombre)
                                putString("correo", loginData.user.correo)
                                putString("rol", loginData.user.rol)
                                putString("fecha", loginData.user.fecha_registro)
                                putString("imagen", loginData.user.foto)
                                putString("access_token", accessToken)
                                putString("refresh_token", refreshToken)
                                apply()
                            }

                            Toast.makeText(this@MainActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@MainActivity, MenuActivity::class.java)
                            startActivity(intent)
                            finish()

                        } else {
                            // This case means HTTP 200 OK, but the response body was null or couldn't be parsed
                            // (e.g., empty response, or parsing failed despite valid JSON)
                            android.util.Log.e("Login", "Respuesta exitosa, pero el cuerpo es nulo.")
                            Toast.makeText(this@MainActivity, "Error en la respuesta del servidor (body nulo)", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // This block executes if the server returns a non-200 HTTP code (e.g., 401, 403, 500)
                        val errorBody = response.errorBody()?.string()
                        val errorCode = response.code()
                        android.util.Log.e("Login", "Error code: $errorCode, Error body: $errorBody")
                        Toast.makeText(this@MainActivity, "Credenciales incorrectas o error en el servidor", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
                    android.util.Log.e("Login", "Fallo de red: ${t.message}", t)
                }
            })
        }
    }
}