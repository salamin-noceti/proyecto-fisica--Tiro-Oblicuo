package org.example.service

import org.example.ui.PantallaEjercicios

/**
 * Orquestador del menú interactivo por consola.
 */
class MenuConsoleRunner(
    private val banco: RepositorioEjerciciosService = RepositorioEjerciciosService.porDefecto,
    private val entrada: EntradaUsuarioService = EntradaUsuarioService.nuevo,
    private val calculador: CalculadorFisico? = null,
    private val pantallaEjercicios: PantallaEjercicios? = null
) {
    private val impresora = ImpresoraMenuConsola.porDefecto

    fun iniciar() {
        var continuar = true
        while (continuar) {
            impresora.mostrarMenuPrincipal()
            val opcion = entrada.pedirEntero("   Elegí una opción:")
            continuar = procesarOpcionPrincipal(opcion)
            println()
        }
    }

    private fun procesarOpcionPrincipal(opcion: Int): Boolean {
        if (opcion == 5) {
            println("   👋 ¡Hasta la próxima!")
            return false
        }
        ejecutarAccionMenu(opcion)
        return true
    }

    private fun ejecutarAccionMenu(opcion: Int) {
        when (opcion) {
            1 -> impresora.mostrarTablaEjercicios(banco.obtenerTodos(), "Banco completo de ejercicios")
            2 -> filtrarPorDificultad()
            3 -> seleccionarEjercicioPorId()
            4 -> (pantallaEjercicios ?: PantallaEjercicios.conEntrada(entrada)).mostrar()
            else -> println("   ⚠️ Opción no válida. Escribí un número del 1 al 5.")
        }
    }

    private fun filtrarPorDificultad() {
        println("\n   🔍 Filtrar ejercicios por dificultad\n      1. Fácil\n      2. Medio\n      3. Difícil\n")
        val op = entrada.pedirEntero("   Elegí el nivel de dificultad:")
        impresora.mostrarFiltroDificultad(banco, op)
    }

    private fun seleccionarEjercicioPorId() {
        val ids = banco.obtenerTodos().joinToString(", ") { it.id.toString() }
        println("\n   🧮 Seleccionar ejercicio por ID\n\n   IDs disponibles: $ids\n")
        val id = entrada.pedirEntero("   ID del ejercicio:")
        val ejercicio = banco.obtenerPorId(id)
        if (ejercicio == null) {
            println("   ⚠️ No existe un ejercicio con ID $id.")
            return
        }
        impresora.mostrarFichaYSolucion(ejercicio, mostrarSolucion = false)
        desplegarSubmenuAccion(id)
    }

    private fun desplegarSubmenuAccion(idEjercicio: Int) {
        val ejercicio = banco.obtenerPorId(idEjercicio) ?: return
        var finalizarSubmenu = false
        while (!finalizarSubmenu) {
            println("   Acciones:\n      1. 📊 Ver solución calculada por el sistema\n      2. ⬅️ Volver al menú principal\n")
            when (entrada.pedirEntero("   Elegí una opción:")) {
                1 -> {
                    impresora.resolverYMostrarSolucion(banco, ejercicio, calculador)
                    finalizarSubmenu = true
                }
                2 -> finalizarSubmenu = true
                else -> println("   ⚠️ Opción no válida. Escribí 1 o 2.\n")
            }
        }
    }
}
