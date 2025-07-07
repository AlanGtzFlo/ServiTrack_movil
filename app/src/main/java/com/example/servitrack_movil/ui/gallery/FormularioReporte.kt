package com.example.servitrack_movil.ui.gallery

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.servitrack_movil.R

class FormularioReporte : Fragment() {

    companion object {
        fun newInstance() = FormularioReporte()
    }

    private val viewModel: FormularioReporteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_formulario_reporte, container, false)
    }
}