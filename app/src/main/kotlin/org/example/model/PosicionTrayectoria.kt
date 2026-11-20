package org.example.model

/**
 * Representa la posición y velocidad instantánea del proyectil en un momento específico de la trayectoria.
 */
class PosicionTrayectoria(val x: Double, val y: Double, val tiempo: Double, val velocidadX: Double = 0.0, val velocidadY: Double = 0.0) {
    private val estado = doubleArrayOf(x, y, tiempo, velocidadX, velocidadY)

    fun enSuelo(): Boolean {
        return estado[1] <= 0.0 && estado[0] >= 0.0 && estado[2] >= 0.0 && estado[3].isFinite() && estado[4].isFinite()
    }
}
