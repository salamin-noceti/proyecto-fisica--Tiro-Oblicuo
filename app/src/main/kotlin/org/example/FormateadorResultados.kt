package org.example

import kotlin.math.pow
import kotlin.math.round

class FormateadorResultados(
    val decimalesPredeterminados: Int = 2
) {
    init {
        require(decimalesPredeterminados >= 0) {
            "La cantidad de cifras decimales debe ser mayor o igual a cero: $decimalesPredeterminados"
        }
    }

    fun redondear(valor: Double, decimales: Int = decimalesPredeterminados): Double {
        val factor = 10.0.pow(decimales)
        return round(valor * factor) / factor
    }

    fun redondearResultados(
        resultados: ResultadosTiro,
        decimales: Int = decimalesPredeterminados
    ): ResultadosTiro {
        return ResultadosTiro(
            vx = redondear(resultados.vx, decimales),
            vy = redondear(resultados.vy, decimales),
            tiempoSubida = redondear(resultados.tiempoSubida, decimales),
            tiempoTotal = redondear(resultados.tiempoTotal, decimales),
            alturaMaxima = redondear(resultados.alturaMaxima, decimales),
            alcanceHorizontal = redondear(resultados.alcanceHorizontal, decimales)
        )
    }

    fun formatearReporte(
        entrada: DatosEntrada,
        resultados: ResultadosTiro,
        decimales: Int = decimalesPredeterminados
    ): String {
        val r = redondearResultados(resultados, decimales)
        return buildString {
            appendLine("=== Tiro Oblicuo ===")
            appendLine("Velocidad inicial: ${entrada.velocidadInicial} m/s")
            appendLine("Ángulo: ${entrada.anguloGrados}°")
            appendLine()
            appendLine("Vx (componente horizontal): ${r.vx} m/s")
            appendLine("Vy (componente vertical):   ${r.vy} m/s")
            appendLine()
            appendLine("Tiempo de subida:   ${r.tiempoSubida} s")
            appendLine("Tiempo total vuelo: ${r.tiempoTotal} s")
            appendLine("Altura máxima:     ${r.alturaMaxima} m")
            append("Alcance horizontal:${r.alcanceHorizontal} m")
        }
    }
}
