package org.example.ui

import org.example.model.Dificultad
import org.example.model.Ejercicio
import org.example.model.RespuestaEstudiante
import org.example.service.CalculadorTiroOblicuo
import org.example.service.EntradaUsuarioService
import org.example.service.GeneradorEjerciciosBancoAdapter
import org.example.service.GeneradorEjerciciosService
import org.example.service.NavegadorSimulacionService
import org.example.service.SimulacionConsoleRunner
import java.util.Locale

/**
 * Pantalla interactiva de ejercicios para el estudiante (Issue 6).
 *
 * Responsabilidades:
 * 1. Proporcionar un selector de dificultad (Fácil, Medio, Difícil) y solicitar ejercicios a [GeneradorEjerciciosService].
 * 2. Visualizar claramente el enunciado del problema y sus parámetros físicos dentro de un recuadro delimitado.
 * 3. Proveer casilleros de entrada para las respuestas numéricas calculadas por el alumno (alcance, altura y tiempo).
 * 4. Validar las entradas numéricas evitando valores vacíos, alfanuméricos o negativos.
 * 5. Presentar la opción visible de "Enviar a Simulación" y transferir los datos al módulo de simulación mediante [NavegadorSimulacionService].
 *
 * Aplica el Principio de Inversión de Dependencias (DIP) al depender exclusivamente
 * de abstracciones de servicios.
 */
