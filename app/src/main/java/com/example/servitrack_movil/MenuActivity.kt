package com.example.servitrack_movil

import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.servitrack_movil.databinding.ActivityMenuBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.servitrack_movil.ConsejoAdapter


class MenuActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Primero inflamos el layout
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ahora sí, ya podemos acceder a las vistas del layout (como el RecyclerView)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerConsejos)
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val listaConsejos = listOf(
            Consejos("Recuerda llevar tu herramienta", R.drawable.herramientas),
            Consejos("Usa tus protecciones", R.drawable.proteccion),
            Consejos("Verifica el ticket", R.drawable.ticket),
            Consejos("Contacta al cliente", R.drawable.llamada),
            Consejos("Registra tu entrada y salida", R.drawable.reloj),
            Consejos("No olvides tu gafete", R.drawable.gafete)
        )

        recyclerView.adapter = ConsejoAdapter(listaConsejos)

        // Toolbar y navegación
        setSupportActionBar(binding.appBarMenu.toolbar)

        binding.appBarMenu.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_menu)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}