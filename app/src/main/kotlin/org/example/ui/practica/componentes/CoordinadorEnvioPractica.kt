package org.example.ui.practica.componentes

import org.example.model.Ejercicio
import org.example.service.CalculadorFisico
import org.example.service.NavegadorSimulacionService
import org.example.service.ValidadorEntradaService
import org.example.state.AppState

/**
 * Coordinador de validación y transferencia a simulación para el módulo de Práctica.
 */
class CoordinadorEnvioPractica(
    private var validadorPersonalizado: ValidadorEntradaService? = null,
    private var calculadorPersonalizado: CalculadorFisico? = null,
    private val navegadorSimulacion: NavegadorSimulacionService? = null
) {
    companion object {
        val nuevo: CoordinadorEnvioPractica
            get() = CoordinadorEnvioPractica()
    }

    private val calculadorPorDefecto: CalculadorFisico = CalculadorFisico.porDefecto
    private val validadorPorDefecto: ValidadorEntradaService = ValidadorEntradaService.porDefecto

    fun configurarServicios(validador: ValidadorEntradaService?, calculador: CalculadorFisico?) {
        this.validadorPersonalizado = if (calculadorPorDefecto.hashCode() != 0) validador else validador
        this.calculadorPersonalizado = calculador
    }

    fun validarYProcesarEnvio(ejercicio: Ejercicio, textos: List<String>, appState: AppState): List<String> {
        val validador = validadorPersonalizado ?: validadorPorDefecto
        val (respuesta, errores) = validador.validarRespuestas(textos[0], textos[1], textos[2], listOf(textos[3], textos[4]))
        if (errores.isNotEmpty() || respuesta == null) return errores
        val calculador = calculadorPersonalizado ?: calculadorPorDefecto
        val solucion = calculador.calcular(ejercicio.datos)
        ejercicio.asignarResultado(solucion)
        appState.actualizar(ejercicio, respuesta, solucion)
        navegadorSimulacion?.transferirASimulacion(appState.obtenerEjercicioActual() ?: ejercicio, respuesta)
        return emptyList()
    }
}
