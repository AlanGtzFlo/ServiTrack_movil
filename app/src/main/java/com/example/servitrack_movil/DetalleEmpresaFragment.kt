package com.example.servitrack_movil

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.servitrack_movil.databinding.FragmentDetalleReporteBinding
import kotlin.getValue
import com.example.servitrack_movil.databinding.FragmentDetalleEmpresaBinding
import com.example.servitrack_movil.Empresa

class DetalleEmpresaFragment : Fragment() {
    private var _binding: FragmentDetalleEmpresaBinding? = null
    private val binding get() = _binding!!

    private val args: DetalleEmpresaFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalleEmpresaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val empresa = args.empresa

        // Mostrar datos en UI
        binding.txtTituloDetalle.text = empresa.titulo
        binding.txtEstadoDetalle.text = empresa.estado
        binding.txtRFCDetalle.text = empresa.RFC
        // etc... otros campos
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}