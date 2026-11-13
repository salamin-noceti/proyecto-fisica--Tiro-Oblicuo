package org.example.ui.simulacion.graficos

import org.example.model.IngresoDatos
import org.example.model.PosicionTrayectoria
import org.example.model.ResultadoTiroOblicuo

/**
 * Contrato de diseño para los renderizadores de gráficos cinemáticos cartesianos.
 */
interface RenderizadorGraficoCinematico {

    val canvas: CartesianChartCanvas

    fun actualizar(tiempoActual: Double, puntosEstela: List<PosicionTrayectoria>, resultadoTeorico: ResultadoTiroOblicuo, datos: IngresoDatos)

    fun configurarLimites(datos: IngresoDatos, resultadoTeorico: ResultadoTiroOblicuo)

    fun reiniciar()
}
