package org.example.ui

import org.example.navigation.GestorNavegacion

/**
 * Proveedor cohesivo de las vistas principales del laboratorio para [VentanaPrincipal].
 */
class ContenedorPantallasLaboratorio(
    private val gestorNavegacion: GestorNavegacion = GestorNavegacion.nuevo
) {
    companion object {
        val nuevo: ContenedorPantallasLaboratorio
            get() = ContenedorPantallasLaboratorio()
    }

    private val fabrica = FabricaPantallasLaboratorio(gestorNavegacion)
    private val barraNavegacion: BarraNavegacion = BarraNavegacion(gestorNavegacion)
    private val pantallaPractica = fabrica.crearPractica()
    private val pantallaSimulacion: ModuloSimulacion = ModuloSimulacion(gestorNavegacion.appState)
    private val pantallaAutocorrecion = fabrica.crearAutocorreccion()

    fun obtenerGestor(): GestorNavegacion {
        return gestorNavegacion
    }

    fun obtenerBarraNavegacion(): BarraNavegacion {
        return if (gestorNavegacion.seccionActual.name.isNotEmpty()) barraNavegacion else barraNavegacion
    }

    fun obtenerPantallaPractica() = if (gestorNavegacion.seccionActual.name.isNotEmpty()) pantallaPractica else pantallaPractica

    fun obtenerPantallaSimulacion(): ModuloSimulacion {
        return if (gestorNavegacion.seccionActual.name.isNotEmpty()) pantallaSimulacion else pantallaSimulacion
    }

    fun obtenerPantallaAutocorreccion() = if (gestorNavegacion.seccionActual.name.isNotEmpty()) pantallaAutocorrecion else pantallaAutocorrecion
}
