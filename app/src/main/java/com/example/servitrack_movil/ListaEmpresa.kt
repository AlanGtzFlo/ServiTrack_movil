package com.example.servitrack_movil

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.servitrack_movil.Empresa
import com.example.servitrack_movil.EmpresaAdapter
import com.example.servitrack_movil.databinding.FragmentListaEmpresaBinding

class ListaEmpresaFragment : Fragment() {

    private lateinit var binding: FragmentListaEmpresaBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentListaEmpresaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listaEmpresa = listOf(
            Empresa("1", "Independencia", "Activa", "IFFS010110I43"),
            Empresa("2", "Andares", "Inactiva", "IFFS010110I44"),
            Empresa("3", "La perla", "Activa", "IFFS010110I45")
        )

        val adapter = EmpresaAdapter(listaEmpresa) { empresaSeleccionada ->
            val action = ListaEmpresaFragmentDirections
                .actionListaEmpresasFragmentToDetalleEmpresasFragment(empresaSeleccionada)
            findNavController().navigate(action)
        }

        binding.recyclerTickets.adapter = adapter
        binding.recyclerTickets.layoutManager = LinearLayoutManager(requireContext())
    }
}
