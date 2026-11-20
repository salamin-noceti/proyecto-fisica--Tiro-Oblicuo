package org.example.ui

import org.example.navigation.GestorNavegacion
import org.example.ui.autocorrecion.CoordinadorAutocorreccion
import org.example.ui.practica.componentes.CoordinadorPractica

/**
 * Fábrica cohesiva para la instanciación de las pantallas de Práctica y Autocorrección.
 */
class FabricaPantallasLaboratorio(
    private val gestorNavegacion: GestorNavegacion
) {
    fun crearPractica(): PantallaPractica {
        return PantallaPractica(CoordinadorPractica(gestorNavegacion, gestorNavegacion.appState))
    }

    fun crearAutocorreccion(): PantallaAutocorrecion {
        return PantallaAutocorrecion(CoordinadorAutocorreccion(gestorNavegacion.appState))
    }
}
