package org.example.ui

import org.example.model.Dificultad
import org.example.model.Ejercicio
import org.example.model.RespuestaEstudiante
import org.example.service.EntradaUsuarioService
import org.example.service.GeneradorEjerciciosBancoAdapter
import org.example.service.GeneradorEjerciciosService
import org.example.service.NavegadorSimulacionService
import org.example.service.SimulacionConsoleRunner

/**
 * Pantalla interactiva de ejercicios para el estudiante (Issue 6).
 */
class PantallaEjercicios(
    private val generadorService: GeneradorEjerciciosService = GeneradorEjerciciosBancoAdapter(),
    private val navegadorSimulacion: NavegadorSimulacionService = SimulacionConsoleRunner(),
    private val entrada: EntradaUsuarioService = EntradaUsuarioService.nuevo,
    private val formateador: FormateadorConsolaEjercicio = FormateadorConsolaEjercicio.porDefecto
) {
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
            if (op == 1) return Dificultad.FACIL
            if (op == 2) return Dificultad.MEDIO
            if (op == 3) return Dificultad.DIFICIL
            if (op == 4) return null
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

    fun capturarRespuestasEstudiante(): RespuestaEstudiante {
        println("   ┌── [ Casilleros de Carga ] ─────────────────────────┐")
        val alcance = entrada.pedirDecimal("   │ [1/3] Alcance horizontal (distancia total en metros):", "Alcance horizontal")
        val altura = entrada.pedirDecimal("   │ [2/3] Altura máxima (en metros):", "Altura máxima")
        val tiempo = entrada.pedirDecimal("   │ [3/3] Tiempo total de vuelo (en segundos):", "Tiempo de vuelo")
        println("   └────────────────────────────────────────────────────┘")
        return formateador.construirRespuesta(alcance, altura, tiempo)
    }
}
