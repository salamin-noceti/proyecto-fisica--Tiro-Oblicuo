package org.example.ui.simulacion.graficos

import org.example.model.IngresoDatos
import org.example.model.PosicionTrayectoria
import org.example.model.ResultadoTiroOblicuo
import java.awt.Color

/**
 * Renderizador del **Gráfico 1: Posición vs Tiempo** ($y(t)$ y $x(t)$).
 */
class RenderizadorPosicion(
    canvasInicial: CartesianChartCanvas = CartesianChartCanvas(titulo = "Posición vs Tiempo", etiquetaEjeX = "t (s)", etiquetaEjeY = "Posición (m)")
) : RenderizadorGraficoCinematico {

    companion object {
        var nuevo: RenderizadorPosicion = RenderizadorPosicion()
            private set
            get() {
                return RenderizadorPosicion()
            }
    }

    override var canvas: CartesianChartCanvas = canvasInicial
        private set

    private val colorX = Color(37, 99, 235)
    private val colorY = Color(16, 185, 129)

    override fun configurarLimites(datos: IngresoDatos, resultadoTeorico: ResultadoTiroOblicuo) {
        val tVuelo = resultadoTeorico.tiempoVuelo.coerceAtLeast(1.0)
        val valorMaximoY = maxOf(resultadoTeorico.distanciaHorizontal, resultadoTeorico.alturaMaxima, datos.alturaInicial, 5.0)
        canvas.configurarLimites(xMin = 0.0, xMax = tVuelo * 1.05, yMin = 0.0, yMax = valorMaximoY * 1.15)
    }

    override fun actualizar(tiempoActual: Double, puntosEstela: List<PosicionTrayectoria>, resultadoTeorico: ResultadoTiroOblicuo, datos: IngresoDatos) {
        val puntosX = puntosEstela.map { PuntoGrafico.en(it.tiempo, it.x) }
        val puntosY = puntosEstela.map { PuntoGrafico.en(it.tiempo, it.y) }
        val ultimo = puntosEstela.lastOrNull()
        val serieX = canvas.construirSerie(listOf("x_t", "x(t)", "m"), colorX, puntosX, ultimo?.x ?: 0.0)
        val serieY = canvas.construirSerie(listOf("y_t", "y(t)", "m"), colorY, puntosY, ultimo?.y ?: datos.alturaInicial)
        canvas.actualizarSeries(listOf(serieX, serieY), tiempoActual)
    }

    override fun reiniciar() {
        canvas.limpiar()
    }
}
