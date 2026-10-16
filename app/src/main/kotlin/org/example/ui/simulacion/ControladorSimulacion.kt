package org.example.ui.simulacion

import org.example.model.IngresoDatos
import org.example.model.PosicionTrayectoria
import org.example.model.ResultadoTiroOblicuo
import org.example.service.CalculadorFisico
import org.example.service.CalculadorTiroOblicuo
import javax.swing.Timer

/**
 * Estados posibles del ciclo de vida de la animación de simulación.
 */
enum class EstadoSimulacion {
    DETENIDO,
    EN_EJECUCION,
    PAUSADO,
    FINALIZADO
}

/**
 * Oyente de eventos de actualización emitidos por [ControladorSimulacion].
 */
fun interface SimulacionListener {
    /**
     * Invocado cada vez que el tiempo, la posición del proyectil o el estado de simulación cambian.
     *
     * @param estado Estado actual del ciclo de animación.
     * @param posicion Posición y velocidad instantánea del proyectil.
     * @param puntosEstela Lista de puntos recorridos hasta el momento para renderizar la estela.
     */
    fun onActualizacionSimulacion(
        estado: EstadoSimulacion,
        posicion: PosicionTrayectoria,
        puntosEstela: List<PosicionTrayectoria>
    )
}

/**
 * Controlador de simulación y bucle temporal de la animación del tiro oblicuo.
 *
 * Cumple con el Principio de Responsabilidad Única (SRP) aislando la lógica de temporización,
 * avance cronológico, estados de control ("Lanzar", "Pausar", "Reiniciar") y detención
 * al impacto del proyectil.
 *
 * Cumple con el Principio de Inversión de Dependencias (DIP) al depender de la abstracción
 * [CalculadorFisico] para la obtención de datos analíticos.
 *
 * @param calculadorFisico Servicio físico analítico inyectado.
 * @param datosIniciales Condiciones iniciales físicas del problema.
 */
class ControladorSimulacion(
    val calculadorFisico: CalculadorFisico = CalculadorTiroOblicuo(),
    datosIniciales: IngresoDatos = IngresoDatos(velocidadInicial = 25.0, angulo = 45.0, alturaInicial = 0.0)
) {

    /**
     * Parámetros físicos configurados actualmente para la simulación.
     */
    var datos: IngresoDatos = datosIniciales
        private set

    /**
     * Solución teórica calculada por el motor físico.
     */
    var resultadoTeorico: ResultadoTiroOblicuo = calculadorFisico.calcular(datosIniciales)
        private set

    /**
     * Estado actual del bucle de animación.
     */
    var estado: EstadoSimulacion = EstadoSimulacion.DETENIDO
        private set

    /**
     * Tiempo cronológico simulado transcurrido en segundos desde el inicio del lanzamiento.
     */
    var tiempoActual: Double = 0.0
        private set

    /**
     * Posición y velocidad física instantánea actual del proyectil.
     */
    var posicionActual: PosicionTrayectoria = calculadorFisico.calcularPosicion(datosIniciales, 0.0)
        private set

    /**
     * Historial de puntos físicos recorridos por el proyectil durante el vuelo actual,
     * utilizado para renderizar la estela / curva de la trayectoria.
     */
    val puntosEstela: MutableList<PosicionTrayectoria> = mutableListOf(posicionActual)

    /**
     * Factor de escala temporal de la animación (1.0 = tiempo real).
     */
    var factorVelocidad: Double = 1.0

    private val listeners = mutableListOf<SimulacionListener>()

    // Intervalo de actualización en milisegundos (~60 FPS)
    private val intervaloTimerMs = 16
    private var swingTimer: Timer? = null

    init {
        reiniciar()
    }

    /**
     * Configura nuevos parámetros físicos del ejercicio (v0, ángulo, altura) y reinicia la simulación.
     *
     * @param nuevosDatos Nuevos datos de entrada del tiro oblicuo.
     */
    fun configurarEjercicio(nuevosDatos: IngresoDatos) {
        detenerTimer()
        this.datos = nuevosDatos
        this.resultadoTeorico = calculadorFisico.calcular(nuevosDatos)
        reiniciar()
    }

    /**
     * Inicia o reanuda la animación del lanzamiento.
     * Si la animación ya había finalizado por impacto en el suelo, se reinicia automáticamente antes de disparar.
     */
    fun lanzar() {
        if (estado == EstadoSimulacion.FINALIZADO) {
            reiniciar()
        }

        if (estado == EstadoSimulacion.DETENIDO || estado == EstadoSimulacion.PAUSADO) {
            estado = EstadoSimulacion.EN_EJECUCION
            iniciarTimer()
            notificarActualizacion()
        }
    }

    /**
     * Congela momentáneamente el objeto en su posición actual.
     */
    fun pausar() {
        if (estado == EstadoSimulacion.EN_EJECUCION) {
            detenerTimer()
            estado = EstadoSimulacion.PAUSADO
            notificarActualizacion()
        }
    }

    /**
     * Regresa el proyectil y el cronómetro al estado inicial (t = 0), limpiando la trayectoria dibujada.
     */
    fun reiniciar() {
        detenerTimer()
        tiempoActual = 0.0
        posicionActual = calculadorFisico.calcularPosicion(datos, 0.0)
        puntosEstela.clear()
        puntosEstela.add(posicionActual)
        estado = EstadoSimulacion.DETENIDO
        notificarActualizacion()
    }

    /**
     * Avanza el tiempo de simulación un paso delta de segundos especificado.
     * Si el tiempo alcanza o supera el tiempo de vuelo, ubica al proyectil en el suelo
     * y finaliza automáticamente la animación.
     *
     * @param deltaSegundos Cantidad de segundos físicos a avanzar.
     */
    fun avanzarPaso(deltaSegundos: Double) {
        if (estado != EstadoSimulacion.EN_EJECUCION) {
            return
        }

        val tiempoVuelo = resultadoTeorico.tiempoVuelo
        val nuevoTiempo = tiempoActual + (deltaSegundos * factorVelocidad)

        if (nuevoTiempo >= tiempoVuelo) {
            // Impacto en el suelo alcanzado: t = t_vuelo, y = 0
            tiempoActual = tiempoVuelo
            posicionActual = calculadorFisico.calcularPosicion(datos, tiempoVuelo)
            puntosEstela.add(posicionActual)
            detenerTimer()
            estado = EstadoSimulacion.FINALIZADO
        } else {
            tiempoActual = nuevoTiempo
            posicionActual = calculadorFisico.calcularPosicion(datos, nuevoTiempo)
            puntosEstela.add(posicionActual)
        }

        notificarActualizacion()
    }

    /**
     * Registra un observador [SimulacionListener].
     */
    fun agregarListener(listener: SimulacionListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    /**
     * Remueve un observador [SimulacionListener].
     */
    fun removerListener(listener: SimulacionListener) {
        listeners.remove(listener)
    }

    /**
     * Detiene el timer de Swing liberando recursos.
     */
    fun detenerTimer() {
        swingTimer?.stop()
        swingTimer = null
    }

    private fun iniciarTimer() {
        detenerTimer()
        val dtSegundos = intervaloTimerMs / 1000.0
        swingTimer = Timer(intervaloTimerMs) {
            avanzarPaso(dtSegundos)
        }.apply {
            isRepeats = true
            start()
        }
    }

    private fun notificarActualizacion() {
        val copiaPuntos = puntosEstela.toList()
        for (listener in listeners.toList()) {
            listener.onActualizacionSimulacion(estado, posicionActual, copiaPuntos)
        }
    }
}
