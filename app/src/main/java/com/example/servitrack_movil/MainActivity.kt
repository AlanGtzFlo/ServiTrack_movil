package com.example.servitrack_movil

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Referencias a los campos
        val edtUsuario = findViewById<EditText>(R.id.edtIdUsuario)
        val edtPass = findViewById<EditText>(R.id.edtPass)
        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val btnCancelar = findViewById<Button>(R.id.btnCancelar)

        btnIngresar.setOnClickListener {
            val usuario = edtUsuario.text.toString()
            val password = edtPass.text.toString()

            // Validación simple de prueba
            if (usuario == "2122200418" && password == "1234") {
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
                finish() // Cierra esta pantalla
            } else {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancelar.setOnClickListener {
            edtUsuario.text.clear()
            edtPass.text.clear()
        }
    }
}