package org.example.ui

import org.example.navigation.SeccionNavegacion
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.SwingUtilities
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VerificacionVisualVentanaTest {

    private val scratchDir = File("/home/alejo/.gemini/antigravity-cli/brain/dd98ad17-d155-4946-b00d-ee9a49473309/scratch")

    @Test
    fun testVerificarRenderizadoVisualYCambioInteractivoEntrePestanias() {
        var ventana: VentanaPrincipal? = null
        SwingUtilities.invokeAndWait {
            ventana = VentanaPrincipal.iniciar(hacerVisible = false).apply {
                addNotify(); pack(); setSize(1024, 720); validate()
            }
        }
        val frame = ventana!!
        scratchDir.mkdirs()
        for (seccion in SeccionNavegacion.values()) {
            SwingUtilities.invokeAndWait { validarYCapturarSeccion(frame, seccion) }
        }
        SwingUtilities.invokeAndWait { frame.dispose() }
    }

    private fun validarYCapturarSeccion(frame: VentanaPrincipal, seccion: SeccionNavegacion) {
        val boton = frame.barraNavegacion.obtenerBoton(seccion)
        boton?.doClick()
        frame.contentPane.validate()
        frame.contentPane.doLayout()
        assertEquals(seccion, frame.gestorNavegacion.seccionActual)
        assertEquals(BarraNavegacion.COLOR_BOTON_ACTIVO_FONDO, boton?.background)
        for (otra in SeccionNavegacion.values()) {
            if (otra != seccion) {
                assertEquals(BarraNavegacion.COLOR_BOTON_INACTIVO_FONDO, frame.barraNavegacion.obtenerBoton(otra)?.background)
            }
        }
        val imagen = BufferedImage(frame.width, frame.height, BufferedImage.TYPE_INT_RGB)
        val g2d = imagen.createGraphics()
        frame.contentPane.printAll(g2d)
        g2d.dispose()
        val archivoSalida = File(scratchDir, "captura_${seccion.name.lowercase()}.png")
        ImageIO.write(imagen, "png", archivoSalida)
        assertTrue(archivoSalida.exists() && archivoSalida.length() > 0)
    }
}
