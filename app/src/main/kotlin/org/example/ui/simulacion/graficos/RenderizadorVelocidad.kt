package org.example.ui.simulacion.graficos

import org.example.model.IngresoDatos
import org.example.model.PosicionTrayectoria
import org.example.model.ResultadoTiroOblicuo
import java.awt.Color

/**
 * Renderizador del **Gráfico 2: Velocidad vs Tiempo** ($v_y(t)$ y $v_x(t)$).
 */
class RenderizadorVelocidad(
    canvasInicial: CartesianChartCanvas = CartesianChartCanvas(titulo = "Velocidad vs Tiempo", etiquetaEjeX = "t (s)", etiquetaEjeY = "Velocidad (m/s)")
) : RenderizadorGraficoCinematico {

    override val canvas: CartesianChartCanvas = canvasInicial

    private val colorVx = Color(37, 99, 235)
    private val colorVy = Color(245, 158, 11)
    private val gravedad = 9.81

    override fun configurarLimites(datos: IngresoDatos, resultadoTeorico: ResultadoTiroOblicuo) {
        val tVuelo = resultadoTeorico.tiempoVuelo.coerceAtLeast(1.0)
        val vyFinal = resultadoTeorico.velocidadY - (gravedad * resultadoTeorico.tiempoVuelo)
        val vMax = maxOf(resultadoTeorico.velocidadX, resultadoTeorico.velocidadY, 5.0)
        val vMin = minOf(0.0, vyFinal)
        canvas.configurarLimites(xMin = 0.0, xMax = tVuelo * 1.05, yMin = (vMin * 1.15).coerceAtMost(-2.0), yMax = vMax * 1.15)
    }

    override fun actualizar(tiempoActual: Double, puntosEstela: List<PosicionTrayectoria>, resultadoTeorico: ResultadoTiroOblicuo, datos: IngresoDatos) {
        val ultimo = puntosEstela.lastOrNull()
        val serieVx = canvas.construirSerieTrayectoria(listOf("vx_t", "vx(t)", "m/s"), colorVx, puntosEstela, { it.velocidadX }, ultimo?.velocidadX ?: resultadoTeorico.velocidadX)
        val serieVy = canvas.construirSerieTrayectoria(listOf("vy_t", "vy(t)", "m/s"), colorVy, puntosEstela, { it.velocidadY }, ultimo?.velocidadY ?: resultadoTeorico.velocidadY)
        canvas.actualizarSeries(listOf(serieVx, serieVy), tiempoActual)
    }

    override fun reiniciar() {
        canvas.limpiar()
    }
}
