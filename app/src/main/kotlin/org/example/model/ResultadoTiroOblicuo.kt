package org.example.model

/**
 * Clase que almacena los resultados calculados de un tiro oblicuo.
 * Contiene todos los valores que se obtienen después de aplicar las fórmulas físicas.
 */
class ResultadoTiroOblicuo(
    alturaMaxima: Double,
    distanciaHorizontal: Double,
    tiempoVuelo: Double,
    velocidadX: Double = 0.0,
    velocidadY: Double = 0.0
) {
    var alturaMaxima: Double = alturaMaxima
        private set

    var distanciaHorizontal: Double = distanciaHorizontal
        private set

    var tiempoVuelo: Double = tiempoVuelo
        private set

    var velocidadX: Double = velocidadX
        private set

    var velocidadY: Double = velocidadY
        private set

    fun comoLista(): List<Double> {
        return listOf(alturaMaxima, distanciaHorizontal, tiempoVuelo, velocidadX, velocidadY)
    }

    override fun equals(other: Any?): Boolean {
        return other is ResultadoTiroOblicuo && comoLista() == other.comoLista()
    }

    override fun hashCode(): Int {
        return comoLista().hashCode()
    }
}
