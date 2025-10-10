package com.example.servitrack_movil

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.servitrack_movil.databinding.ItemEmpresaBinding

class EmpresaAdapter(
    private val lista: List<Empresa>,
    private val onItemClick: (Empresa) -> Unit
) : RecyclerView.Adapter<EmpresaAdapter.EmpresaViewHolder>() {

    inner class EmpresaViewHolder(val binding: ItemEmpresaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(empresa: Empresa) {
            binding.txtNombreEmpresa.text = empresa.nombre
            // binding.txtEstadoEmpresa.text = "Estado: ${if (empresa.estatus) "Activa" else "Inactiva"}"
            binding.txtRFC.text = "ID: ${empresa.id}"

            binding.root.setOnClickListener {
                onItemClick(empresa)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpresaViewHolder {
        val binding = ItemEmpresaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return EmpresaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EmpresaViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    override fun getItemCount(): Int = lista.size
}
