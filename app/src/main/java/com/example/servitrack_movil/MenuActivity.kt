package com.example.servitrack_movil

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.servitrack_movil.databinding.ActivityMenuBinding
import com.google.android.material.navigation.NavigationView

class MenuActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ PRIMERO: Inicializar el binding
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val nombre = prefs.getString("nombre", "Usuario")
        val id = prefs.getInt("id", 0)

        // ✅ Acceder al header del NavigationView
        val headerView = binding.navView.getHeaderView(0)
        val txtNombre = headerView.findViewById<TextView>(R.id.txtNombre)
        val txtMatricula = headerView.findViewById<TextView>(R.id.txtMatricula)

        txtNombre.text = nombre
        txtMatricula.text = "ID: $id"

        // ✅ Toolbar y navegación
        setSupportActionBar(binding.appBarMenu.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_menu)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_usuario,
                R.id.nav_listaTickets,
                R.id.nav_ubicaciones,
                R.id.nav_listaReportes,
                R.id.nav_empresas,
                R.id.nav_cerrar_sesion
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_menu)
        return androidx.navigation.ui.NavigationUI.navigateUp(navController, appBarConfiguration)
    }
}