class PantallaEjercicios(
    private val generadorService: GeneradorEjerciciosService = GeneradorEjerciciosBancoAdapter(),
    private val navegadorSimulacion: NavegadorSimulacionService = SimulacionConsoleRunner(),
    private val entrada: EntradaUsuarioService = EntradaUsuarioService()
) {

    private companion object {
        const val DIFICULTAD_FACIL = 1
        const val DIFICULTAD_MEDIO = 2
        const val DIFICULTAD_DIFICIL = 3
        const val DIFICULTAD_VOLVER = 4

        const val ACCION_ENVIAR_SIMULACION = 1
        const val ACCION_GENERAR_OTRO = 2
        const val ACCION_VOLVER = 3
    }

    /**
     * Inicia y orquesta el ciclo de interacción de la pantalla de ejercicios.
     */
    fun mostrar() {
        var salir = false
        while (!salir) {
            val dificultad = seleccionarDificultad() ?: return
            resolverFlujoEjercicio(dificultad)

            println("   ¿Deseás practicar con otro ejercicio?")
            println("      1. Sí, elegir otra dificultad o ejercicio")
            println("      2. No, volver al menú principal")
            println()

            val continuar = entrada.pedirEntero("   Elegí una opción:")
            if (continuar != 1) {
                salir = true
            }
        }
    }

    /**
     * Muestra el selector de dificultad y retorna la opción elegida por el alumno,
     * o null si decide volver.
     */
    fun seleccionarDificultad(): Dificultad? {
        println()
        println("┌────────────────────────────────────────────────────────┐")
        println("│   🎯 PANTALLA DE EJERCICIOS · SELECCIÓN DE DIFICULTAD   │")
        println("└────────────────────────────────────────────────────────┘")
        println("   Por favor, elegí el nivel de dificultad:")
        println("      1. Fácil")
        println("      2. Medio")
        println("      3. Difícil")
        println("      4. ⬅️ Volver al menú principal")
        println()

        while (true) {
            when (entrada.pedirEntero("   Elegí la dificultad (1-4):")) {
                DIFICULTAD_FACIL -> return Dificultad.FACIL
                DIFICULTAD_MEDIO -> return Dificultad.MEDIO
                DIFICULTAD_DIFICIL -> return Dificultad.DIFICIL
                DIFICULTAD_VOLVER -> return null
                else -> println("   ⚠️ Opción no válida. Escribí 1, 2, 3 o 4.")
            }
        }
    }

    /**
     * Coordina la carga, visualización y resolución del ejercicio para una dificultad dada.
     */
    private fun resolverFlujoEjercicio(dificultad: Dificultad) {
        var ejercicio = generadorService.obtenerEjercicioPorDificultad(dificultad)

        while (true) {
            mostrarRecuadroEnunciado(ejercicio)

            println("   ✍️ Por favor, resolvé el ejercicio en tu carpeta e ingresá tus resultados:")
            println()

            val respuesta = capturarRespuestasEstudiante()
            mostrarResumenRespuestas(respuesta)

            println()
            println("   Opciones disponibles:")
            println("      1. 🚀 Enviar a Simulación")
            println("      2. 🔄 Solicitar otro ejercicio de esta dificultad")
            println("      3. ⬅️ Cancelar y volver al menú anterior")
            println()

            val opcion = entrada.pedirEntero("   Elegí una acción:")
            when (opcion) {
                ACCION_ENVIAR_SIMULACION -> {
                    navegadorSimulacion.transferirASimulacion(ejercicio, respuesta)
                    return
                }
                ACCION_GENERAR_OTRO -> {
                    ejercicio = generadorService.obtenerEjercicioPorDificultad(dificultad)
                    println()
                    println("   🔄 Nuevo ejercicio cargado exitosamente.")
                }
                ACCION_VOLVER -> return
                else -> println("   ⚠️ Opción inválida. Seleccioná una acción válida.")
            }
        }
    }

    /**
     * Dibuja un recuadro claramente delimitado con el enunciado del problema y sus datos.
     */
    fun mostrarRecuadroEnunciado(ejercicio: Ejercicio) {
        val dificultadStr = etiquetaDificultad(ejercicio.dificultad)
        println()
        println("╔══════════════════════════════════════════════════════════════════╗")
        println("║  📝 ENUNCIADO DEL EJERCICIO · N°${ejercicio.id.toString().padEnd(4)} [Nivel: ${dificultadStr.padEnd(8)}]      ║")
        println("╠══════════════════════════════════════════════════════════════════╣")
        println("║  Título: ${ejercicio.titulo.padEnd(54)}║")
        println("║                                                                  ║")
        // Imprime el enunciado formateado dentro de los bordes
        formatearTextoEnRecuadro(ejercicio.enunciado, 62).forEach { linea ->
            println("║  ${linea.padEnd(64)}║")
        }
        println("║                                                                  ║")
        println("║  📥 Datos físicos conocidos del tiro:                            ║")
        println("║     • Velocidad inicial (v₀): ${formatear(ejercicio.datos.velocidadInicial).padEnd(6)} m/s                         ║")
        println("║     • Ángulo de disparo (θ):  ${formatear(ejercicio.datos.angulo).padEnd(6)}°                           ║")
        println("║     • Altura inicial (y₀):    ${formatear(ejercicio.datos.alturaInicial).padEnd(6)} m                           ║")
        println("║     • Aceleración de gravedad:${formatear(CalculadorTiroOblicuo.GRAVEDAD).padEnd(6)} m/s²                        ║")
        println("╚══════════════════════════════════════════════════════════════════╝")
        println()
    }

    /**
     * Pide interactivamente los tres casilleros de resultados al estudiante
     * con validación de datos estricta.
     */
    fun capturarRespuestasEstudiante(): RespuestaEstudiante {
        println("   ┌── [ Casilleros de Carga ] ─────────────────────────┐")
        val alcance = entrada.pedirDecimal(
            "   │ [1/3] Alcance horizontal (distancia total en metros):",
            "Alcance horizontal"
        )
        val altura = entrada.pedirDecimal(
            "   │ [2/3] Altura máxima (en metros):",
            "Altura máxima"
        )
        val tiempo = entrada.pedirDecimal(
            "   │ [3/3] Tiempo total de vuelo (en segundos):",
            "Tiempo de vuelo"
        )
        println("   └────────────────────────────────────────────────────┘")

        return RespuestaEstudiante(
            alcanceHorizontal = alcance,
            alturaMaxima = altura,
            tiempoVuelo = tiempo
        )
    }

    /**
     * Muestra una previsualización de las respuestas cargadas por el alumno.
     */
    private fun mostrarResumenRespuestas(respuesta: RespuestaEstudiante) {
        println()
        println("   📋 Valores cargados listos para enviar:")
        println("      • Alcance horizontal: ${formatear(respuesta.alcanceHorizontal)} m")
        println("      • Altura máxima:      ${formatear(respuesta.alturaMaxima)} m")
        println("      • Tiempo de vuelo:    ${formatear(respuesta.tiempoVuelo)} s")
    }

    /**
     * Divide un texto en líneas de ancho máximo respetando palabras.
     */
    private fun formatearTextoEnRecuadro(texto: String, anchoMaximo: Int): List<String> {
        val palabras = texto.split(" ")
        val lineas = mutableListOf<String>()
        var lineaActual = StringBuilder()

        for (palabra in palabras) {
            if (lineaActual.length + palabra.length + 1 > anchoMaximo) {
                lineas.add(lineaActual.toString())
                lineaActual = StringBuilder(palabra)
            } else {
                if (lineaActual.isNotEmpty()) lineaActual.append(" ")
                lineaActual.append(palabra)
            }
        }
        if (lineaActual.isNotEmpty()) {
            lineas.add(lineaActual.toString())
        }
        return lineas
    }

    private fun etiquetaDificultad(dificultad: Dificultad): String = when (dificultad) {
        Dificultad.FACIL -> "Fácil"
        Dificultad.MEDIO -> "Medio"
        Dificultad.DIFICIL -> "Difícil"
    }

    private fun formatear(valor: Double): String = String.format(Locale.US, "%.2f", valor)
}
