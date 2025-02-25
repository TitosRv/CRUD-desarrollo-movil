package mx.edu.utng.crudsqliteproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.app.DatePickerDialog
import android.widget.DatePicker
import java.util.*

class AddEditActivity : AppCompatActivity(){

    private lateinit var dbHelper: VentasDBHelper
    private var ventaId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)

        dbHelper = VentasDBHelper(this)

        val etTipoPropiedad = findViewById<EditText>(R.id.etTipoPropiedad)
        val etAgenteVenta = findViewById<EditText>(R.id.etAgenteVenta)
        val etUbicacion = findViewById<EditText>(R.id.etUbicacion)
        val etFecha = findViewById<EditText>(R.id.etFecha)
        val etPrecio = findViewById<EditText>(R.id.etPrecio)
        val etEstatus = findViewById<EditText>(R.id.etEstatus)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)

        etFecha.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { view: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                    // Formatear la fecha seleccionada y mostrarla en el EditText
                    val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    etFecha.setText(formattedDate)
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        //se verifica si se recibio un ID para una edicion
        ventaId = intent.getIntExtra("venta_id", -1)
        if(ventaId != null && ventaId != -1){
            //se cargan aqui los datos de la venta desde SQLite
            val venta = dbHelper.obtenerVentaPorId(ventaId!!)
            if (venta != null) {
                etTipoPropiedad.setText(venta.tipoPropiedad)
                etAgenteVenta.setText(venta.agenteVenta)
                etUbicacion.setText(venta.ubicacion)
                etFecha.setText(venta.fecha)
                etPrecio.setText(venta.precio.toString())
                etEstatus.setText(venta.estatus)
            }
        }

        btnGuardar.setOnClickListener{
            val tipo = etTipoPropiedad.text.toString()
            val agente = etAgenteVenta.text.toString()
            val ubicacion = etUbicacion.text.toString()
            val fecha = etFecha.text.toString()
            val precio = etPrecio.text.toString().toDoubleOrNull() ?: 0.0
            val estatus = etEstatus.text.toString()


            if (tipo.isBlank() || agente.isBlank() || ubicacion.isBlank() || fecha.isBlank()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(ventaId == -1){
                //insertar nueva venta
                dbHelper.insertarVenta(tipo, agente, ubicacion, fecha, precio, estatus)
                Toast.makeText(this, "Venta guardada", Toast.LENGTH_SHORT).show()
            } else {
                //actualiza una venta existene
                dbHelper.actualizarVenta(ventaId!!,tipo, agente, ubicacion, fecha, precio, estatus)
                Toast.makeText(this, "Venta actualizada", Toast.LENGTH_SHORT).show()
            }

            val intent = Intent()
            intent.putExtra("actualizar", true)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}