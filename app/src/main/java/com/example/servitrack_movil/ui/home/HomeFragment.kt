package com.example.servitrack_movil.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.servitrack_movil.ConsejoAdapter
import com.example.servitrack_movil.Consejos
import com.example.servitrack_movil.R
import com.example.servitrack_movil.databinding.FragmentHomeBinding



class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listaConsejos = listOf(
            Consejos("Recuerda llevar tu herramienta", R.drawable.herramientas),
            Consejos("Usa tus protecciones", R.drawable.proteccion),
            Consejos("Verifica el ticket", R.drawable.ticket),
            Consejos("Contacta al cliente", R.drawable.llamada),
            Consejos("Registra tu entrada y salida", R.drawable.reloj),
            Consejos("No olvides tu gafete", R.drawable.gafete)
        )

        binding.recyclerConsejos.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerConsejos.adapter = ConsejoAdapter(listaConsejos)

        // Cargar SharedPreferences
        val prefs = requireContext().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
        val nombre = prefs.getString("nombre", "Usuario")
        val id = prefs.getInt("id", 0)

        binding.txtNombre.text = nombre
        binding.txtMatricula.text = "Matrícula: $id"
    }
}
