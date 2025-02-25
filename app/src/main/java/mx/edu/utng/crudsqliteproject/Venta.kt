package mx.edu.utng.crudsqliteproject

data class Venta(
    val id: Int,
    val tipoPropiedad: String,
    val agenteVenta: String,
    val ubicacion: String,
    val fecha: String,
    val precio: Double,
    val estatus: String
)
