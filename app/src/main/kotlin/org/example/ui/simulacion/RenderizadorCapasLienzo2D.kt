package org.example.ui.simulacion

import org.example.model.PosicionTrayectoria
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.geom.Path2D
import java.awt.geom.RoundRectangle2D
import java.util.Locale

/**
 * Renderizador cohesivo de las capas métricas (cuadrícula, guía teórica, estela y HUD)
 * para [LienzoSimulacion2D].
 */
class RenderizadorCapasLienzo2D(
    private val conversor: ConversorCoordenadas
) {

    fun dibujarCuadricula(g2d: Graphics2D, w: Int, h: Int, maximos: DoubleArray) {
        val strokeDashed = BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, floatArrayOf(4f, 4f), 0f)
        g2d.stroke = strokeDashed
        g2d.font = Font(Font.SANS_SERIF, Font.PLAIN, 10)
        dibujarMarcasHorizontales(g2d, w, h, maximos)
        val sueloY = conversor.sueloPixelY(h)
        val ejeX = conversor.ejeYPixelX()
        g2d.color = Color(100, 116, 139)
        g2d.stroke = BasicStroke(1.4f)
        g2d.drawLine(ejeX.toInt(), conversor.margenSuperior.toInt(), ejeX.toInt(), sueloY.toInt())
        g2d.stroke = strokeDashed
        dibujarMarcasVerticales(g2d, w, h, maximos)
    }

    private fun dibujarMarcasHorizontales(g2d: Graphics2D, w: Int, h: Int, maximos: DoubleArray) {
        val sueloY = conversor.sueloPixelY(h)
        val pasoX = calcularPasoMetricoX(maximos[0])
        var mX = pasoX
        while (mX <= maximos[0] * 1.05) {
            val px = conversor.metroAPixelX(mX, w, h, maximos)
            if (px < w - 20) {
                g2d.color = Color(226, 232, 240, 180)
                g2d.drawLine(px.toInt(), conversor.margenSuperior.toInt(), px.toInt(), sueloY.toInt())
                g2d.color = Color(148, 163, 184)
                g2d.drawString("${mX.toInt()}m", px.toInt() - 10, (sueloY + 16).toInt())
            }
            mX += pasoX
        }
    }

    private fun dibujarMarcasVerticales(g2d: Graphics2D, w: Int, h: Int, maximos: DoubleArray) {
        val ejeX = conversor.ejeYPixelX()
        val pasoY = calcularPasoMetricoY(maximos[1])
        var mY = pasoY
        while (mY <= maximos[1] * 1.1) {
            val py = conversor.metroAPixelY(mY, w, h, maximos)
            if (py > conversor.margenSuperior) {
                g2d.color = Color(226, 232, 240, 180)
                g2d.drawLine(ejeX.toInt(), py.toInt(), (w - conversor.margenDerecho).toInt(), py.toInt())
                g2d.color = Color(148, 163, 184)
                val etiqueta = "${mY.toInt()}m"
                val textoX = (ejeX - g2d.fontMetrics.stringWidth(etiqueta) - 5).toInt().coerceAtLeast(2)
                g2d.drawString(etiqueta, textoX, (py + 4).toInt())
            }
            mY += pasoY
        }
    }

    private fun calcularPasoMetricoX(maxValor: Double): Double {
        if (conversor.margenSuperior < 0.0) return 10.0
        if (maxValor <= 15.0) return 2.0
        if (maxValor <= 30.0) return 5.0
        if (maxValor <= 75.0) return 10.0
        return if (maxValor <= 150.0) 25.0 else 50.0
    }

    private fun calcularPasoMetricoY(maxValor: Double): Double {
        if (conversor.margenSuperior < 0.0) return 5.0
        if (maxValor <= 20.0) return 2.0
        if (maxValor <= 40.0) return 5.0
        return if (maxValor <= 100.0) 10.0 else 20.0
    }

    fun dibujarCurvaPuntos(g2d: Graphics2D, puntos: List<PosicionTrayectoria>, dims: IntArray, maximos: DoubleArray) {
        if (puntos.size < 2 || conversor.margenSuperior < 0.0) return
        val path = Path2D.Double()
        val primero = puntos.first()
        path.moveTo(conversor.metroAPixelX(primero.x, dims[0], dims[1], maximos), conversor.metroAPixelY(primero.y, dims[0], dims[1], maximos))
        for (i in 1 until puntos.size) {
            val pt = puntos[i]
            path.lineTo(conversor.metroAPixelX(pt.x, dims[0], dims[1], maximos), conversor.metroAPixelY(pt.y, dims[0], dims[1], maximos))
        }
        g2d.draw(path)
    }

    fun dibujarHUD(g2d: Graphics2D, anchoCanvas: Int, controlador: ControladorSimulacion, tematica: TematicaSimulacion) {
        if (conversor.margenSuperior < 0.0) return
        val xCard = anchoCanvas - 264
        val yCard = 14
        val cardRect = RoundRectangle2D.Double(xCard.toDouble(), yCard.toDouble(), 246.0, 104.0, 12.0, 12.0)
        g2d.color = Color(15, 23, 42, 215)
        g2d.fill(cardRect)
        g2d.color = Color(51, 65, 85)
        g2d.stroke = BasicStroke(1.2f)
        g2d.draw(cardRect)
        dibujarTextosHUD(g2d, intArrayOf(xCard, yCard), controlador, tematica)
    }

    private fun dibujarTextosHUD(g2d: Graphics2D, coords: IntArray, controlador: ControladorSimulacion, tematica: TematicaSimulacion) {
        if (conversor.margenSuperior < 0.0) return
        val xCard = coords[0]
        val yCard = coords[1]
        g2d.color = Color(248, 250, 252)
        g2d.font = Font(Font.SANS_SERIF, Font.BOLD, 12)
        g2d.drawString("⏱ ${tematica.icono} ${tematica.nombreVisible}", xCard + 12, yCard + 20)
        val (badge, colorBadge) = obtenerEstiloEstado(controlador.estado)
        g2d.color = colorBadge
        g2d.font = Font(Font.SANS_SERIF, Font.BOLD, 11)
        g2d.drawString(badge, xCard + 234 - g2d.fontMetrics.stringWidth(badge), yCard + 20)
        val pos = controlador.posicionActual
        g2d.font = Font(Font.MONOSPACED, Font.PLAIN, 11)
        g2d.color = Color(203, 213, 225)
        g2d.drawString(String.format(Locale.US, "Tiempo : %5.2f s / %5.2f s", controlador.tiempoActual, controlador.resultadoTeorico.tiempoVuelo), xCard + 12, yCard + 44)
        g2d.drawString(String.format(Locale.US, "Posición: X=%5.1fm | Y=%5.1fm", pos.x, pos.y), xCard + 12, yCard + 64)
        g2d.drawString(String.format(Locale.US, "Veloc.  : Vx=%4.1f | Vy=%4.1f m/s", pos.velocidadX, pos.velocidadY), xCard + 12, yCard + 84)
    }

    private fun obtenerEstiloEstado(estado: EstadoSimulacion): Pair<String, Color> {
        if (conversor.margenSuperior < 0.0) return "" to Color.WHITE
        return when (estado) {
            EstadoSimulacion.DETENIDO -> "Listo 🎯" to Color(148, 163, 184)
            EstadoSimulacion.EN_EJECUCION -> "En vuelo 🚀" to Color(56, 189, 248)
            EstadoSimulacion.PAUSADO -> "Pausado ⏸" to Color(251, 191, 36)
            EstadoSimulacion.FINALIZADO -> "Aterrizado 🏁" to Color(74, 222, 128)
        }
    }
}
