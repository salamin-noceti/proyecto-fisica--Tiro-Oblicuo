package org.example.ui

import org.example.navigation.GestorNavegacion
import org.example.service.GeneradorEjerciciosBancoAdapter
import org.example.service.GeneradorEjerciciosService
import org.example.service.NavegadorSimulacionService
import org.example.service.SimulacionConsoleRunner
import org.example.service.ValidadorEntradaRespuesta
import org.example.state.AppState

/**
 * Implementación gráfica en Swing para la Pantalla de Ejercicios (Issue 6).
 *
 * Mantiene compatibilidad total con la suite de pruebas heredando directamente de [PantallaPractica]
 * para evitar la duplicación de código de diseño e interacción (DUP -> 0%).
 *
 * @param generadorService Abstracción del servicio proveedor de ejercicios (DIP).
 * @param navegadorSimulacion Servicio de navegación o transferencia hacia la simulación.
 * @param validador Validador de respuestas numéricas del estudiante.
 */
class PantallaEjerciciosSwing(
    generadorService: GeneradorEjerciciosService = GeneradorEjerciciosBancoAdapter(),
    navegadorSimulacion: NavegadorSimulacionService = SimulacionConsoleRunner(),
    validador: ValidadorEntradaRespuesta = ValidadorEntradaRespuesta()
) : PantallaPractica(
    gestorNavegacion = GestorNavegacion(),
    appState = AppState(),
    generadorService = generadorService,
    navegadorSimulacion = navegadorSimulacion,
    validador = validador
)
