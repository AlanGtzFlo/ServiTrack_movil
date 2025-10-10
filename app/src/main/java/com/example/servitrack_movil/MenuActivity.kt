package com.example.servitrack_movil

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.servitrack_movil.Network.ApiClient
import com.example.servitrack_movil.Network.LogoutRequest
import com.example.servitrack_movil.Network.LogoutResponse
import com.example.servitrack_movil.Network.User
import com.example.servitrack_movil.databinding.ActivityMenuBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuActivity : BaseActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val nombre = prefs.getString("nombre", "Usuario")
        val id = prefs.getInt("id", 0)
        val token = prefs.getString("access_token", null)

        val headerView = binding.navView.getHeaderView(0)
        val txtNombre = headerView.findViewById<TextView>(R.id.txtNombre)
        val txtMatricula = headerView.findViewById<TextView>(R.id.txtMatricula)
        val imgUsuarioHeader = headerView.findViewById<ImageView>(R.id.imageView)

        txtNombre.text = nombre
        txtMatricula.text = "ID: $id"

        if (!token.isNullOrEmpty() && id != 0) {
            ApiClient.retrofit.obtenerUsuarioPorId(id, "Bearer $token")
                .enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            val user = response.body()
                            if (!user?.foto.isNullOrEmpty()) {
                                Glide.with(this@MenuActivity)
                                    .load(user.foto)
                                    .placeholder(R.drawable.ic_user)
                                    .error(R.drawable.ic_user)
                                    .circleCrop()
                                    .skipMemoryCache(true)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .into(imgUsuarioHeader)
                            } else {
                                imgUsuarioHeader.setImageResource(R.drawable.ic_user)
                            }
                        } else {
                            imgUsuarioHeader.setImageResource(R.drawable.ic_user)
                        }
                    }
                    override fun onFailure(call: Call<User>, t: Throwable) {
                        imgUsuarioHeader.setImageResource(R.drawable.ic_user)
                    }
                })
        }


        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val bottomNavView: BottomNavigationView = binding.bottomNavView
        // Forma alternativa y más robusta de obtener el NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_menu) as androidx.navigation.fragment.NavHostFragment
        val navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                // Asegúrate que estos IDs coincidan con los de tus menús
                R.id.nav_home, R.id.nav_listaTickets, R.id.nav_listaReportes,
                R.id.nav_empresas, R.id.nav_usuario
            ),
            drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        bottomNavView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.nav_cerrar_sesion) {
                cerrarSesion()
                true
            } else {
                NavigationUI.onNavDestinationSelected(menuItem, navController)
                drawerLayout.closeDrawers()
                true
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_menu)
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
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