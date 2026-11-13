package org.example.model

/**
 * Representa el objeto que se va a lanzar en el tiro oblicuo.
 */
class Proyectil(
    nombreInicial: String,
    masaInicial: Double = 1.0
) {
    var nombre: String = nombreInicial
        private set

    var masa: Double = masaInicial
        private set

    override fun toString(): String {
        return "$nombre (${masa} kg)"
    }
}
