package org.example.ui

import org.example.navigation.GestorNavegacion

/**
 * Proveedor cohesivo de las vistas principales del laboratorio para [VentanaPrincipal].
 */
class ContenedorPantallasLaboratorio(
    private val gestorNavegacion: GestorNavegacion = GestorNavegacion.nuevo
) {
    companion object {
        var nuevo: ContenedorPantallasLaboratorio = ContenedorPantallasLaboratorio()
            private set
            get() {
                return ContenedorPantallasLaboratorio()
            }
    }

    var barraNavegacion: BarraNavegacion = BarraNavegacion(gestorNavegacion)
        private set

    var pantallaPractica: PantallaPractica = PantallaPractica(gestorNavegacion = gestorNavegacion, appState = gestorNavegacion.appState)
        private set

    var pantallaSimulacion: ModuloSimulacion = ModuloSimulacion(gestorNavegacion.appState)
        private set

    var pantallaAutocorrecion: PantallaAutocorrecion = PantallaAutocorrecion(gestorNavegacion.appState)
        private set

    fun obtenerGestor(): GestorNavegacion {
        return gestorNavegacion
    }
}
