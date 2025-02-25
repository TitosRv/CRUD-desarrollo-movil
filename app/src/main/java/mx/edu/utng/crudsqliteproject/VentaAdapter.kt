import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mx.edu.utng.crudsqliteproject.R
import mx.edu.utng.crudsqliteproject.Venta

class VentaAdapter(
    private val ventas: MutableList<Venta>,
    private val onEditClick: (Venta) -> Unit,
    private val onDeleteClick: (Venta) -> Unit
) : RecyclerView.Adapter<VentaAdapter.VentaViewHolder>() {

    inner class VentaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTipoPropiedad: TextView = itemView.findViewById(R.id.tvTipoPropiedad)
        val tvUbicacion: TextView = itemView.findViewById(R.id.tvUbicacion)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        val tvEstatus: TextView = itemView.findViewById(R.id.tvEstatus)
        val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditar)
        val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)

        fun bind(venta: Venta) {
            tvTipoPropiedad.text = venta.tipoPropiedad
            tvUbicacion.text = venta.ubicacion
            tvPrecio.text = "Precio: $${venta.precio}"
            tvEstatus.text = venta.estatus

            btnEditar.setOnClickListener { onEditClick(venta) }
            btnEliminar.setOnClickListener { onDeleteClick(venta) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VentaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_venta, parent, false)
        return VentaViewHolder(view)
    }

    override fun onBindViewHolder(holder: VentaViewHolder, position: Int) {
        holder.bind(ventas[position])

        val animacion = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.fade_in)
        holder.itemView.startAnimation(animacion)
    }

    override fun getItemCount(): Int = ventas.size

    fun actualizarLista(nuevaLista: List<Venta>) {
        ventas.clear()
        ventas.addAll(nuevaLista)
        Log.d("VentaAdapter", "Lista actualizada, tamaño: ${ventas.size}")
        notifyDataSetChanged()
    }
}
