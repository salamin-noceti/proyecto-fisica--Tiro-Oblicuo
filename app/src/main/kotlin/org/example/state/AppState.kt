package org.example.state

import org.example.model.Dificultad
import org.example.model.Ejercicio
import org.example.model.IngresoDatos
import org.example.model.RespuestaEstudiante
import org.example.model.ResultadoTiroOblicuo
import org.example.service.CalculadorTiroOblicuo

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

    /**
     * Distingue formalmente entre un estado no inicializado (`null`) y un ejercicio
     * explícitamente configurado (incluso si sus parámetros numéricos son v0 = 0.0).
     */
    val estaInicializado: Boolean
        get() = ejercicioActual != null

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
     * Configura o actualiza directamente el ejercicio activo calculando su solución teórica,
     * garantizando que valores como v0 = 0.0 no caigan en estados nulos ni generen fallbacks.
     *
     * @param ejercicio Instancia del ejercicio a activar.
     */
    fun actualizarEjercicio(ejercicio: Ejercicio) {
        val calculador = CalculadorTiroOblicuo()
        val resultado = calculador.calcular(ejercicio.datos)
        this.ejercicioActual = ejercicio
        this.resultadoCorrecto = resultado
        notificarCambios()
    }

    /**
     * Configura parámetros físicos explícitos en el estado compartido sin aplicar fallback a datos por defecto.
     *
     * @param datos Condiciones físicas (v0, ángulo, y0).
     */
    fun actualizarDatos(datos: IngresoDatos) {
        val ejercicio = Ejercicio(
            id = 0,
            titulo = "Simulación Libre",
            enunciado = "Parámetros configurados explícitamente con v0 = ${datos.velocidadInicial} m/s",
            dificultad = Dificultad.FACIL,
            datos = datos
        )
        actualizarEjercicio(ejercicio)
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
