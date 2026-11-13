package org.example.ui.simulacion

import org.example.model.PosicionTrayectoria

/**
 * Oyente de eventos de actualización emitidos por [ControladorSimulacion].
 */
fun interface SimulacionListener {
    fun onActualizacionSimulacion(estado: EstadoSimulacion, posicion: PosicionTrayectoria, puntosEstela: List<PosicionTrayectoria>)
}
