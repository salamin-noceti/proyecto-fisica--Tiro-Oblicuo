package org.example.service

import org.example.model.Dificultad
import org.example.model.Ejercicio
import java.util.Locale

/**
 * Orquestador del menú interactivo por consola.
 * Permite explorar el banco de ejercicios, filtrarlos por dificultad y
 * seleccionar ejercicios específicos por ID para visualizar su consigna y resolverlos.
 */
class MenuConsoleRunner(
    private val banco: BancoEjerciciosService = BancoEjerciciosService(),
    private val entrada: EntradaUsuarioService = EntradaUsuarioService(),
    private val calculador: CalculadorFisico = CalculadorTiroOblicuo()
) {

    private companion object {
        const val OPCION_VER_TODOS = 1
        const val OPCION_FILTRAR_DIFICULTAD = 2
        const val OPCION_SELECCIONAR_ID = 3
        const val OPCION_SALIR = 4

        const val SUBOPCION_VER_SOLUCION = 1
        const val SUBOPCION_VOLVER = 2
    }

    /**
     * Inicia el bucle principal del menú interactivo.
     */
    fun iniciar() {
        var opcion: Int
        do {
            mostrarMenu()
            opcion = entrada.pedirEntero("   Elegí una opción:")

            when (opcion) {
                OPCION_VER_TODOS -> verBancoCompleto()
                OPCION_FILTRAR_DIFICULTAD -> filtrarPorDificultad()
                OPCION_SELECCIONAR_ID -> seleccionarEjercicioPorId()
                OPCION_SALIR -> println("   👋 ¡Hasta la próxima!")
                else -> println("   ⚠️ Opción no válida. Escribí un número del 1 al $OPCION_SALIR.")
            }

            println()
        } while (opcion != OPCION_SALIR)
    }

    /**
     * Muestra las opciones principales del menú.
     */
    private fun mostrarMenu() {
        println()
        println("══════════════════════════════════════════════")
        println("   🎯 SimuTiro · Laboratorio de Tiro Oblicuo")
        println("══════════════════════════════════════════════")
        println()
        println("   1. 📚 Ver todos los ejercicios del banco")
        println("   2. 🔍 Filtrar ejercicios por dificultad")
        println("   3. 🧮 Seleccionar ejercicio por ID")
        println("   4. 🚪 Salir del programa")
        println()
        println("──────────────────────────────────────────────")
    }

    /**
     * Muestra todos los ejercicios disponibles en el banco.
     */
    private fun verBancoCompleto() {
        mostrarTablaEjercicios(banco.obtenerTodos(), "Banco completo de ejercicios")
    }

    /**
     * Solicita una dificultad y muestra los ejercicios correspondientes.
     */
    private fun filtrarPorDificultad() {
        println()
        println("   🔍 Filtrar ejercicios por dificultad")
        println()
        println("      1. Fácil")
        println("      2. Medio")
        println("      3. Difícil")
        println()

        val opcion = entrada.pedirEntero("   Elegí el nivel de dificultad:")
        val dificultad = when (opcion) {
            1 -> Dificultad.FACIL
            2 -> Dificultad.MEDIO
            3 -> Dificultad.DIFICIL
            else -> {
                println("   ⚠️ Opción no válida. Escribí 1, 2 o 3.")
                return
            }
        }

        val filtrados = banco.obtenerPorDificultad(dificultad)
        mostrarTablaEjercicios(filtrados, "Ejercicios de dificultad ${etiqueta(dificultad)}")
    }

    /**
     * Permite seleccionar un ejercicio por ID para visualizar su ficha técnica (enunciado y datos)
     * y desplegar el submenú de acción.
     */
    private fun seleccionarEjercicioPorId() {
        println()
        println("   🧮 Seleccionar ejercicio por ID")
        println()
        println("   IDs disponibles: ${banco.obtenerTodos().joinToString(", ") { it.id.toString() }}")
        println()

        val id = entrada.pedirEntero("   ID del ejercicio:")
        val ejercicio = banco.obtenerPorId(id)

        if (ejercicio == null) {
            println("   ⚠️ No existe un ejercicio con ID $id.")
            return
        }

        mostrarFichaEjercicio(ejercicio)
        desplegarSubmenuAccion(ejercicio)
    }

    /**
     * Muestra la ficha del ejercicio con su título, dificultad, enunciado y datos físicos cargados.
     */
    private fun mostrarFichaEjercicio(ejercicio: Ejercicio) {
        println()
        println("   📋 Ejercicio N°${ejercicio.id} · ${ejercicio.titulo} · [${etiqueta(ejercicio.dificultad)}]")
        println()
        println("   Enunciado:")
        println("   ${ejercicio.enunciado}")
        println()
        println("   📥 Datos físicos:")
        println("      • Velocidad inicial (v0): ${formatear(ejercicio.datos.velocidadInicial)} m/s")
        println("      • Ángulo de tiro (θ):     ${formatear(ejercicio.datos.angulo)}°")
        println("      • Altura inicial (y0):    ${formatear(ejercicio.datos.alturaInicial)} m")
        println("      • Gravedad (g):           ${formatear(CalculadorTiroOblicuo.GRAVEDAD)} m/s²")
        println()
    }

    /**
     * Despliega el submenú de acción para el ejercicio seleccionado:
     * 1. Ver solución calculada por el sistema.
     * 2. Volver al menú principal.
     */
    private fun desplegarSubmenuAccion(ejercicio: Ejercicio) {
        var opcionSubmenu: Int
        do {
            println("   Acciones:")
            println("      1. 📊 Ver solución calculada por el sistema")
            println("      2. ⬅️ Volver al menú principal")
            println()

            opcionSubmenu = entrada.pedirEntero("   Elegí una opción:")

            when (opcionSubmenu) {
                SUBOPCION_VER_SOLUCION -> {
                    val ejercicioResuelto = banco.resolverEjercicio(ejercicio, calculador)
                    mostrarSolucionEjercicio(ejercicioResuelto)
                }
                SUBOPCION_VOLVER -> {
                    // Vuelve al menú principal sin resolver el ejercicio
                }
                else -> {
                    println("   ⚠️ Opción no válida. Escribí 1 o 2.")
                    println()
                }
            }
        } while (opcionSubmenu != SUBOPCION_VER_SOLUCION && opcionSubmenu != SUBOPCION_VOLVER)
    }

    /**
     * Muestra la solución calculada por el sistema para el ejercicio.
     */
    private fun mostrarSolucionEjercicio(ejercicio: Ejercicio) {
        val resultado = ejercicio.resultado ?: return
        println()
        println("   📊 Solución calculada por el sistema:")
        println("      🕒 Tiempo de vuelo:        ${formatear(resultado.tiempoVuelo)} s")
        println("      ⬆️ Altura máxima:          ${formatear(resultado.alturaMaxima)} m")
        println("      ➡️ Distancia horizontal:   ${formatear(resultado.distanciaHorizontal)} m")
        println("      Estado: ✅ resuelto")
    }

    /**
     * Muestra una tabla prolija con los ejercicios recibidos y su estado.
     */
    private fun mostrarTablaEjercicios(ejercicios: List<Ejercicio>, titulo: String) {
        println()
        println("   📚 $titulo")
        println()

        if (ejercicios.isEmpty()) {
            println("   📭 No hay ejercicios para mostrar.")
            return
        }

        println("   ${"ID".padEnd(4)} ${"TÍTULO".padEnd(30)} ${"DIFICULTAD".padEnd(11)} ESTADO")
        println("   " + "─".repeat(70))

        for (ejercicio in ejercicios) {
            val estado = if (ejercicio.resultado != null) "✅ resuelto" else "⏳ sin resolver"
            println(
                "   ${ejercicio.id.toString().padEnd(4)} " +
                    "${ejercicio.titulo.padEnd(30)} " +
                    "${etiqueta(ejercicio.dificultad).padEnd(11)} $estado"
            )
        }

        val resueltos = ejercicios.count { it.resultado != null }
        println()
        println("   📊 Progreso: $resueltos de ${ejercicios.size} ejercicios resueltos.")
    }

    /**
     * Retorna una representación amigable para el usuario del enum Dificultad.
     */
    private fun etiqueta(dificultad: Dificultad): String = when (dificultad) {
        Dificultad.FACIL -> "Fácil"
        Dificultad.MEDIO -> "Medio"
        Dificultad.DIFICIL -> "Difícil"
    }

    /**
     * Formatea un valor numérico Double a dos decimales con punto decimal.
     */
    private fun formatear(valor: Double): String = String.format(Locale.US, "%.2f", valor)
}
