package com.example.servitrack_movil

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    private val SESSION_TIMEOUT = 60 * 60 * 1000L // 1 hora
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        checkSessionTimeout()
    }

    override fun onStop() {
        super.onStop()
        prefs.edit().putLong("last_exit_time", System.currentTimeMillis()).apply()
    }

    override fun onStart() {
        super.onStart()
        checkSessionTimeout()
    }

    private fun checkSessionTimeout() {
        val lastExitTime = prefs.getLong("last_exit_time", -1)
        if (lastExitTime != -1L) {
            val diff = System.currentTimeMillis() - lastExitTime
            if (diff > SESSION_TIMEOUT) {
                // Sesión expirada
                prefs.edit().clear().apply()
                Toast.makeText(this, "Sesión expirada, vuelve a iniciar sesión", Toast.LENGTH_SHORT).show()
                goToLogin()
            }
        }
    }

    private fun goToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
