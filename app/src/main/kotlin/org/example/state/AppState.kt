package org.example.state

import org.example.model.Ejercicio
import org.example.model.RespuestaEstudiante
import org.example.model.ResultadoTiroOblicuo

/**
 * Interfaz funcional para observar cambios en el estado compartido de la aplicación.
 */
fun interface AppStateListener {
    /**
     * Invocado cuando el estado compartido es actualizado.
     *
     * @param appState Instancia del estado con los datos actualizados.
     */
    fun onStateChanged(appState: AppState)
}

/**
 * Estado global compartido de la aplicación desktop "Simulación Tiro: Laboratorio de Tiro Oblicuo".
 *
 * Mantiene la información central compartida entre los distintos módulos:
 * - El ejercicio seleccionado actualmente ([Ejercicio]).
 * - Las respuestas numéricas calculadas e ingresadas por el estudiante ([RespuestaEstudiante]).
 * - La solución analítica exacta calculada teóricamente por el sistema ([ResultadoTiroOblicuo]).
 *
 * Permite suscribir oyentes ([AppStateListener]) para sincronizar de manera desacoplada
 * los componentes gráficos (Práctica, Simulación, Autocorrección).
 *
 * @param ejercicioInicial Ejercicio cargado inicialmente (nulo por defecto).
 * @param respuestaInicial Respuestas iniciales del estudiante (nulo por defecto).
 * @param resultadoInicial Resultado teórico del sistema (nulo por defecto).
 */
class AppState(
    ejercicioInicial: Ejercicio? = null,
    respuestaInicial: RespuestaEstudiante? = null,
    resultadoInicial: ResultadoTiroOblicuo? = null
) {
    /**
     * Ejercicio seleccionado actualmente del banco de ejercicios.
     */
    var ejercicioActual: Ejercicio? = ejercicioInicial
        private set

    /**
     * Respuestas numéricas calculadas y cargadas por el estudiante.
     */
    var respuestaEstudiante: RespuestaEstudiante? = respuestaInicial
        private set

    /**
     * Resultados físicos correctos calculados por el sistema teórico.
     */
    var resultadoCorrecto: ResultadoTiroOblicuo? = resultadoInicial
        private set

    /**
     * Alias de acceso directo para [resultadoCorrecto] según convención de nombres.
     */
    val resultadoCalculado: ResultadoTiroOblicuo?
        get() = resultadoCorrecto

    private val listeners = mutableListOf<AppStateListener>()

    /**
     * Actualiza el estado compartido con los datos del ejercicio resuelto y
     * notifica a todos los oyentes registrados.
     *
     * @param ejercicio Ejercicio seleccionado y resuelto.
     * @param respuesta Valores numéricos cargados por el estudiante.
     * @param resultadoCorrecto Solución teórica exacta calculada por el sistema.
     */
    fun actualizar(
        ejercicio: Ejercicio,
        respuesta: RespuestaEstudiante,
        resultadoCorrecto: ResultadoTiroOblicuo
    ) {
        this.ejercicioActual = ejercicio
        this.respuestaEstudiante = respuesta
        this.resultadoCorrecto = resultadoCorrecto
        notificarCambios()
    }

    /**
     * Registra un nuevo escuchador [AppStateListener].
     *
     * @param listener El oyente a agregar.
     */
    fun agregarListener(listener: AppStateListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    /**
     * Remueve un escuchador previamente registrado.
     *
     * @param listener El oyente a remover.
     */
    fun removerListener(listener: AppStateListener) {
        listeners.remove(listener)
    }

    /**
     * Restablece el estado eliminando el ejercicio y respuestas activas.
     */
    fun limpiar() {
        this.ejercicioActual = null
        this.respuestaEstudiante = null
        this.resultadoCorrecto = null
        notificarCambios()
    }

    /**
     * Notifica a todos los escuchadores de los cambios aplicados.
     */
    private fun notificarCambios() {
        for (listener in listeners.toList()) {
            listener.onStateChanged(this)
        }
    }

    companion object {
        /**
         * Instancia compartida por defecto si se requiere acceso global.
         */
        val compartido: AppState = AppState()
    }
}
