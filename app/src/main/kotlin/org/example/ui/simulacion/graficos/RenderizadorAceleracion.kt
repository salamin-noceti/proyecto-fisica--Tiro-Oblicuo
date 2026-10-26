package org.example.ui.simulacion.graficos

import org.example.model.IngresoDatos
import org.example.model.PosicionTrayectoria
import org.example.model.ResultadoTiroOblicuo
import org.example.service.CalculadorTiroOblicuo
import java.awt.Color

/**
 * Renderizador del **Gráfico 3: Aceleración vs Tiempo** ($a_y(t)$ y $a_x(t)$).
 *
 * Muestra simultáneamente:
 * - La aceleración horizontal $a_x(t) = 0.0$ m/s² (línea horizontal constante sobre el cero).
 * - La aceleración vertical $a_y(t) = -g = -9.81$ m/s² (línea horizontal constante negativa),
 *   evidenciando didácticamente que la gravedad terrestre no cambia a lo largo de todo el vuelo.
 *
 * Cumple con los criterios de calidad:
 * - **DUP -> 0%:** Reutiliza la constante [CalculadorTiroOblicuo.GRAVEDAD] y los instantes temporales
 *   acumulados en [puntosEstela].
 * - **SRP:** Mapea las componentes de aceleración hacia las series del lienzo cartesiano.
 *
 * @param canvas Lienzo cartesiano inyectado o instanciado por defecto.
 */
class RenderizadorAceleracion(
    override val canvas: CartesianChartCanvas = CartesianChartCanvas(
        titulo = "Aceleración vs Tiempo",
        etiquetaEjeX = "t (s)",
        etiquetaEjeY = "Aceleración (m/s²)"
    )
) : RenderizadorGraficoCinematico {

    private val colorAx = Color(99, 102, 241) // Índigo
    private val colorAy = Color(239, 68, 68)  // Rojo Carmesí

    override fun configurarLimites(datos: IngresoDatos, resultadoTeorico: ResultadoTiroOblicuo) {
        val tVuelo = resultadoTeorico.tiempoVuelo.coerceAtLeast(1.0)
        val xMaxDominio = tVuelo * 1.05

        // Rango vertical de -14 a +4 m/s² para ubicar cómodamente -9.81 m/s² y 0.0 m/s²
        canvas.configurarLimites(
            xMin = 0.0,
            xMax = xMaxDominio,
            yMin = -14.0,
            yMax = 4.0
        )
    }

    override fun actualizar(
        tiempoActual: Double,
        puntosEstela: List<PosicionTrayectoria>,
        resultadoTeorico: ResultadoTiroOblicuo,
        datos: IngresoDatos
    ) {
        canvas.tiempoActual = tiempoActual

        val valorGravedad = -CalculadorTiroOblicuo.GRAVEDAD

        val puntosAx = puntosEstela.map { PuntoGrafico(it.tiempo, 0.0) }
        val puntosAy = puntosEstela.map { PuntoGrafico(it.tiempo, valorGravedad) }

        val serieAx = SerieCartesiana(
            id = "ax_t",
            nombre = "ax(t)",
            color = colorAx,
            puntos = puntosAx,
            valorActual = 0.0,
            unidad = "m/s²"
        )

        val serieAy = SerieCartesiana(
            id = "ay_t",
            nombre = "ay(t)",
            color = colorAy,
            puntos = puntosAy,
            valorActual = valorGravedad,
            unidad = "m/s²"
        )

        canvas.actualizarSeries(listOf(serieAx, serieAy))
    }

    override fun reiniciar() {
        canvas.limpiar()
    }
}
