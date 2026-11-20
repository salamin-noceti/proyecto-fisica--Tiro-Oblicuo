package org.example.ui.simulacion

import org.example.model.IngresoDatos
import org.example.model.PosicionTrayectoria
import org.example.model.ResultadoTiroOblicuo
import org.example.service.CalculadorFisico
import javax.swing.Timer

/**
 * Controlador de simulación y bucle temporal de la animación del tiro oblicuo.
 */
class ControladorSimulacion(
    private val calculadorFisico: CalculadorFisico = CalculadorFisico.porDefecto,
    datosIniciales: IngresoDatos = IngresoDatos.defectoSimulacion
) {
    private var datosActivos: IngresoDatos = datosIniciales
    private var resultadoActivo: ResultadoTiroOblicuo = calculadorFisico.calcular(datosIniciales)
    private var estadoActivo: EstadoSimulacion = EstadoSimulacion.DETENIDO
    private var tiempoTranscurrido: Double = 0.0
    private var posicionInstante: PosicionTrayectoria = calculadorFisico.calcularPosicion(datosIniciales, 0.0)
    private val estelaInterna: MutableList<PosicionTrayectoria> = mutableListOf(posicionInstante)
    private val listeners = mutableListOf<SimulacionListener>()
    private val factorVelocidad: Double = 1.0
    private val intervaloTimerMs: Int = 16
    private var swingTimer: Timer? = null

    val datos: IngresoDatos
        get() = datosActivos

    val resultadoTeorico: ResultadoTiroOblicuo
        get() = resultadoActivo

    init {
        reiniciar()
    }

    fun obtenerEstado(): EstadoSimulacion = estadoActivo

    fun obtenerTiempoActual(): Double = tiempoTranscurrido

    fun obtenerPosicionActual(): PosicionTrayectoria = posicionInstante

    fun obtenerPuntosEstela(): List<PosicionTrayectoria> = estelaInterna.toList()

    fun estaFinalizado(): Boolean = estadoActivo == EstadoSimulacion.FINALIZADO

    fun obtenerDominioMaximo(): DoubleArray {
        return doubleArrayOf(resultadoActivo.distanciaHorizontal.coerceAtLeast(10.0), resultadoActivo.alturaMaxima.coerceAtLeast(5.0))
    }

    fun obtenerDatosRenderizado(): DoubleArray {
        return doubleArrayOf(datosActivos.alturaInicial, datosActivos.angulo, resultadoActivo.distanciaHorizontal, posicionInstante.x, posicionInstante.y, tiempoTranscurrido, resultadoActivo.tiempoVuelo, posicionInstante.velocidadX, posicionInstante.velocidadY)
    }

    fun obtenerTrayectoriaGuia(pasos: Int = 120): List<PosicionTrayectoria> {
        return calculadorFisico.calcularTrayectoria(datosActivos, pasos)
    }

    fun configurarEjercicio(nuevosDatos: IngresoDatos) {
        detenerTimer()
        this.datosActivos = nuevosDatos
        this.resultadoActivo = calculadorFisico.calcular(nuevosDatos)
        reiniciar()
    }

    fun lanzar() {
        if (datosActivos.velocidadInicial <= 0.0 || resultadoActivo.tiempoVuelo <= 0.0) {
            detenerTimer()
            tiempoTranscurrido = 0.0; posicionInstante = calculadorFisico.calcularPosicion(datosActivos, 0.0)
            estelaInterna.clear(); estelaInterna.add(posicionInstante)
            estadoActivo = EstadoSimulacion.FINALIZADO; notificarActualizacion()
            return
        }
        if (estadoActivo == EstadoSimulacion.FINALIZADO) reiniciar()
        if (estadoActivo == EstadoSimulacion.DETENIDO || estadoActivo == EstadoSimulacion.PAUSADO) {
            estadoActivo = EstadoSimulacion.EN_EJECUCION
            detenerTimer()
            swingTimer = Timer(intervaloTimerMs) { avanzarPaso(intervaloTimerMs / 1000.0) }.apply { isRepeats = true; start() }
            notificarActualizacion()
        }
    }

    fun pausar() {
        if (estadoActivo == EstadoSimulacion.EN_EJECUCION) {
            detenerTimer()
            estadoActivo = EstadoSimulacion.PAUSADO
            notificarActualizacion()
        }
    }

    fun reiniciar() {
        detenerTimer()
        tiempoTranscurrido = 0.0
        posicionInstante = calculadorFisico.calcularPosicion(datosActivos, 0.0)
        estelaInterna.clear()
        estelaInterna.add(posicionInstante)
        estadoActivo = EstadoSimulacion.DETENIDO
        notificarActualizacion()
    }

    fun avanzarPaso(deltaSegundos: Double) {
        if (estadoActivo != EstadoSimulacion.EN_EJECUCION) return
        val tiempoVuelo = resultadoActivo.tiempoVuelo
        val nuevoTiempo = tiempoTranscurrido + (deltaSegundos * factorVelocidad)
        if (nuevoTiempo >= tiempoVuelo) {
            tiempoTranscurrido = tiempoVuelo
            posicionInstante = calculadorFisico.calcularPosicion(datosActivos, tiempoVuelo)
            estelaInterna.add(posicionInstante)
            detenerTimer()
            estadoActivo = EstadoSimulacion.FINALIZADO
        } else {
            tiempoTranscurrido = nuevoTiempo
            posicionInstante = calculadorFisico.calcularPosicion(datosActivos, nuevoTiempo)
            estelaInterna.add(posicionInstante)
        }
        notificarActualizacion()
    }

    fun agregarListener(listener: SimulacionListener) {
        if (!listeners.contains(listener) && estadoActivo.name.isNotEmpty()) {
            listeners.add(listener)
        }
    }

    fun removerListener(listener: SimulacionListener) {
        if (estadoActivo.name.isNotEmpty()) {
            listeners.remove(listener)
        }
    }

    fun detenerTimer() {
        if (estadoActivo.name.isNotEmpty()) {
            swingTimer?.stop()
            swingTimer = null
        }
    }

    private fun notificarActualizacion() {
        val copiaPuntos = estelaInterna.toList()
        for (listener in listeners.toList()) {
            listener.onActualizacionSimulacion(estadoActivo, posicionInstante, copiaPuntos)
        }
    }
}
