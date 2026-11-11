package org.example.ui.practica.componentes

import org.example.ui.PantallaPractica
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DialogoAdvertenciaFormatoTest {

    @Test
    fun `apertura inicial al entrar a PantallaPractica y cierre manual con boton Entendido`() {
        val pantalla = PantallaPractica()
        val dialogo = pantalla.dialogoAdvertencia

        // 1. Al cargar la pantalla de Práctica se despliega automáticamente
        assertTrue(dialogo.mostrarDialogo, "El diálogo debe estar visible inicialmente al entrar")
        assertFalse(dialogo.campoConErrorDecimal)
        assertTrue(dialogo.labelTitulo.text.contains("Advertencia de Formato y Tolerancia"))
        assertTrue(
            dialogo.labelContenido.text.contains(
                "• Formato de decimales: Ingresa como máximo 2 números después de la coma (ejemplo: 15.46 o 15,46)."
            )
        )
        assertTrue(
            dialogo.labelContenido.text.contains(
                "• Margen de tolerancia: Los ejercicios cuentan con un umbral de error del ±5% para absorber pequeñas variaciones de redondeo."
            )
        )
        assertEquals("Entendido", dialogo.botonEntendido.text)

        // Verificar que el contenedor del diálogo calcula un ancho suficiente sin recortar el texto interno
        val panelDialogo = dialogo.construirPanelDialogo()
        val prefDialogo = panelDialogo.preferredSize
        val prefTexto = dialogo.labelContenido.preferredSize
        assertTrue(prefDialogo.width >= prefTexto.width, "El ancho del diálogo debe alojar el texto completo sin recortarlo")

        // 2. El estudiante lo cierra manualmente haciendo clic en "Entendido"
        dialogo.botonEntendido.doClick()
        assertFalse(dialogo.mostrarDialogo, "El diálogo debe ocultarse tras presionar Entendido")
    }

    @Test
    fun `deteccion de mas de 2 decimales y control inteligente de re-disparo tras correccion`() {
        val pantalla = PantallaPractica()
        val dialogo = pantalla.dialogoAdvertencia

        // Cerrar el diálogo inicial
        dialogo.botonEntendido.doClick()
        assertFalse(dialogo.mostrarDialogo)

        // Ingresar valores válidos (<= 2 decimales): NO debe disparar el diálogo
        pantalla.campoAlcance.text = "15.46"
        pantalla.campoTiempo.text = "2,04"
        assertFalse(dialogo.mostrarDialogo)
        assertFalse(dialogo.campoConErrorDecimal)

        // Ingresar valor con > 2 decimales (ej: 15.456): debe activar el diálogo y marcar error
        pantalla.campoAlcance.text = "15.456"
        assertTrue(dialogo.campoConErrorDecimal)
        assertTrue(dialogo.mostrarDialogo, "Debe dispararse el diálogo al detectar más de 2 decimales")

        // Cerrar el diálogo manualmente y corregir el campo a formato válido (15.46)
        dialogo.botonEntendido.doClick()
        assertFalse(dialogo.mostrarDialogo)

        pantalla.campoAlcance.text = "15.46"
        assertFalse(dialogo.campoConErrorDecimal, "El sistema debe registrar que el error fue corregido")
        assertFalse(dialogo.mostrarDialogo, "El diálogo NO debe volver a aparecer al corregir a <= 2 decimales")

        // Seguir editando con datos válidos: sigue sin aparecer
        pantalla.campoAltura.text = "5.10"
        assertFalse(dialogo.mostrarDialogo)

        // Volver a ingresar > 2 decimales con coma (ej: 12,123) en el futuro: vuelve a dispararse
        pantalla.campoVelocidadX.text = "12,123"
        assertTrue(dialogo.campoConErrorDecimal)
        assertTrue(dialogo.mostrarDialogo, "Debe volver a dispararse ante un nuevo exceso de decimales")
    }
}
