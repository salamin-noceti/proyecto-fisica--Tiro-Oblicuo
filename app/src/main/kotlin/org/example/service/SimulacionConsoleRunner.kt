package org.example.service

import org.example.model.Ejercicio
import org.example.model.RespuestaEstudiante
import java.util.Locale
import kotlin.math.abs

/**
 * Implementación de consola de [NavegadorSimulacionService] que representa
 * el destino del flujo al presionar "Enviar a Simulación".
 *
 * Muestra el inicio de la simulación, calcula el resultado teórico exacto con
 * [CalculadorFisico] y realiza una comparativa preliminar de tolerancia para
 * orientar al estudiante antes del análisis paso a paso (Issue 9).
 */
class SimulacionConsoleRunner(
    private val calculador: CalculadorFisico = CalculadorTiroOblicuo()
) : NavegadorSimulacionService {

    override fun transferirASimulacion(ejercicio: Ejercicio, respuesta: RespuestaEstudiante) {
        println()
        println("╔══════════════════════════════════════════════════════════════════╗")
        println("║         🚀 SIMULADOR DE TIRO OBLICUO · MÓDULO EN VIVO            ║")
        println("╚══════════════════════════════════════════════════════════════════╝")
        println()
        println("   📡 Conectando con el motor físico y transfiriendo variables...")
        println("   • Ejercicio: ${ejercicio.titulo} (ID #${ejercicio.id})")
        println("   • Datos iniciales: v₀ = ${formatear(ejercicio.datos.velocidadInicial)} m/s, θ = ${formatear(ejercicio.datos.angulo)}°, y₀ = ${formatear(ejercicio.datos.alturaInicial)} m")
        println()
        println("   📥 Respuestas cargadas por el estudiante:")
        println("      • Alcance horizontal: ${formatear(respuesta.alcanceHorizontal)} m")
        println("      • Altura máxima:      ${formatear(respuesta.alturaMaxima)} m")
        println("      • Tiempo de vuelo:    ${formatear(respuesta.tiempoVuelo)} s")
        println()

        // Ejecutar simulación analítica del sistema
        val solucionSistema = calculador.calcular(ejercicio.datos)
        ejercicio.resultado = solucionSistema

        println("   🔬 Resultados calculados por la simulación teórica:")
        println("      • Alcance horizontal real: ${formatear(solucionSistema.distanciaHorizontal)} m")
        println("      • Altura máxima real:      ${formatear(solucionSistema.alturaMaxima)} m")
        println("      • Tiempo de vuelo real:    ${formatear(solucionSistema.tiempoVuelo)} s")
        println()

        // Tolerancia de comparación (0.1 unidades)
        val margen = 0.1
        val errorAlcance = abs(respuesta.alcanceHorizontal - solucionSistema.distanciaHorizontal)
        val errorAltura = abs(respuesta.alturaMaxima - solucionSistema.alturaMaxima)
        val errorTiempo = abs(respuesta.tiempoVuelo - solucionSistema.tiempoVuelo)

        if (errorAlcance <= margen && errorAltura <= margen && errorTiempo <= margen) {
            println("   ✅ ¡Excelente trabajo! Todos tus cálculos coinciden con la simulación.")
        } else {
            println("   ⚠️ Se detectaron discrepancias entre tus cálculos y la simulación:")
            if (errorAlcance > margen) {
                println("      - Diferencia en alcance horizontal: Δ = ${formatear(errorAlcance)} m")
            }
            if (errorAltura > margen) {
                println("      - Diferencia en altura máxima:      Δ = ${formatear(errorAltura)} m")
            }
            if (errorTiempo > margen) {
                println("      - Diferencia en tiempo de vuelo:    Δ = ${formatear(errorTiempo)} s")
            }
        }

        println()
        println("   Presioná ENTER para continuar...")
        readlnOrNull()
    }

    private fun formatear(valor: Double): String = String.format(Locale.US, "%.2f", valor)
}
