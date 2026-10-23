package org.example.ui.simulacion.graficos

import org.example.model.IngresoDatos
import org.example.model.PosicionTrayectoria
import org.example.model.ResultadoTiroOblicuo
import java.awt.Color

/**
 * Renderizador del **Gráfico 2: Velocidad vs Tiempo** ($v_y(t)$ y $v_x(t)$).
 *
 * Muestra simultáneamente:
 * - La velocidad horizontal $v_x(t)$ (línea horizontal constante durante todo el vuelo).
 * - La velocidad vertical $v_y(t)$ (recta con pendiente negativa $-g$, que comienza en $v_{0y}$,
 *   cruza $0$ m/s en la cúspide parabólica y adquiere valores negativos hasta el impacto).
 *
 * Cumple con los criterios de calidad:
 * - **DUP -> 0%:** Reutiliza los cálculos instantáneos de [PosicionTrayectoria.velocidadX] y
 *   [PosicionTrayectoria.velocidadY] provistos por el motor analítico `CalculadorTiroOblicuo`.
 * - **SRP:** Mapea las componentes vectoriales de velocidad hacia las series del lienzo cartesiano.
 *
 * @param canvas Lienzo cartesiano inyectado o instanciado por defecto.
 */
class RenderizadorVelocidad(
    override val canvas: CartesianChartCanvas = CartesianChartCanvas(
        titulo = "Velocidad vs Tiempo",
        etiquetaEjeX = "t (s)",
        etiquetaEjeY = "Velocidad (m/s)"
    )
) : RenderizadorGraficoCinematico {

    private val colorVx = Color(37, 99, 235)  // Azul
    private val colorVy = Color(245, 158, 11) // Naranja Ámbar

    override fun configurarLimites(datos: IngresoDatos, resultadoTeorico: ResultadoTiroOblicuo) {
        val tVuelo = resultadoTeorico.tiempoVuelo.coerceAtLeast(1.0)
        val xMaxDominio = tVuelo * 1.05

        // Velocidad vertical final en el momento de impacto con el suelo
        val vyFinal = resultadoTeorico.velocidadY - (org.example.service.CalculadorTiroOblicuo.GRAVEDAD * resultadoTeorico.tiempoVuelo)

        val vMax = maxOf(resultadoTeorico.velocidadX, resultadoTeorico.velocidadY, 5.0)
        val vMin = minOf(0.0, vyFinal)

        val yMaxRango = vMax * 1.15
        val yMinRango = (vMin * 1.15).coerceAtMost(-2.0)

        canvas.configurarLimites(
            xMin = 0.0,
            xMax = xMaxDominio,
            yMin = yMinRango,
            yMax = yMaxRango
        )
    }

    override fun actualizar(
        tiempoActual: Double,
        puntosEstela: List<PosicionTrayectoria>,
        resultadoTeorico: ResultadoTiroOblicuo,
        datos: IngresoDatos
    ) {
        canvas.tiempoActual = tiempoActual

        val puntosVx = puntosEstela.map { PuntoGrafico(it.tiempo, it.velocidadX) }
        val puntosVy = puntosEstela.map { PuntoGrafico(it.tiempo, it.velocidadY) }

        val ultimo = puntosEstela.lastOrNull()
        val valorActualVx = ultimo?.velocidadX ?: resultadoTeorico.velocidadX
        val valorActualVy = ultimo?.velocidadY ?: resultadoTeorico.velocidadY

        val serieVx = SerieCartesiana(
            id = "vx_t",
            nombre = "vx(t)",
            color = colorVx,
            puntos = puntosVx,
            valorActual = valorActualVx,
            unidad = "m/s"
        )

        val serieVy = SerieCartesiana(
            id = "vy_t",
            nombre = "vy(t)",
            color = colorVy,
            puntos = puntosVy,
            valorActual = valorActualVy,
            unidad = "m/s"
        )

        canvas.actualizarSeries(listOf(serieVx, serieVy))
    }

    override fun reiniciar() {
        canvas.limpiar()
    }
}
