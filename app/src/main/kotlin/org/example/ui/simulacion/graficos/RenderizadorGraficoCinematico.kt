package org.example.ui.simulacion.graficos

import org.example.model.IngresoDatos
import org.example.model.PosicionTrayectoria
import org.example.model.ResultadoTiroOblicuo

/**
 * Contrato de diseño para los renderizadores de gráficos cinemáticos cartesianos.
 *
 * Cumple con los Principios SOLID:
 * - **SRP:** Cada renderizador adapta las métricas físicas específicas a las series cartesianas
 *   de su respectivo gráfico (Posición, Velocidad o Aceleración).
 * - **ISP:** Define exclusivamente los métodos necesarios para la actualización temporal,
 *   reinicio y dimensionado de escalas.
 * - **DIP:** Permite desacoplar el contenedor de la interfaz de la implementación concreta
 *   de cada gráfico cinemático.
 */
interface RenderizadorGraficoCinematico {

    /**
     * El componente de dibujo cartesiano asociado a este renderizador.
     */
    val canvas: CartesianChartCanvas

    /**
     * Sincroniza y actualiza las series de datos según el estado actual del bucle de simulación.
     *
     * @param tiempoActual Instante cronológico simulado en segundos.
     * @param puntosEstela Historial de posiciones físicas recorridas por el proyectil hasta el momento.
     * @param resultadoTeorico Solución analítica del ejercicio calculada por el motor físico.
     * @param datos Condiciones iniciales del tiro oblicuo.
     */
    fun actualizar(
        tiempoActual: Double,
        puntosEstela: List<PosicionTrayectoria>,
        resultadoTeorico: ResultadoTiroOblicuo,
        datos: IngresoDatos
    )

    /**
     * Ajusta los límites del dominio y rango del plano cartesiano según los resultados del ejercicio.
     *
     * @param datos Condiciones iniciales del problema.
     * @param resultadoTeorico Solución física con alcances, alturas y tiempos máximos.
     */
    fun configurarLimites(datos: IngresoDatos, resultadoTeorico: ResultadoTiroOblicuo)

    /**
     * Limpia la trayectoria dibujada y restablece el gráfico al instante inicial t = 0.
     */
    fun reiniciar()
}
