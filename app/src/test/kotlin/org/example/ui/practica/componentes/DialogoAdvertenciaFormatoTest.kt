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
        val dialogo = pantalla.obtenerDialogoAdvertencia()
        val datos = dialogo.obtenerDatosVisuales()
        assertTrue(dialogo.mostrarDialogo, "El diálogo debe estar visible inicialmente al entrar")
        assertFalse(dialogo.campoConErrorDecimal)
        assertTrue(datos["titulo"]?.contains("Advertencia de Formato y Tolerancia") == true)
        assertTrue(datos["contenido"]?.contains("• Formato de decimales: Ingresa como máximo 2 números después de la coma (ejemplo: 15.46 o 15,46).") == true)
        assertTrue(datos["contenido"]?.contains("• Margen de tolerancia: Los ejercicios cuentan con un umbral de error del ±5% para absorber pequeñas variaciones de redondeo.") == true)
        assertEquals("Entendido", datos["boton"])

        val panelDialogo = dialogo.construirPanelDialogo()
        val anchoContenido = datos["anchoContenido"]?.toIntOrNull() ?: 0
        assertTrue(panelDialogo.preferredSize.width >= anchoContenido)
        dialogo.presionarEntendido()
        assertFalse(dialogo.mostrarDialogo, "El diálogo debe ocultarse tras presionar Entendido")
    }

    @Test
    fun testDeteccionDeMasDe2DecimalesYControlDeRedisparoTrasCorreccion() {
        val pantalla = crearPractica()
        val dialogo = pantalla.obtenerDialogoAdvertencia()
        dialogo.presionarEntendido()
        assertFalse(dialogo.mostrarDialogo)

        pantalla.configurarEntrada(campos = mapOf(0 to "15.46", 2 to "2,04"))
        assertFalse(dialogo.mostrarDialogo || dialogo.campoConErrorDecimal)

        pantalla.configurarEntrada(campos = mapOf(0 to "15.456"))
        assertTrue(dialogo.campoConErrorDecimal && dialogo.mostrarDialogo)

        dialogo.presionarEntendido()
        pantalla.configurarEntrada(campos = mapOf(0 to "15.46"))
        assertFalse(dialogo.campoConErrorDecimal || dialogo.mostrarDialogo)

        pantalla.configurarEntrada(campos = mapOf(1 to "5.10"))
        assertFalse(dialogo.mostrarDialogo)
        pantalla.configurarEntrada(campos = mapOf(3 to "12,123"))
        assertTrue(dialogo.campoConErrorDecimal && dialogo.mostrarDialogo)
    }
}
