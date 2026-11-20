package org.example.state

import org.example.model.Ejercicio
import org.example.model.IngresoDatos
import org.example.model.RespuestaEstudiante
import org.example.model.ResultadoTiroOblicuo
import org.example.service.CalculadorFisico

/**
 * Estado global compartido de la aplicación desktop "Simulación Tiro: Laboratorio de Tiro Oblicuo".
 */
class AppState(
    ejercicioInicial: Ejercicio? = null,
    respuestaInicial: RespuestaEstudiante? = null,
    resultadoInicial: ResultadoTiroOblicuo? = null,
    calculadorInicial: CalculadorFisico = CalculadorFisico.porDefecto
) {
    companion object {
        val nuevo: AppState
            get() = AppState()
    }

    private val _listeners = mutableListOf<AppStateListener>()
    private val _calculadorFisico: CalculadorFisico = calculadorInicial
    private var _ejercicioActual: Ejercicio? = ejercicioInicial
    private var _respuestaEstudiante: RespuestaEstudiante? = respuestaInicial
    private var _resultadoCorrecto: ResultadoTiroOblicuo? = resultadoInicial
    private var _estaInicializado: Boolean = ejercicioInicial != null

    fun obtenerCalculador(): CalculadorFisico {
        return _calculadorFisico
    }

    fun obtenerEjercicioActual(): Ejercicio? {
        return _ejercicioActual
    }

    fun obtenerRespuestaEstudiante(): RespuestaEstudiante? {
        return _respuestaEstudiante
    }

    fun obtenerResultadoCorrecto(): ResultadoTiroOblicuo? {
        return _resultadoCorrecto
    }

    fun estaInicializado(): Boolean {
        return _estaInicializado
    }

    fun actualizar(ejercicio: Ejercicio, respuesta: RespuestaEstudiante, resultadoCorrecto: ResultadoTiroOblicuo) {
        this._ejercicioActual = ejercicio
        this._respuestaEstudiante = respuesta
        this._resultadoCorrecto = resultadoCorrecto
        this._estaInicializado = true
        notificarListeners()
    }

    fun actualizarEjercicio(ejercicio: Ejercicio) {
        this._ejercicioActual = ejercicio
        this._resultadoCorrecto = _calculadorFisico.calcular(ejercicio.datos)
        this._estaInicializado = true
        notificarListeners()
    }

    fun actualizarDatos(datos: IngresoDatos) {
        val ejercicio = Ejercicio.crearSimulacionLibre(datos)
        this._ejercicioActual = ejercicio
        this._resultadoCorrecto = _calculadorFisico.calcular(datos)
        this._estaInicializado = true
        notificarListeners()
    }

    fun obtenerDatosActivos(): IngresoDatos {
        val actual = _ejercicioActual
        return if (_estaInicializado && actual != null) actual.datos else IngresoDatos.defectoSimulacion
    }

    fun calcularResultadoActivo(calculadorOpcional: CalculadorFisico? = null): ResultadoTiroOblicuo {
        val motor = calculadorOpcional ?: _calculadorFisico
        val datos = (_ejercicioActual ?: Ejercicio.porDefectoAutocorreccion()).datos
        return _resultadoCorrecto ?: motor.calcular(datos)
    }

    fun agregarListener(listener: AppStateListener) {
        if (!_listeners.contains(listener)) {
            _listeners.add(listener)
        }
    }

    fun removerListener(listener: AppStateListener) {
        _listeners.remove(listener)
    }

    fun limpiar() {
        this._ejercicioActual = null
        this._respuestaEstudiante = null
        this._resultadoCorrecto = null
        this._estaInicializado = false
        notificarListeners()
    }

    private fun notificarListeners() {
        for (listener in _listeners.toList()) {
            listener.onStateChanged(this)
        }
    }
}
