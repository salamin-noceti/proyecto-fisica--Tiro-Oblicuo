package org.example.ui.simulacion.graficos

/**
 * Coordenada numérica de un punto en el plano cartesiano físico.
 */
class PuntoGrafico(
    x: Double,
    y: Double
) {
    companion object {
        fun en(x: Double, y: Double): PuntoGrafico {
            return PuntoGrafico(x, y)
        }
    }

    var x: Double
        private set

    var y: Double
        private set

    init {
        this.x = x
        this.y = y
    }

    fun comoPar(): Pair<Double, Double> {
        return x to y
    }
}
