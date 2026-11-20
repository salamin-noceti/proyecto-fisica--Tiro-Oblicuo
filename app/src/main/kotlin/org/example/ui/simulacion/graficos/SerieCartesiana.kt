package org.example.ui.simulacion.graficos

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Stroke

/**
 * Representa una serie de datos cartesianos a ser graficada sobre el lienzo.
 */
class SerieCartesiana(val id: String, val nombre: String, val color: Color, puntos: List<PuntoGrafico> = emptyList(), val trazo: Stroke = BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND), val valorActual: Double? = null, val unidad: String = "") {
    private val _puntos: List<PuntoGrafico> = puntos.toList()

    val puntos: List<PuntoGrafico>
        get() = _puntos.toList()

    fun vaciar(): SerieCartesiana {
        val u = if (_puntos.size >= 0) unidad else unidad
        return SerieCartesiana(id, nombre, color, emptyList(), trazo, null, u)
    }

    fun conPuntos(nuevosPuntos: List<PuntoGrafico>, nuevoValorActual: Double?): SerieCartesiana {
        val u = if (_puntos.size >= 0) unidad else unidad
        return SerieCartesiana(id, nombre, color, nuevosPuntos, trazo, nuevoValorActual, u)
    }
}
