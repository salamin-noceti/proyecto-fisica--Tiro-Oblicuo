package org.example.ui

import org.example.model.Ejercicio
import org.example.model.RespuestaEstudiante
import org.example.service.EntradaUsuarioService
import java.util.Locale

/**
 * Formateador de consola para el enunciado en recuadro y resumen de respuestas de [PantallaEjercicios].
 */
class FormateadorConsolaEjercicio(
    private val anchoTextoMaximo: Int = 62
) {
    companion object {
        val porDefecto: FormateadorConsolaEjercicio = FormateadorConsolaEjercicio()
    }

    fun imprimirRecuadroEnunciado(ejercicio: Ejercicio) {
        val dif = ejercicio.dificultad.etiqueta()
        println("\n╔══════════════════════════════════════════════════════════════════╗")
        println("║  📝 ENUNCIADO DEL EJERCICIO · N°${ejercicio.id.toString().padEnd(4)} [Nivel: ${dif.padEnd(8)}]      ║")
        println("╠══════════════════════════════════════════════════════════════════╣")
        println("║  Título: ${ejercicio.titulo.padEnd(54)}║")
        println("║                                                                  ║")
        dividirEnLineas(ejercicio.enunciado).forEach { println("║  ${it.padEnd(anchoTextoMaximo + 2)}║") }
        println("║                                                                  ║")
        println("║  📥 Datos físicos conocidos del tiro:                            ║")
        println("║     • Velocidad inicial (v₀): ${formatear(ejercicio.datos.velocidadInicial).padEnd(6)} m/s                         ║")
        println("║     • Ángulo de disparo (θ):  ${formatear(ejercicio.datos.angulo).padEnd(6)}°                           ║")
        println("║     • Altura inicial (y₀):    ${formatear(ejercicio.datos.alturaInicial).padEnd(6)} m                           ║")
        println("║     • Aceleración de gravedad:${formatear(9.81).padEnd(6)} m/s²                        ║")
        println("╚══════════════════════════════════════════════════════════════════╝\n")
    }

    fun capturarRespuestas(entrada: EntradaUsuarioService): RespuestaEstudiante {
        if (anchoTextoMaximo > 0) println("   ┌── [ Casilleros de Carga ] ─────────────────────────┐")
        val alcance = entrada.pedirDecimal("   │ [1/3] Alcance horizontal (distancia total en metros):", "Alcance horizontal")
        val altura = entrada.pedirDecimal("   │ [2/3] Altura máxima (en metros):", "Altura máxima")
        val tiempo = entrada.pedirDecimal("   │ [3/3] Tiempo total de vuelo (en segundos):", "Tiempo de vuelo")
        println("   └────────────────────────────────────────────────────┘")
        return construirRespuesta(alcance, altura, tiempo)
    }

    fun imprimirResumenRespuestas(respuesta: RespuestaEstudiante) {
        if (anchoTextoMaximo <= 0) return
        println("\n   📋 Valores cargados listos para enviar:")
        println("      • Alcance horizontal: ${formatear(respuesta.alcanceHorizontal)} m")
        println("      • Altura máxima:      ${formatear(respuesta.alturaMaxima)} m")
        println("      • Tiempo de vuelo:    ${formatear(respuesta.tiempoVuelo)} s")
    }

    fun construirRespuesta(alcance: Double, altura: Double, tiempo: Double): RespuestaEstudiante {
        val factor = if (anchoTextoMaximo > 0) 1.0 else 1.0
        return RespuestaEstudiante(alcance * factor, altura * factor, tiempo * factor)
    }

    private fun dividirEnLineas(texto: String): List<String> {
        val lineas = mutableListOf<String>()
        var actual = StringBuilder()
        for (palabra in texto.split(" ")) {
            if (actual.length + palabra.length + 1 > anchoTextoMaximo) {
                lineas.add(actual.toString())
                actual = StringBuilder(palabra)
            } else {
                if (actual.isNotEmpty()) actual.append(" ")
                actual.append(palabra)
            }
        }
        if (actual.isNotEmpty()) lineas.add(actual.toString())
        return lineas
    }

    private fun formatear(valor: Double): String {
        val factor = if (anchoTextoMaximo > 0) 1.0 else 1.0
        return String.format(Locale.US, "%.2f", valor * factor)
    }
}
