package com.example.servitrack_movil

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.servitrack_movil.databinding.FragmentListaReporteBinding
import com.example.servitrack_movil.Reporte
import com.example.servitrack_movil.ReporteAdapter

class ListaReporteFragment : Fragment() {

    private lateinit var binding: FragmentListaReporteBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentListaReporteBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listaReportes = listOf(
            Reporte("1", "Reporte A", "Abierto", "12 abr 2025"),
            Reporte("2", "Reporte B", "En proceso", "13 abr 2025"),
            Reporte("3", "Reporte C", "Resuelto", "14 abr 2025")
        )

        val adapter = ReporteAdapter(listaReportes) { reporteSeleccionado ->
            val action = ListaReporteFragmentDirections
                .actionReporteFragmentToDetalleReporteFragment(reporteSeleccionado)
            findNavController().navigate(action)
        }

        binding.recyclerTickets.adapter = adapter
        binding.recyclerTickets.layoutManager = LinearLayoutManager(requireContext())

        binding.btnCrear.setOnClickListener {
            val action = ListaReporteFragmentDirections.actionListaReporteFragmentToGenerarReporteFragment()
            findNavController().navigate(action)
        }

    }

}
