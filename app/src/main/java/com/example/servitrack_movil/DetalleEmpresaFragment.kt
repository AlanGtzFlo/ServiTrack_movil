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

        binding.txtEmpresaId.text = "ID de la empresa:\n${empresa.id}"
        binding.txtTituloDetalle.text = "Nombre:\n${empresa.nombre}"
        binding.txtEstadoDetalle.text = "Estatus:\n${empresa.estatus}"
        binding.txtTipoPoliza.text = "Tipo de póliza:\n${empresa.tipo_poliza}"
        binding.txtFechaInicioPoliza.text = "Inicio de póliza:\n${empresa.fecha_inicio_poliza}"
        binding.txtFechaFinPoliza.text = "Fin de póliza:\n${empresa.fecha_fin_poliza}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}