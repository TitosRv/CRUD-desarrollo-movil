package mx.edu.utng.crudsqliteproject

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class VentasDBHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object{
        private const val DATABASE_NAME = "bienesraices.db"
        private const val DATABASE_VERSION = 3
        const val TABLE_NAME = "Ventas"

        const val COLUMN_ID = "id"
        const val COLUMN_TIPO = "tipoPropiedad"
        const val COLUMN_AGENTE = "agenteVenta"
        const val COLUMN_UBICACION = "ubicacion"
        const val COLUMN_FECHA = "fecha"
        const val COLUMN_PRECIO = "precio"
        const val COLUMN_ESTATUS = "estatus"
    }

    override fun onCreate(db: SQLiteDatabase){
        val createTableQuery = """
        CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TIPO TEXT,
                $COLUMN_AGENTE TEXT,
                $COLUMN_UBICACION TEXT,
                $COLUMN_FECHA TEXT,
                $COLUMN_PRECIO REAL,
                $COLUMN_ESTATUS TEXT
                )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertarVenta(tipo: String, agente: String, ubicacion: String, fecha: String, precio: Double, estatus: String): Long{
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TIPO, tipo)
            put(COLUMN_AGENTE, agente)
            put(COLUMN_UBICACION, ubicacion)
            put(COLUMN_FECHA, fecha) //se guarda como YYYY-MM-DD
            put(COLUMN_PRECIO, precio)
            put(COLUMN_ESTATUS, estatus)
        }
        val id =  db.insert(TABLE_NAME, null, values)
        Log.d("DB", "Venta insertada con ID: $id")
        return id
    }

    fun actualizarVenta(id: Int, tipo: String, agente: String, ubicacion: String, fecha: String, precio: Double, estatus: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TIPO, tipo)
            put(COLUMN_AGENTE, agente)
            put(COLUMN_UBICACION, ubicacion)
            put(COLUMN_FECHA, fecha)
            put(COLUMN_PRECIO, precio)
            put(COLUMN_ESTATUS, estatus)
        }
        return db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun obtenerTodasLasVentas(): List<Venta>{
        val listaVentas = mutableListOf<Venta>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val tipoPropiedad = cursor.getString(cursor.getColumnIndexOrThrow("tipoPropiedad"))
            val agenteVenta = cursor.getString(cursor.getColumnIndexOrThrow("agenteVenta"))
            val ubicacion = cursor.getString(cursor.getColumnIndexOrThrow("ubicacion"))
            val fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"))
            val precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
            val estatus = cursor.getString(cursor.getColumnIndexOrThrow("estatus"))


            val venta = Venta(id, tipoPropiedad, agenteVenta, ubicacion, fecha, precio, estatus)
            listaVentas.add(venta)
        }
        cursor.close()
        return listaVentas
    }

    fun eliminarVenta(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun obtenerVentaPorId(id: Int): Venta? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null, //todas las columnas
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        val venta = if (cursor.moveToFirst()) {
            Venta(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AGENTE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UBICACION)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRECIO)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESTATUS))
            )
        } else {
            null
        }
        cursor.close()
        return venta
    }
}

