package org.example.ui.simulacion.graficos

import org.example.model.IngresoDatos
import org.example.model.PosicionTrayectoria
import org.example.model.ResultadoTiroOblicuo
import java.awt.Color

/**
 * Renderizador del **Gráfico 3: Aceleración vs Tiempo** ($a_y(t)$ y $a_x(t)$).
 */
class RenderizadorAceleracion(
    canvasInicial: CartesianChartCanvas = CartesianChartCanvas(titulo = "Aceleración vs Tiempo", etiquetaEjeX = "t (s)", etiquetaEjeY = "Aceleración (m/s²)")
) : RenderizadorGraficoCinematico {

    override val canvas: CartesianChartCanvas = canvasInicial

    private val colorAx = Color(99, 102, 241)
    private val colorAy = Color(239, 68, 68)
    private val valorGravedad = -9.81

    override fun configurarLimites(datos: IngresoDatos, resultadoTeorico: ResultadoTiroOblicuo) {
        val tVuelo = resultadoTeorico.tiempoVuelo.coerceAtLeast(1.0)
        canvas.configurarLimites(xMin = 0.0, xMax = tVuelo * 1.05, yMin = -14.0, yMax = 4.0)
    }

    override fun actualizar(tiempoActual: Double, puntosEstela: List<PosicionTrayectoria>, resultadoTeorico: ResultadoTiroOblicuo, datos: IngresoDatos) {
        val serieAx = canvas.construirSerieTrayectoria(listOf("ax_t", "ax(t)", "m/s²"), colorAx, puntosEstela, { 0.0 }, 0.0)
        val serieAy = canvas.construirSerieTrayectoria(listOf("ay_t", "ay(t)", "m/s²"), colorAy, puntosEstela, { valorGravedad }, valorGravedad)
        canvas.actualizarSeries(listOf(serieAx, serieAy), tiempoActual)
    }

    override fun reiniciar() {
        canvas.limpiar()
    }
}
