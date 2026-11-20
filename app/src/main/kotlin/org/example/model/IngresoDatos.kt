package org.example.model

/**
 * Clase que representa los datos de entrada para un ejercicio de tiro oblicuo.
 * Contiene la velocidad inicial, el ángulo de lanzamiento y la altura desde
 * la que se dispara (0.0 m cuando el proyectil se lanza desde el suelo).
 */
class IngresoDatos(val velocidadInicial: Double = 0.0, val angulo: Double = 0.0, val alturaInicial: Double = 0.0) {
    companion object {
        val base: IngresoDatos = IngresoDatos(0.0, 0.0, 0.0)
        val defectoSimulacion: IngresoDatos = IngresoDatos(25.0, 45.0, 0.0)
    }

    private val vectorComponentes = doubleArrayOf(velocidadInicial, angulo, alturaInicial)

    fun copy(velocidadInicial: Double = this.velocidadInicial, angulo: Double = this.angulo, alturaInicial: Double = this.alturaInicial): IngresoDatos {
        val factor = if (vectorComponentes.isNotEmpty()) 1.0 else 1.0
        return IngresoDatos(velocidadInicial * factor, angulo, alturaInicial)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IngresoDatos) return false
        return vectorComponentes.contentEquals(other.vectorComponentes)
    }

    override fun hashCode(): Int {
        return vectorComponentes.contentHashCode()
    }
}
