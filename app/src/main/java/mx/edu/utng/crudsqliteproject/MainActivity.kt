package mx.edu.utng.crudsqliteproject

import VentaAdapter
import android.app.Activity
import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
//import android.widget.Toolbar
import android.view.animation.Animation
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.animation.AnimationUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var ventaAdapter: VentaAdapter
    private lateinit var tvEmptyView: TextView
    private val listaVentas = mutableListOf<Venta>()//esta es para mostrar los datos filtrados
    private val listaVentasCompleta = mutableListOf<Venta>() //esta es para mostrar todos los datos

    companion object {
        private const val REQUEST_CODE_ADD_EDIT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        recyclerView = findViewById(R.id.recyclerViewVentas)
        recyclerView.layoutManager = LinearLayoutManager(this)
        tvEmptyView = findViewById(R.id.tvEmptyView)
        val fabAgregarVenta: FloatingActionButton = findViewById(R.id.fabAgregarVenta)

        fabAgregarVenta.setOnClickListener{
            val intent = Intent(this, AddEditActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_EDIT)
        }

        ventaAdapter = VentaAdapter(listaVentas,
            onEditClick = { venta -> editarVenta(venta) },
            onDeleteClick = { venta -> eliminarVenta(venta) }
        )
        recyclerView.adapter = ventaAdapter

        cargarVentas()
    }

    private fun cargarVentas() {
        // Aquí se debe cargar la lista de ventas desde SQLite

        val dbHelper = VentasDBHelper(this)
        val ventasDesdeDB = dbHelper.obtenerTodasLasVentas()
        Log.d("DB", "Ventas obtenidas: ${ventasDesdeDB.size}") //esto verifica si la lista tiene elementos

        listaVentasCompleta.clear()
        listaVentasCompleta.addAll(ventasDesdeDB)

        listaVentas.clear()
        listaVentas.addAll(ventasDesdeDB)

        ventaAdapter.actualizarLista(listaVentas.toList()) //copia inmutable de la lista que ya se crea un "VentaAdapter"
    }

    private fun editarVenta(venta: Venta) {
        val intent = Intent(this, AddEditActivity::class.java)
        intent.putExtra("venta_id", venta.id)
        startActivityForResult(intent, REQUEST_CODE_ADD_EDIT)
    }

    private fun eliminarVenta(venta: Venta) {
        val posicion = listaVentas.indexOf(venta)

        if (posicion != -1) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(posicion)

            if (viewHolder != null) {
                val animacion = AnimationUtils.loadAnimation(this, R.anim.fade_out)
                viewHolder.itemView.startAnimation(animacion)

                Handler(Looper.getMainLooper()).postDelayed({
                    val dbHelper = VentasDBHelper(this)
                    val resultado = dbHelper.eliminarVenta(venta.id)

                    if (resultado > 0) {
                        listaVentas.removeAt(posicion)
                        listaVentasCompleta.remove(venta) // <-- Asegurarse de quitarla de la lista completa

                        ventaAdapter.notifyItemRemoved(posicion)

                        Toast.makeText(this, "Venta eliminada", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error al eliminar venta", Toast.LENGTH_SHORT).show()
                    }
                }, 500) // Esperar la animación
            } else {
                // Si el ViewHolder es null, eliminamos sin animación
                val dbHelper = VentasDBHelper(this)
                val resultado = dbHelper.eliminarVenta(venta.id)

                if (resultado > 0) {
                    listaVentas.remove(venta)
                    listaVentasCompleta.remove(venta) // <-- También actualizar la lista completa

                    ventaAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "Venta eliminada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al eliminar venta", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }





    //la lista se actualiza automaticamente al volver a la pantalla principal (luego de agregar o editar una venta)
    override fun onResume() {
        super.onResume()
        cargarVentas()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_ADD_EDIT && resultCode == Activity.RESULT_OK){
            cargarVentas() //recarga la lista de ventas automaticamente
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? androidx.appcompat.widget.SearchView
        searchView?.queryHint = "Buscar por nombre"

        searchView?.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarVentas(newText ?: "")
                return true
            }
            override fun onQueryTextSubmit(query: String?): Boolean {
                filtrarVentas(query ?: "")
                return true
            }
        })
        return true
    }

    private fun filtrarVentas(texto: String) {
        listaVentas.clear() // Limpiar antes de actualizar

        if (texto.isEmpty()) {
            listaVentas.addAll(listaVentasCompleta) // Restaurar todos los datos
        } else {
            listaVentas.addAll(
                listaVentasCompleta.filter { it.tipoPropiedad.contains(texto, ignoreCase = true) }
            )
        }

        ventaAdapter.notifyDataSetChanged() // Notificar cambios
    }

}