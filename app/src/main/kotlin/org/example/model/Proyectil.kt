package org.example.model

/**
 * Representa el objeto que se va a lanzar en el tiro oblicuo.
 */
class Proyectil(
    nombreInicial: String,
    masaInicial: Double = 1.0
) {
    val nombre: String = nombreInicial
    val masa: Double = masaInicial

    override fun toString(): String {
        return "$nombre (${masa} kg)"
    }
}
