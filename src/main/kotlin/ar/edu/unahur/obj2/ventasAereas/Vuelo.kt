package ar.edu.unahur.obj2.ventasAereas

import java.time.LocalDate

class Avion(val cantidadMaximaAsientos: Int, val peso: Int)

class Ciudad(val nombre: String)

abstract class Vuelo(fecha: LocalDate, avion: Avion, origen: Ciudad, destino: Ciudad, precioEstandar: Double) {
  fun asientosLibres() = asientosDisponibles() - asientosOcupados()

  abstract fun asientosDisponibles(): Int;
  abstract fun asientosOcupados(): Int;
}
