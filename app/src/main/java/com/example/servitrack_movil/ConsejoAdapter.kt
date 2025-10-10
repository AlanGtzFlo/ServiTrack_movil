package com.example.servitrack_movil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.servitrack_movil.databinding.ItemConsejoBinding
import com.example.servitrack_movil.Consejos


class ConsejoAdapter(private val lista: List<Consejos>) :
    RecyclerView.Adapter<ConsejoAdapter.ConsejoViewHolder>() {

    class ConsejoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imagen: ImageView = itemView.findViewById(R.id.imgIconoTicket)
        val mensaje: TextView = itemView.findViewById(R.id.txtMensaje)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsejoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_consejo, parent, false)
        return ConsejoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConsejoViewHolder, position: Int) {
        val consejo = lista[position]
        holder.mensaje.text = consejo.mensaje
        holder.imagen.setImageResource(consejo.imagenResId)
    }

    override fun getItemCount(): Int = lista.size
}
