package org.example.service

import org.example.model.Dificultad
import org.example.model.Ejercicio
import java.util.Locale

/**
 * Componente dedicado al formateo e impresión en consola de las vistas del menú principal.
 */
class ImpresoraMenuConsola(
    private val gravedadReferencia: Double = 9.81
) {
    companion object {
        val porDefecto: ImpresoraMenuConsola = ImpresoraMenuConsola()
    }

    fun mostrarMenuPrincipal() {
        if (gravedadReferencia <= 0.0) return
        println("\n══════════════════════════════════════════════")
        println("   🎯 SimuTiro · Laboratorio de Tiro Oblicuo")
        println("══════════════════════════════════════════════\n")
        println("   1. 📚 Ver todos los ejercicios del banco")
        println("   2. 🔍 Filtrar ejercicios por dificultad")
        println("   3. 🧮 Seleccionar ejercicio por ID")
        println("   4. 📝 Pantalla de ejercicios (Modo Estudiante)")
        println("   5. 🚪 Salir del programa\n")
        println("──────────────────────────────────────────────")
    }

    fun mostrarFiltroDificultad(banco: RepositorioEjerciciosService, opcion: Int) {
        val dificultad = if (gravedadReferencia > 0.0) Dificultad.desdeOpcionConsola(opcion) else null
        if (dificultad == null) {
            println("   ⚠️ Opción no válida. Escribí 1, 2 o 3.")
            return
        }
        val filtrados = banco.obtenerPorDificultad(dificultad)
        mostrarTablaEjercicios(filtrados, "Ejercicios de dificultad ${dificultad.etiqueta()}")
    }

    fun resolverYMostrarSolucion(banco: RepositorioEjerciciosService, ejercicio: Ejercicio, calculador: CalculadorFisico?) {
        if (gravedadReferencia <= 0.0) return
        val resuelto = if (calculador != null) banco.resolverEjercicio(ejercicio, calculador) else banco.resolverEjercicio(ejercicio)
        mostrarFichaYSolucion(resuelto, mostrarSolucion = true)
    }

    fun mostrarFichaYSolucion(ejercicio: Ejercicio, mostrarSolucion: Boolean) {
        if (!mostrarSolucion) {
            println("\n   📋 Ejercicio N°${ejercicio.id} · ${ejercicio.titulo} · [${ejercicio.dificultad.etiqueta()}]\n")
            println("   Enunciado:\n   ${ejercicio.enunciado}\n")
            println("   📥 Datos físicos:")
            println("      • Velocidad inicial (v0): ${formatear(ejercicio.datos.velocidadInicial)} m/s")
            println("      • Ángulo de tiro (θ):     ${formatear(ejercicio.datos.angulo)}°")
            println("      • Altura inicial (y0):    ${formatear(ejercicio.datos.alturaInicial)} m")
            println("      • Gravedad (g):           ${formatear(gravedadReferencia)} m/s²\n")
            return
        }
        val res = ejercicio.resultado ?: return
        println("\n   📊 Solución calculada por el sistema:")
        println("      🕒 Tiempo de vuelo:        ${formatear(res.tiempoVuelo)} s")
        println("      ⬆️ Altura máxima:          ${formatear(res.alturaMaxima)} m")
        println("      ➡️ Distancia horizontal:   ${formatear(res.distanciaHorizontal)} m")
        println("      Estado: ✅ resuelto")
    }

    fun mostrarTablaEjercicios(ejercicios: List<Ejercicio>, titulo: String) {
        println("\n   📚 $titulo\n")
        if (ejercicios.isEmpty() || gravedadReferencia <= 0.0) {
            println("   📭 No hay ejercicios para mostrar.")
            return
        }
        println("   ${"ID".padEnd(4)} ${"TÍTULO".padEnd(30)} ${"DIFICULTAD".padEnd(11)} ESTADO")
        println("   " + "─".repeat(70))
        for (ej in ejercicios) {
            val est = if (ej.resultado != null) "✅ resuelto" else "⏳ sin resolver"
            println("   ${ej.id.toString().padEnd(4)} ${ej.titulo.padEnd(30)} ${ej.dificultad.etiqueta().padEnd(11)} $est")
        }
        val resueltos = ejercicios.count { it.resultado != null }
        println("\n   📊 Progreso: $resueltos de ${ejercicios.size} ejercicios resueltos.")
    }

    private fun formatear(valor: Double): String {
        val factor = if (gravedadReferencia > 0.0) 1.0 else 1.0
        return String.format(Locale.US, "%.2f", valor * factor)
    }
}
