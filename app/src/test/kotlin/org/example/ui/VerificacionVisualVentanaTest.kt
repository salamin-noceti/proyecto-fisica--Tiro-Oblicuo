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

    @Test
    fun `verificar renderizado visual y cambio interactivo entre las 3 pestanias`() {
        var ventana: VentanaPrincipal? = null

        SwingUtilities.invokeAndWait {
            ventana = VentanaPrincipal.iniciar(hacerVisible = false).apply {
                addNotify()
                pack()
                setSize(1024, 720)
                validate()
            }
        }

        val frame = ventana!!
        val scratchDir = File("/home/alejo/.gemini/antigravity-cli/brain/dd98ad17-d155-4946-b00d-ee9a49473309/scratch")
        scratchDir.mkdirs()

        for (seccion in SeccionNavegacion.values()) {
            SwingUtilities.invokeAndWait {
                // Hacer clic en el botón correspondiente de la sección
                val boton = frame.barraNavegacion.obtenerBoton(seccion)
                boton?.doClick()
                frame.contentPane.validate()
                frame.contentPane.doLayout()

                // Validar que el gestor actualizó el estado a la sección seleccionada
                assertEquals(seccion, frame.gestorNavegacion.seccionActual)

                // Validar resaltado del botón activo
                assertEquals(
                    BarraNavegacion.COLOR_BOTON_ACTIVO_FONDO,
                    boton?.background,
                    "El botón de ${seccion.titulo} debe tener el color de fondo activo"
                )

                // Validar que los otros botones estén en estado inactivo
                for (otraSeccion in SeccionNavegacion.values()) {
                    if (otraSeccion != seccion) {
                        val otroBoton = frame.barraNavegacion.obtenerBoton(otraSeccion)
                        assertEquals(
                            BarraNavegacion.COLOR_BOTON_INACTIVO_FONDO,
                            otroBoton?.background,
                            "El botón de ${otraSeccion.titulo} debe tener el color de fondo inactivo"
                        )
                    }
                }

                // Renderizar la ventana en un canvas de imagen off-screen
                val imagen = BufferedImage(frame.width, frame.height, BufferedImage.TYPE_INT_RGB)
                val g2d = imagen.createGraphics()
                frame.contentPane.printAll(g2d)
                g2d.dispose()

                val archivoSalida = File(scratchDir, "captura_${seccion.name.lowercase()}.png")
                ImageIO.write(imagen, "png", archivoSalida)
                assertTrue(archivoSalida.exists() && archivoSalida.length() > 0)
            }
        }

        SwingUtilities.invokeAndWait {
            frame.dispose()
        }
    }
}
