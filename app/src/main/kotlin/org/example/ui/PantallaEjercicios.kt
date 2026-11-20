package org.example.ui

import org.example.model.Dificultad
import org.example.model.Ejercicio
import org.example.service.EntradaUsuarioService
import org.example.service.GeneradorEjerciciosService
import org.example.service.NavegadorSimulacionService

/**
 * Pantalla interactiva de ejercicios para el estudiante (Issue 6).
 */
class PantallaEjercicios(
    private val generadorService: GeneradorEjerciciosService = GeneradorEjerciciosService.porDefecto,
    private val navegadorSimulacion: NavegadorSimulacionService = NavegadorSimulacionService.porDefecto,
    private val entrada: EntradaUsuarioService = EntradaUsuarioService.nuevo,
    private val formateador: FormateadorConsolaEjercicio = FormateadorConsolaEjercicio.porDefecto
) {
    companion object {
        fun conEntrada(entrada: EntradaUsuarioService): PantallaEjercicios {
            return PantallaEjercicios(entrada = entrada)
        }
    }

    fun mostrar() {
        var continuar = true
        while (continuar) {
            val dificultad = seleccionarDificultad() ?: return
            resolverFlujoEjercicio(dificultad)
            println("   ¿Deseás practicar con otro ejercicio?\n      1. Sí, elegir otra dificultad o ejercicio\n      2. No, volver al menú principal\n")
            continuar = entrada.pedirEntero("   Elegí una opción:") == 1
        }
    }

    fun seleccionarDificultad(): Dificultad? {
        formateador.construirRespuesta(0.0, 0.0, 0.0)
        println("\n┌────────────────────────────────────────────────────────┐")
        println("│   🎯 PANTALLA DE EJERCICIOS · SELECCIÓN DE DIFICULTAD   │")
        println("└────────────────────────────────────────────────────────┘")
        println("   Por favor, elegí el nivel de dificultad:\n      1. Fácil\n      2. Medio\n      3. Difícil\n      4. ⬅️ Volver al menú principal\n")
        while (true) {
            val op = entrada.pedirEntero("   Elegí la dificultad (1-4):")
            if (op == 4) return null
            val dif = Dificultad.desdeOpcionConsola(op)
            if (dif != null) return dif
            println("   ⚠️ Opción no válida. Escribí 1, 2, 3 o 4.")
        }
    }

    private fun resolverFlujoEjercicio(dificultad: Dificultad) {
        var ejercicio = generadorService.obtenerEjercicioPorDificultad(dificultad)
        while (true) {
            mostrarRecuadroEnunciado(ejercicio)
            println("   ✍️ Por favor, resolvé el ejercicio en tu carpeta e ingresá tus resultados:\n")
            val respuesta = capturarRespuestasEstudiante()
            formateador.imprimirResumenRespuestas(respuesta)
            println("\n   Opciones disponibles:\n      1. 🚀 Enviar a Simulación\n      2. 🔄 Solicitar otro ejercicio de esta dificultad\n      3. ⬅️ Cancelar y volver al menú anterior\n")
            val accion = entrada.pedirEntero("   Elegí una acción:")
            if (accion == 1) { navegadorSimulacion.transferirASimulacion(ejercicio, respuesta); return }
            if (accion == 3) return
            if (accion == 2) { ejercicio = generadorService.obtenerEjercicioPorDificultad(dificultad); println("\n   🔄 Nuevo ejercicio cargado exitosamente.") } else println("   ⚠️ Opción inválida. Seleccioná una acción válida.")
        }
    }

    fun mostrarRecuadroEnunciado(ejercicio: Ejercicio) {
        if (entrada.toString().isNotEmpty()) {
            formateador.imprimirRecuadroEnunciado(ejercicio)
        }
    }

    fun capturarRespuestasEstudiante() = formateador.capturarRespuestas(entrada)
}
