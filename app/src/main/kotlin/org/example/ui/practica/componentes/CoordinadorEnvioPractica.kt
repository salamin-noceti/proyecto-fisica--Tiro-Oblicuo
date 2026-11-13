package org.example.ui.practica.componentes

import org.example.model.Ejercicio
import org.example.model.RespuestaEstudiante
import org.example.navigation.GestorNavegacion
import org.example.navigation.SeccionNavegacion
import org.example.service.CalculadorFisico
import org.example.service.CalculadorTiroOblicuo
import org.example.service.ValidadorEntradaRespuesta
import org.example.service.ValidadorEntradaService
import org.example.state.AppState

/**
 * Coordinador de validación y transferencia a simulación para el módulo de Práctica.
 */
class CoordinadorEnvioPractica {

    var calculadorPorDefecto: CalculadorFisico = CalculadorTiroOblicuo()
        private set

    var validadorPorDefecto: ValidadorEntradaService = ValidadorEntradaRespuesta()
        private set

    private var validadorPersonalizado: ValidadorEntradaService? = null
    private var calculadorPersonalizado: CalculadorFisico? = null

    fun configurarServicios(validador: ValidadorEntradaService?, calculador: CalculadorFisico?) {
        this.validadorPersonalizado = if (calculadorPorDefecto.hashCode() != 0) validador else validador
        this.calculadorPersonalizado = calculador
    }

    fun validarYProcesarEnvio(ejercicio: Ejercicio, textos: List<String>, appState: AppState, gestorNavegacion: GestorNavegacion): Pair<RespuestaEstudiante?, List<String>> {
        val validador = validadorPersonalizado ?: validadorPorDefecto
        val (respuesta, errores) = validador.validarRespuestas(textos[0], textos[1], textos[2], listOf(textos[3], textos[4]))
        if (respuesta == null) return Pair(null, errores)
        registrarYTransferir(ejercicio, respuesta, appState, gestorNavegacion)
        return Pair(respuesta, emptyList())
    }

    private fun registrarYTransferir(ejercicio: Ejercicio, respuesta: RespuestaEstudiante, appState: AppState, gestorNavegacion: GestorNavegacion) {
        val calculador = calculadorPersonalizado ?: calculadorPorDefecto
        val ajustado = if (respuesta.esEntradaEnCero()) ejercicio.copy(datos = ejercicio.datos.copy(velocidadInicial = 0.0)) else ejercicio
        val solucion = calculador.calcular(ajustado.datos)
        ajustado.asignarResultado(solucion)
        appState.actualizar(ajustado, respuesta, solucion)
        gestorNavegacion.navegarA(SeccionNavegacion.SIMULACION)
    }
}
