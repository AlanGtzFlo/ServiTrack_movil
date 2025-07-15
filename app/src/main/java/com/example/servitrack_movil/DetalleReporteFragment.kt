package com.example.servitrack_movil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.servitrack_movil.databinding.FragmentDetalleReporteBinding

class DetalleReporteFragment : Fragment() {

    private var _binding: FragmentDetalleReporteBinding? = null
    private val binding get() = _binding!!

    private val args: DetalleReporteFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalleReporteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reporte = args.reporte

        // Mostrar datos en UI
        binding.txtTituloDetalle.text = reporte.titulo
        binding.txtEstadoDetalle.text = reporte.estado
        binding.txtFechaDetalle.text = reporte.fecha
        // etc... otros campos
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
