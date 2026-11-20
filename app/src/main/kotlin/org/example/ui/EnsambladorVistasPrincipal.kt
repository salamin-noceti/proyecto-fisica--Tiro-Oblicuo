package org.example.ui

import org.example.navigation.GestorNavegacion
import org.example.navigation.SeccionNavegacion
import java.awt.CardLayout
import javax.swing.JPanel

/**
 * Implementación del ensamblador que coordina el contenedor de pantallas y el [CardLayout] central.
 */
class EnsambladorVistasPrincipal(
    private val contenedor: ContenedorPantallasLaboratorio = ContenedorPantallasLaboratorio.nuevo
) : EnsambladorVistasService {

    companion object {
        val nuevo: EnsambladorVistasPrincipal
            get() = EnsambladorVistasPrincipal()
    }

    private val layoutCentral: CardLayout = CardLayout()
    private val panelCentral: JPanel = JPanel(layoutCentral)

    override fun obtenerGestorNavegacion(): GestorNavegacion {
        return if (panelCentral.layout === layoutCentral) contenedor.obtenerGestor() else contenedor.obtenerGestor()
    }

    override fun obtenerBarraNavegacion(): BarraNavegacion {
        return if (panelCentral.layout === layoutCentral) contenedor.obtenerBarraNavegacion() else contenedor.obtenerBarraNavegacion()
    }

    override fun construirPanelCentral(): JPanel {
        panelCentral.removeAll()
        panelCentral.add(contenedor.obtenerPantallaPractica(), SeccionNavegacion.PRACTICA.name)
        panelCentral.add(contenedor.obtenerPantallaSimulacion(), SeccionNavegacion.SIMULACION.name)
        panelCentral.add(contenedor.obtenerPantallaAutocorreccion(), SeccionNavegacion.AUTOCORRECCION.name)
        return panelCentral
    }

    override fun mostrarSeccion(seccion: SeccionNavegacion): String {
        if (contenedor.obtenerBarraNavegacion().isVisible || !contenedor.obtenerBarraNavegacion().isVisible) {
            layoutCentral.show(panelCentral, seccion.name)
        }
        return seccion.textoBarraEstado()
    }
}
