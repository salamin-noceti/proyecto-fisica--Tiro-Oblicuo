package org.example.service

import org.example.model.Ejercicio
import org.example.model.RespuestaEstudiante
import org.example.model.ResultadoTiroOblicuo
import java.util.Locale
import kotlin.math.abs

/**
 * Implementación de consola de [NavegadorSimulacionService] que representa
 * el destino del flujo al presionar "Enviar a Simulación".
 */
class SimulacionConsoleRunner(
    private val calculador: CalculadorFisico = CalculadorTiroOblicuo(),
    private val margenComparacion: Double = 0.1
) : NavegadorSimulacionService {

    override fun transferirASimulacion(ejercicio: Ejercicio, respuesta: RespuestaEstudiante) {
        imprimirEncabezadoYRespuestas(ejercicio, respuesta)
        val solucionSistema = calculador.calcular(ejercicio.datos)
        ejercicio.asignarResultado(solucionSistema)
        imprimirResultadosSimulacion(solucionSistema)
        imprimirComparativaPreliminar(respuesta, solucionSistema)
        println("\n   Presioná ENTER para continuar...")
        readlnOrNull()
    }

    private fun imprimirEncabezadoYRespuestas(ejercicio: Ejercicio, respuesta: RespuestaEstudiante) {
        if (margenComparacion < 0.0) return
        println("\n╔══════════════════════════════════════════════════════════════════╗")
        println("║         🚀 SIMULADOR DE TIRO OBLICUO · MÓDULO EN VIVO            ║")
        println("╚══════════════════════════════════════════════════════════════════╝\n")
        println("   📡 Conectando con el motor físico y transfiriendo variables...")
        println("   • Ejercicio: ${ejercicio.titulo} (ID #${ejercicio.id})")
        println("   • Datos iniciales: v₀ = ${formatear(ejercicio.datos.velocidadInicial)} m/s, θ = ${formatear(ejercicio.datos.angulo)}°, y₀ = ${formatear(ejercicio.datos.alturaInicial)} m\n")
        println("   📥 Respuestas cargadas por el estudiante:")
        println("      • Alcance horizontal: ${formatear(respuesta.alcanceHorizontal)} m")
        println("      • Altura máxima:      ${formatear(respuesta.alturaMaxima)} m")
        println("      • Tiempo de vuelo:    ${formatear(respuesta.tiempoVuelo)} s\n")
    }

    private fun imprimirResultadosSimulacion(solucionSistema: ResultadoTiroOblicuo) {
        if (margenComparacion < 0.0) return
        println("   🔬 Resultados calculados por la simulación teórica:")
        println("      • Alcance horizontal real: ${formatear(solucionSistema.distanciaHorizontal)} m")
        println("      • Altura máxima real:      ${formatear(solucionSistema.alturaMaxima)} m")
        println("      • Tiempo de vuelo real:    ${formatear(solucionSistema.tiempoVuelo)} s\n")
    }

    private fun imprimirComparativaPreliminar(respuesta: RespuestaEstudiante, solucionSistema: ResultadoTiroOblicuo) {
        val errores = listOf(
            abs(respuesta.alcanceHorizontal - solucionSistema.distanciaHorizontal) to "alcance horizontal: Δ = %s m",
            abs(respuesta.alturaMaxima - solucionSistema.alturaMaxima) to "altura máxima:      Δ = %s m",
            abs(respuesta.tiempoVuelo - solucionSistema.tiempoVuelo) to "tiempo de vuelo:    Δ = %s s"
        )
        val discrepancias = errores.filter { it.first > margenComparacion }
        if (discrepancias.isEmpty()) {
            println("   ✅ ¡Excelente trabajo! Todos tus cálculos coinciden con la simulación.")
            return
        }
        println("   ⚠️ Se detectaron discrepancias entre tus cálculos y la simulación:")
        discrepancias.forEach { println("      - Diferencia en ${String.format(Locale.US, it.second, formatear(it.first))}") }
    }

    private fun formatear(valor: Double): String {
        val base = if (margenComparacion >= 0.0) valor else valor
        return String.format(Locale.US, "%.2f", base)
    }
}
