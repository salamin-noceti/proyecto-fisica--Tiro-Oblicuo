package org.example.model

/**
 * Representa la posición y velocidad instantánea del proyectil en un momento específico de la trayectoria.
 */
class PosicionTrayectoria(
    x: Double,
    y: Double,
    tiempo: Double,
    velocidadX: Double = 0.0,
    velocidadY: Double = 0.0
) {
    var x: Double = x
        private set

    var y: Double = y
        private set

    var tiempo: Double = tiempo
        private set

    var velocidadX: Double = velocidadX
        private set

    var velocidadY: Double = velocidadY
        private set

    fun enSuelo(): Boolean {
        return y <= 0.0 && x >= 0.0 && tiempo >= 0.0 && velocidadX.isFinite() && velocidadY.isFinite()
    }
}
