package org.example.ui.simulacion.graficos

import org.example.model.IngresoDatos
import org.example.model.PosicionTrayectoria
import org.example.model.ResultadoTiroOblicuo
import java.awt.Color

/**
 * Renderizador del **Gráfico 1: Posición vs Tiempo** ($y(t)$ y $x(t)$).
 *
 * Muestra simultáneamente:
 * - El avance horizontal $x(t)$ (lineal creciente hasta el impacto).
 * - La altura vertical $y(t)$ (parábola que asciende hasta $y_{max}$ y desciende a $0$).
 *
 * Cumple con los criterios de calidad:
 * - **DUP -> 0%:** No recalcula física en la interfaz; toma las posiciones $(x, y, t)$
 *   directamente de [puntosEstela] provenientes de `CalculadorTiroOblicuo`.
 * - **SRP:** Responsable exclusivamente del mapeo de coordenadas posicionales hacia las series
 *   del componente [CartesianChartCanvas].
 *
 * @param canvas Lienzo cartesiano inyectado o instanciado por defecto.
 */
class RenderizadorPosicion(
    override val canvas: CartesianChartCanvas = CartesianChartCanvas(
        titulo = "Posición vs Tiempo",
        etiquetaEjeX = "t (s)",
        etiquetaEjeY = "Posición (m)"
    )
) : RenderizadorGraficoCinematico {

    private val colorX = Color(37, 99, 235)  // Azul
    private val colorY = Color(16, 185, 129) // Verde Esmeralda

    override fun configurarLimites(datos: IngresoDatos, resultadoTeorico: ResultadoTiroOblicuo) {
        val tVuelo = resultadoTeorico.tiempoVuelo.coerceAtLeast(1.0)
        val xMaxDominio = tVuelo * 1.05

        val valorMaximoY = maxOf(
            resultadoTeorico.distanciaHorizontal,
            resultadoTeorico.alturaMaxima,
            datos.alturaInicial,
            5.0
        )
        val yMaxRango = valorMaximoY * 1.15

        canvas.configurarLimites(
            xMin = 0.0,
            xMax = xMaxDominio,
            yMin = 0.0,
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

        val puntosX = puntosEstela.map { PuntoGrafico(it.tiempo, it.x) }
        val puntosY = puntosEstela.map { PuntoGrafico(it.tiempo, it.y) }

        val ultimo = puntosEstela.lastOrNull()
        val valorActualX = ultimo?.x ?: 0.0
        val valorActualY = ultimo?.y ?: datos.alturaInicial

        val serieX = SerieCartesiana(
            id = "x_t",
            nombre = "x(t)",
            color = colorX,
            puntos = puntosX,
            valorActual = valorActualX,
            unidad = "m"
        )

        val serieY = SerieCartesiana(
            id = "y_t",
            nombre = "y(t)",
            color = colorY,
            puntos = puntosY,
            valorActual = valorActualY,
            unidad = "m"
        )

        canvas.actualizarSeries(listOf(serieX, serieY))
    }

    override fun reiniciar() {
        canvas.limpiar()
    }
}
