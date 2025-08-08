package com.example.servitrack_movil

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.servitrack_movil.databinding.ItemReporteBinding

class ReporteAdapter(
    private val lista: List<Reporte>,
    private val onItemClick: (Reporte) -> Unit
) : RecyclerView.Adapter<ReporteAdapter.ReporteViewHolder>() {

    inner class ReporteViewHolder(val binding: ItemReporteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(reporte: Reporte) {
            binding.txtTituloReporte.text = reporte.titulo
            binding.txtEstado.text = reporte.estado
            binding.txtFecha.text = reporte.fecha

            binding.root.setOnClickListener {
                onItemClick(reporte)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReporteViewHolder {
        val binding = ItemReporteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ReporteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReporteViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    override fun getItemCount() = lista.size
}
