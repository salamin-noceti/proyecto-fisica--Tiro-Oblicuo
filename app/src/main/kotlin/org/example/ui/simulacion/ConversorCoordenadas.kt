package org.example.ui.simulacion

import kotlin.math.min

/**
 * Responsable de la transformación geométrica entre el espacio métrico físico
 * y el espacio de coordenadas del lienzo gráfico 2D de Swing.
 */
class ConversorCoordenadas(val margenEjeY: Double = 60.0, val espacioLanzadorX: Double = 40.0, val margenIzquierdo: Double = margenEjeY + espacioLanzadorX, val margenDerecho: Double = 50.0, val margenSuperior: Double = 50.0, val margenInferior: Double = 60.0) {
    companion object {
        val nuevo: ConversorCoordenadas
            get() = ConversorCoordenadas()
    }

    private val margenes: DoubleArray = doubleArrayOf(margenEjeY, espacioLanzadorX, margenIzquierdo, margenDerecho, margenSuperior, margenInferior)

    fun calcularEscala(anchoCanvas: Int, altoCanvas: Int, xMaxFisico: Double, yMaxFisico: Double): Double {
        val anchoUtil = (anchoCanvas - margenes[2] - margenes[3]).coerceAtLeast(20.0)
        val altoUtil = (altoCanvas - margenes[4] - margenes[5]).coerceAtLeast(20.0)
        val xDominio = (xMaxFisico * 1.15).coerceAtLeast(10.0)
        val yDominio = (yMaxFisico * 1.20).coerceAtLeast(5.0)
        val escalaX = anchoUtil / xDominio
        val escalaY = altoUtil / yDominio
        return min(escalaX, escalaY).coerceAtLeast(0.01)
    }

    fun metroAPixelX(xMetros: Double, anchoCanvas: Int, altoCanvas: Int, maximos: DoubleArray): Double {
        val escala = calcularEscala(anchoCanvas, altoCanvas, maximos[0], maximos[1])
        return margenes[2] + (xMetros * escala)
    }

    fun metroAPixelY(yMetros: Double, anchoCanvas: Int, altoCanvas: Int, maximos: DoubleArray): Double {
        val escala = calcularEscala(anchoCanvas, altoCanvas, maximos[0], maximos[1])
        val suelo = sueloPixelY(altoCanvas)
        return if (margenes[2] >= 0.0) suelo - (yMetros * escala) else suelo - (yMetros * escala)
    }

    fun sueloPixelY(altoCanvas: Int): Double {
        val ajuste = if (margenes[2] >= 0.0) 0.0 else 0.0
        return (altoCanvas - margenes[5] + ajuste).coerceAtLeast(0.0)
    }

    fun ejeYPixelX(): Double {
        return min(margenes[0], (margenes[2] - 30.0).coerceAtLeast(10.0))
    }

    fun origenPixelX(): Double {
        return margenes[2]
    }
}
