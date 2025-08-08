package com.example.servitrack_movil

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.servitrack_movil.Network.ApiClient
import com.example.servitrack_movil.Network.LogoutRequest
import com.example.servitrack_movil.Network.LogoutResponse
import com.example.servitrack_movil.databinding.ActivityMenuBinding
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val nombre = prefs.getString("nombre", "Usuario")
        val id = prefs.getInt("id", 0)

        val headerView = binding.navView.getHeaderView(0)
        val txtNombre = headerView.findViewById<TextView>(R.id.txtNombre)
        val txtMatricula = headerView.findViewById<TextView>(R.id.txtMatricula)

        txtNombre.text = nombre
        txtMatricula.text = "ID: $id"

        setSupportActionBar(binding.appBarMenu.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_menu)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_usuario,
                R.id.nav_listaTickets,
                R.id.nav_listaUbicaciones,
                R.id.nav_listaReportes,
                R.id.nav_empresas,
                R.id.nav_cerrar_sesion
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.appBarMenu.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerToggle.drawerArrowDrawable.color =
            ContextCompat.getColor(this, R.color.hamburger_orange)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()


        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_cerrar_sesion -> {
                    cerrarSesion()
                    true
                }

                else -> {
                    navController.navigate(menuItem.itemId)
                    drawerLayout.closeDrawers()
                    true
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_menu)
        return androidx.navigation.ui.NavigationUI.navigateUp(navController, appBarConfiguration)
    }

    private fun cerrarSesion() {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val refreshToken = prefs.getString("refresh_token", null)
        val accessToken = prefs.getString("access_token", null)

        if (refreshToken.isNullOrEmpty() || accessToken.isNullOrEmpty()) {
            Toast.makeText(this, "Tokens no disponibles", Toast.LENGTH_SHORT).show()
            return
        }

        val logoutRequest = LogoutRequest(refresh = refreshToken)
        val authHeader = "Bearer $accessToken"

        ApiClient.retrofit.logout(authHeader, logoutRequest).enqueue(object : Callback<LogoutResponse> {
            override fun onResponse(call: Call<LogoutResponse>, response: Response<LogoutResponse>) {
                if (response.isSuccessful) {
                    prefs.edit().clear().apply()
                    Toast.makeText(this@MenuActivity, "Sesión cerrada", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@MenuActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    val errorCode = response.code()
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("Logout", "Error logout: $errorCode, body: $errorBody")
                    Toast.makeText(this@MenuActivity, "Error al cerrar sesión: $errorCode", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                android.util.Log.e("Logout", "Fallo red: ${t.message}", t)
                Toast.makeText(this@MenuActivity, "Fallo de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}
