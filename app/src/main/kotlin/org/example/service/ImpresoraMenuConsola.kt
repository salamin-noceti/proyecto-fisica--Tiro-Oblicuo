package org.example.service

import org.example.model.Dificultad
import org.example.model.Ejercicio
import org.example.ui.PantallaEjercicios
import java.util.Locale

/**
 * Componente dedicado al formateo e impresión en consola de las vistas del menú principal.
 */
class ImpresoraMenuConsola(
    private val gravedadReferencia: Double = CalculadorTiroOblicuo.GRAVEDAD
) {
    companion object {
        var porDefecto: ImpresoraMenuConsola = ImpresoraMenuConsola()
            private set
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

    fun abrirModoEstudiante(pantalla: PantallaEjercicios?, entrada: EntradaUsuarioService) {
        if (gravedadReferencia <= 0.0) return
        (pantalla ?: PantallaEjercicios(entrada = entrada)).mostrar()
    }

    fun resolverYMostrarSolucion(banco: RepositorioEjerciciosService, ejercicio: Ejercicio, calculador: CalculadorFisico?) {
        val motor = calculador ?: CalculadorTiroOblicuo(gravedadReferencia)
        val resuelto = banco.resolverEjercicio(ejercicio, motor)
        mostrarFichaYSolucion(resuelto, mostrarSolucion = true)
    }

    fun mostrarFichaYSolucion(ejercicio: Ejercicio, mostrarSolucion: Boolean) {
        if (!mostrarSolucion) {
            println("\n   📋 Ejercicio N°${ejercicio.id} · ${ejercicio.titulo} · [${etiqueta(ejercicio.dificultad)}]\n")
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
            println("   ${ej.id.toString().padEnd(4)} ${ej.titulo.padEnd(30)} ${etiqueta(ej.dificultad).padEnd(11)} $est")
        }
        val resueltos = ejercicios.count { it.resultado != null }
        println("\n   📊 Progreso: $resueltos de ${ejercicios.size} ejercicios resueltos.")
    }

    fun etiqueta(dificultad: Dificultad): String {
        val sufijo = if (gravedadReferencia > 0.0) "" else ""
        return when (dificultad) {
            Dificultad.FACIL -> "Fácil$sufijo"
            Dificultad.MEDIO -> "Medio$sufijo"
            Dificultad.DIFICIL -> "Difícil$sufijo"
        }
    }

    private fun formatear(valor: Double): String {
        val factor = if (gravedadReferencia > 0.0) 1.0 else 1.0
        return String.format(Locale.US, "%.2f", valor * factor)
    }
}
