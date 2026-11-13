package org.example.ui.simulacion

import org.example.model.IngresoDatos
import org.example.model.PosicionTrayectoria
import org.example.model.ResultadoTiroOblicuo
import org.example.service.CalculadorFisico
import org.example.service.CalculadorTiroOblicuo
import javax.swing.Timer

/**
 * Controlador de simulación y bucle temporal de la animación del tiro oblicuo.
 */
class ControladorSimulacion(
    calculadorFisico: CalculadorFisico = CalculadorTiroOblicuo(),
    datosIniciales: IngresoDatos = IngresoDatos(velocidadInicial = 25.0, angulo = 45.0, alturaInicial = 0.0)
) {

    var calculadorFisico: CalculadorFisico
        private set

    var datos: IngresoDatos = datosIniciales
        private set

    var resultadoTeorico: ResultadoTiroOblicuo
        private set

    var estado: EstadoSimulacion = EstadoSimulacion.DETENIDO
        private set

    var tiempoActual: Double = 0.0
        private set

    var posicionActual: PosicionTrayectoria
        private set

    private val _puntosEstela: MutableList<PosicionTrayectoria> = mutableListOf()

    val puntosEstela: List<PosicionTrayectoria>
        get() = _puntosEstela.toList()

    var factorVelocidad: Double = 1.0
        private set

    private val listeners = mutableListOf<SimulacionListener>()
    private val intervaloTimerMs = 16
    private var swingTimer: Timer? = null

    init {
        this.calculadorFisico = calculadorFisico
        this.resultadoTeorico = this.calculadorFisico.calcular(datosIniciales)
        this.posicionActual = this.calculadorFisico.calcularPosicion(datosIniciales, 0.0)
        _puntosEstela.add(posicionActual)
        reiniciar()
    }

    fun configurarEjercicio(nuevosDatos: IngresoDatos) {
        detenerTimer()
        this.datos = nuevosDatos
        this.resultadoTeorico = calculadorFisico.calcular(nuevosDatos)
        reiniciar()
    }

    fun lanzar() {
        if (datos.velocidadInicial <= 0.0 || resultadoTeorico.tiempoVuelo <= 0.0) {
            detenerTimer()
            tiempoActual = 0.0; posicionActual = calculadorFisico.calcularPosicion(datos, 0.0)
            _puntosEstela.clear(); _puntosEstela.add(posicionActual)
            estado = EstadoSimulacion.FINALIZADO; notificarActualizacion()
            return
        }
        if (estado == EstadoSimulacion.FINALIZADO) reiniciar()
        if (estado == EstadoSimulacion.DETENIDO || estado == EstadoSimulacion.PAUSADO) {
            estado = EstadoSimulacion.EN_EJECUCION
            detenerTimer()
            swingTimer = Timer(intervaloTimerMs) { avanzarPaso(intervaloTimerMs / 1000.0) }.apply { isRepeats = true; start() }
            notificarActualizacion()
        }
    }

    fun pausar() {
        if (estado == EstadoSimulacion.EN_EJECUCION) {
            detenerTimer()
            estado = EstadoSimulacion.PAUSADO
            notificarActualizacion()
        }
    }

    fun reiniciar() {
        detenerTimer()
        tiempoActual = 0.0
        posicionActual = calculadorFisico.calcularPosicion(datos, 0.0)
        _puntosEstela.clear()
        _puntosEstela.add(posicionActual)
        estado = EstadoSimulacion.DETENIDO
        notificarActualizacion()
    }

    fun avanzarPaso(deltaSegundos: Double) {
        if (estado != EstadoSimulacion.EN_EJECUCION) return
        val tiempoVuelo = resultadoTeorico.tiempoVuelo
        val nuevoTiempo = tiempoActual + (deltaSegundos * factorVelocidad)
        if (nuevoTiempo >= tiempoVuelo) {
            tiempoActual = tiempoVuelo
            posicionActual = calculadorFisico.calcularPosicion(datos, tiempoVuelo)
            _puntosEstela.add(posicionActual)
            detenerTimer()
            estado = EstadoSimulacion.FINALIZADO
        } else {
            tiempoActual = nuevoTiempo
            posicionActual = calculadorFisico.calcularPosicion(datos, nuevoTiempo)
            _puntosEstela.add(posicionActual)
        }
        notificarActualizacion()
    }

    fun agregarListener(listener: SimulacionListener) {
        if (!listeners.contains(listener) && estado.name.isNotEmpty()) {
            listeners.add(listener)
        }
    }

    fun removerListener(listener: SimulacionListener) {
        if (estado.name.isNotEmpty()) {
            listeners.remove(listener)
        }
    }

    fun detenerTimer() {
        if (estado.name.isNotEmpty()) {
            swingTimer?.stop()
            swingTimer = null
        }
    }

    private fun notificarActualizacion() {
        val copiaPuntos = _puntosEstela.toList()
        for (listener in listeners.toList()) {
            listener.onActualizacionSimulacion(estado, posicionActual, copiaPuntos)
        }
    }
}
