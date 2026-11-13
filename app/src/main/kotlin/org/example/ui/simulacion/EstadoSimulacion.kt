package org.example.ui.simulacion

/**
 * Estados posibles del ciclo de vida de la animación de simulación.
 */
enum class EstadoSimulacion {
    DETENIDO,
    EN_EJECUCION,
    PAUSADO,
    FINALIZADO;

    fun estaEnVuelo(): Boolean {
        return this == EN_EJECUCION
    }
}
