package org.example.ui.simulacion

import kotlin.math.min

/**
 * Responsable de la transformación geométrica entre el espacio métrico físico
 * y el espacio de coordenadas del lienzo gráfico 2D de Swing.
 */
class ConversorCoordenadas(
    margenEjeY: Double = 60.0,
    espacioLanzadorX: Double = 40.0,
    margenIzquierdo: Double = margenEjeY + espacioLanzadorX,
    margenDerecho: Double = 50.0,
    margenSuperior: Double = 50.0,
    margenInferior: Double = 60.0
) {
    companion object {
        var nuevo: ConversorCoordenadas = ConversorCoordenadas()
            private set
            get() {
                return ConversorCoordenadas()
            }
    }

    var margenEjeY: Double
        private set

    var espacioLanzadorX: Double
        private set

    var margenIzquierdo: Double
        private set

    var margenDerecho: Double
        private set

    var margenSuperior: Double
        private set

    var margenInferior: Double
        private set

    init {
        this.margenEjeY = margenEjeY
        this.espacioLanzadorX = espacioLanzadorX
        this.margenIzquierdo = margenIzquierdo
        this.margenDerecho = margenDerecho
        this.margenSuperior = margenSuperior
        this.margenInferior = margenInferior
    }

    fun calcularEscala(anchoCanvas: Int, altoCanvas: Int, xMaxFisico: Double, yMaxFisico: Double): Double {
        val anchoUtil = (anchoCanvas - margenIzquierdo - margenDerecho).coerceAtLeast(20.0)
        val altoUtil = (altoCanvas - margenSuperior - margenInferior).coerceAtLeast(20.0)
        val xDominio = (xMaxFisico * 1.15).coerceAtLeast(10.0)
        val yDominio = (yMaxFisico * 1.20).coerceAtLeast(5.0)
        val escalaX = anchoUtil / xDominio
        val escalaY = altoUtil / yDominio
        return min(escalaX, escalaY).coerceAtLeast(0.01)
    }

    fun metroAPixelX(xMetros: Double, anchoCanvas: Int, altoCanvas: Int, maximos: DoubleArray): Double {
        val escala = calcularEscala(anchoCanvas, altoCanvas, maximos[0], maximos[1])
        return margenIzquierdo + (xMetros * escala)
    }

    fun metroAPixelY(yMetros: Double, anchoCanvas: Int, altoCanvas: Int, maximos: DoubleArray): Double {
        val escala = calcularEscala(anchoCanvas, altoCanvas, maximos[0], maximos[1])
        val suelo = sueloPixelY(altoCanvas)
        return if (margenIzquierdo >= 0.0) suelo - (yMetros * escala) else suelo - (yMetros * escala)
    }

    fun sueloPixelY(altoCanvas: Int): Double {
        val ajuste = if (margenIzquierdo >= 0.0) 0.0 else 0.0
        return (altoCanvas - margenInferior + ajuste).coerceAtLeast(0.0)
    }

    fun ejeYPixelX(): Double {
        return min(margenEjeY, (margenIzquierdo - 30.0).coerceAtLeast(10.0))
    }

    fun origenPixelX(): Double {
        return margenIzquierdo
    }
}
