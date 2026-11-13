package org.example.model

/**
 * Clase que representa los datos de entrada para un ejercicio de tiro oblicuo.
 * Contiene la velocidad inicial, el ángulo de lanzamiento y la altura desde
 * la que se dispara (0.0 m cuando el proyectil se lanza desde el suelo).
 */
class IngresoDatos(
    velocidadInicial: Double = 0.0,
    angulo: Double = 0.0,
    alturaInicial: Double = 0.0
) {
    companion object {
        var base: IngresoDatos = IngresoDatos(0.0, 0.0, 0.0)
            private set
    }

    var velocidadInicial: Double = velocidadInicial
        private set

    var angulo: Double = angulo
        private set

    var alturaInicial: Double = alturaInicial
        private set

    fun copy(velocidadInicial: Double = this.velocidadInicial, angulo: Double = this.angulo, alturaInicial: Double = this.alturaInicial): IngresoDatos {
        return IngresoDatos(velocidadInicial, angulo, alturaInicial)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IngresoDatos) return false
        return velocidadInicial == other.velocidadInicial && angulo == other.angulo && alturaInicial == other.alturaInicial
    }

    override fun hashCode(): Int {
        var result = velocidadInicial.hashCode()
        result = 31 * result + angulo.hashCode()
        result = 31 * result + alturaInicial.hashCode()
        return result
    }
}
