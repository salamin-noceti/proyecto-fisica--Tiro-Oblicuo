package org.example.ui.practica.componentes

import org.example.ui.PantallaPractica
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DialogoAdvertenciaFormatoTest {

    private fun crearPractica(): PantallaPractica {
        return PantallaPractica()
    }

    @Test
    fun testAperturaInicialAlEntrarYCierreManualConBotonEntendido() {
        val pantalla = crearPractica()
        val dialogo = pantalla.dialogoAdvertencia
        assertTrue(dialogo.mostrarDialogo, "El diálogo debe estar visible inicialmente al entrar")
        assertFalse(dialogo.campoConErrorDecimal)
        assertTrue(dialogo.labelTitulo.text.contains("Advertencia de Formato y Tolerancia"))
        assertTrue(dialogo.labelContenido.text.contains("• Formato de decimales: Ingresa como máximo 2 números después de la coma (ejemplo: 15.46 o 15,46)."))
        assertTrue(dialogo.labelContenido.text.contains("• Margen de tolerancia: Los ejercicios cuentan con un umbral de error del ±5% para absorber pequeñas variaciones de redondeo."))
        assertEquals("Entendido", dialogo.botonEntendido.text)

        val panelDialogo = dialogo.construirPanelDialogo()
        assertTrue(panelDialogo.preferredSize.width >= dialogo.labelContenido.preferredSize.width)
        dialogo.botonEntendido.doClick()
        assertFalse(dialogo.mostrarDialogo, "El diálogo debe ocultarse tras presionar Entendido")
    }

    @Test
    fun testDeteccionDeMasDe2DecimalesYControlDeRedisparoTrasCorreccion() {
        val pantalla = crearPractica()
        val dialogo = pantalla.dialogoAdvertencia
        dialogo.botonEntendido.doClick()
        assertFalse(dialogo.mostrarDialogo)

        pantalla.campoAlcance.text = "15.46"; pantalla.campoTiempo.text = "2,04"
        assertFalse(dialogo.mostrarDialogo || dialogo.campoConErrorDecimal)

        pantalla.campoAlcance.text = "15.456"
        assertTrue(dialogo.campoConErrorDecimal && dialogo.mostrarDialogo)

        dialogo.botonEntendido.doClick()
        pantalla.campoAlcance.text = "15.46"
        assertFalse(dialogo.campoConErrorDecimal || dialogo.mostrarDialogo)

        pantalla.campoAltura.text = "5.10"
        assertFalse(dialogo.mostrarDialogo)
        pantalla.campoVelocidadX.text = "12,123"
        assertTrue(dialogo.campoConErrorDecimal && dialogo.mostrarDialogo)
    }
}
