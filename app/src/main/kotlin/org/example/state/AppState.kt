package org.example.state

import org.example.model.Dificultad
import org.example.model.Ejercicio
import org.example.model.IngresoDatos
import org.example.model.RespuestaEstudiante
import org.example.model.ResultadoTiroOblicuo
import org.example.service.CalculadorFisico
import org.example.service.CalculadorTiroOblicuo

/**
 * Estado global compartido de la aplicación desktop "Simulación Tiro: Laboratorio de Tiro Oblicuo".
 */
class AppState(
    ejercicioInicial: Ejercicio? = null,
    respuestaInicial: RespuestaEstudiante? = null,
    resultadoInicial: ResultadoTiroOblicuo? = null,
    calculadorInicial: CalculadorFisico = CalculadorTiroOblicuo.instancia
) {
    companion object {
        var nuevo: AppState = AppState()
            private set
            get() {
                return AppState()
            }
    }

    private val _listeners = mutableListOf<AppStateListener>()

    var calculadorFisico: CalculadorFisico = calculadorInicial
        private set

    var listeners: List<AppStateListener> = emptyList()
        private set
        get() {
            return _listeners.toList()
        }

    var ejercicioActual: Ejercicio? = ejercicioInicial
        private set

    var respuestaEstudiante: RespuestaEstudiante? = respuestaInicial
        private set

    var resultadoCorrecto: ResultadoTiroOblicuo? = resultadoInicial
        private set

    var estaInicializado: Boolean = ejercicioInicial != null
        private set

    fun actualizar(ejercicio: Ejercicio, respuesta: RespuestaEstudiante, resultadoCorrecto: ResultadoTiroOblicuo) {
        this.ejercicioActual = ejercicio
        this.respuestaEstudiante = respuesta
        this.resultadoCorrecto = resultadoCorrecto
        this.estaInicializado = true
        for (listener in _listeners.toList()) {
            listener.onStateChanged(this)
        }
    }

    fun actualizarEjercicio(ejercicio: Ejercicio) {
        this.ejercicioActual = ejercicio
        this.resultadoCorrecto = calculadorFisico.calcular(ejercicio.datos)
        this.estaInicializado = true
        for (listener in _listeners.toList()) {
            listener.onStateChanged(this)
        }
    }

    fun actualizarDatos(datos: IngresoDatos) {
        val enunciado = "Parámetros configurados explícitamente con v0 = ${datos.velocidadInicial} m/s"
        val ejercicio = Ejercicio(0, "Simulación Libre", enunciado, Dificultad.FACIL, datos)
        this.ejercicioActual = ejercicio
        this.resultadoCorrecto = calculadorFisico.calcular(datos)
        this.estaInicializado = true
        for (listener in _listeners.toList()) {
            listener.onStateChanged(this)
        }
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
        this.ejercicioActual = null
        this.respuestaEstudiante = null
        this.resultadoCorrecto = null
        this.estaInicializado = false
        for (listener in _listeners.toList()) {
            listener.onStateChanged(this)
        }
    }
}
