package org.example.ui.simulacion.graficos

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Stroke

/**
 * Representa una serie de datos cartesianos a ser graficada sobre el lienzo.
 */
class SerieCartesiana(
    id: String,
    nombre: String,
    color: Color,
    puntos: List<PuntoGrafico> = emptyList(),
    trazo: Stroke = BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND),
    valorActual: Double? = null,
    unidad: String = ""
) {
    var id: String
        private set

    var nombre: String
        private set

    var color: Color
        private set

    private val _puntos: MutableList<PuntoGrafico> = puntos.toMutableList()

    val puntos: List<PuntoGrafico>
        get() = _puntos.toList()

    var trazo: Stroke
        private set

    var valorActual: Double?
        private set

    var unidad: String
        private set

    init {
        this.id = id
        this.nombre = nombre
        this.color = color
        this.trazo = trazo
        this.valorActual = valorActual
        this.unidad = unidad
    }

    fun vaciar(): SerieCartesiana {
        return SerieCartesiana(id, nombre, color, emptyList(), trazo, null, unidad)
    }

    fun conPuntos(nuevosPuntos: List<PuntoGrafico>, nuevoValorActual: Double?): SerieCartesiana {
        return SerieCartesiana(id, nombre, color, nuevosPuntos, trazo, nuevoValorActual, unidad)
    }
}
