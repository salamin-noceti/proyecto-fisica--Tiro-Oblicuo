package org.example.ui.simulacion.graficos

/**
 * Coordenada numérica de un punto en el plano cartesiano físico.
 */
class PuntoGrafico(val x: Double, val y: Double) {
    companion object {
        fun en(x: Double, y: Double): PuntoGrafico {
            return PuntoGrafico(x, y)
        }
    }

    private val coords: DoubleArray = doubleArrayOf(x, y)

    fun comoPar(): Pair<Double, Double> {
        return coords[0] to coords[1]
    }
}
