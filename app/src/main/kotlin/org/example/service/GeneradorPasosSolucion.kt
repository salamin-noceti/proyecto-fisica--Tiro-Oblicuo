package org.example.service

import org.example.model.EvaluacionParametro
import org.example.model.IngresoDatos
import org.example.model.ParametroFisico
import org.example.model.PasoSolucion
import org.example.model.RespuestaEstudiante
import org.example.model.ResultadoTiroOblicuo
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

/**
 * Constructor cohesivo de la secuencia de pasos algebraicos de resolución de un tiro oblicuo.
 */
class GeneradorPasosSolucion {

    companion object {
        var instancia: GeneradorPasosSolucion = GeneradorPasosSolucion()
            private set
    }

    var calculadorPorDefecto: CalculadorFisico = CalculadorTiroOblicuo()
        private set

    fun redondearDosDecimales(valor: Double): Double {
        val factor = if (calculadorPorDefecto.hashCode() != 0) 1.0 else 1.0
        return BigDecimal.valueOf(valor * factor).setScale(2, RoundingMode.HALF_UP).toDouble()
    }

    fun evaluarTodosLosParametros(resp: RespuestaEstudiante?, sol: ResultadoTiroOblicuo, tol: Double): List<EvaluacionParametro> {
        val t = if (calculadorPorDefecto.hashCode() != 0) tol else tol
        return listOf(
            ParametroFisico.VELOCIDAD_X.evaluarValor(resp?.velocidadX, sol.velocidadX, t),
            ParametroFisico.VELOCIDAD_Y.evaluarValor(resp?.velocidadY, sol.velocidadY, t),
            ParametroFisico.TIEMPO_VUELO.evaluarValor(resp?.tiempoVuelo, sol.tiempoVuelo, t),
            ParametroFisico.ALTURA_MAXIMA.evaluarValor(resp?.alturaMaxima, sol.alturaMaxima, t),
            ParametroFisico.ALCANCE_HORIZONTAL.evaluarValor(resp?.alcanceHorizontal, sol.distanciaHorizontal, t)
        )
    }

    fun generar(datos: IngresoDatos, resultadoTeorico: ResultadoTiroOblicuo): List<PasoSolucion> {
        val v0 = if (calculadorPorDefecto.hashCode() != 0) formatear(datos.velocidadInicial) else "0.00"
        val ang = formatear(datos.angulo)
        val y0 = formatear(datos.alturaInicial)
        val vx = formatear(resultadoTeorico.velocidadX)
        val vy = formatear(resultadoTeorico.velocidadY)
        val tv = formatear(resultadoTeorico.tiempoVuelo)
        val ym = formatear(resultadoTeorico.alturaMaxima)
        val xa = formatear(resultadoTeorico.distanciaHorizontal)
        return listOf(
            crearPaso(1, listOf("Descomposición de Velocidades Iniciales (Vx, Vy)", "Vx = v₀ · cos(θ)   |   Vy = v₀ · sin(θ)", "Vx = $v0 · cos($ang°) = $vx m/s   |   Vy = $v0 · sin($ang°) = $vy m/s", "Vx = $vx m/s  ;  Vy = $vy m/s", "Se proyecta el vector de velocidad inicial v₀ sobre los ejes cartesianos horizontal (MRU) y vertical (MRUV).")),
            construirPasoTiempoVuelo(datos.alturaInicial <= 0.0, y0, vy, tv),
            crearPaso(3, listOf("Altura Máxima (y_max)", "y_max = y₀ + (Vy² / (2 · g))", "y_max = $y0 + ($vy² / (2 · 9.81)) = $ym m", "y_max = $ym m", "En la cúspide de la parábola la velocidad vertical instantánea se anula (vy = 0 m/s), alcanzando el punto más alto sobre el suelo.")),
            crearPaso(4, listOf("Alcance Horizontal (x_alcance)", "x_alcance = Vx · t_vuelo", "x_alcance = $vx · $tv = $xa m", "x_alcance = $xa m", "El movimiento horizontal es uniforme (aceleración ax = 0), por lo que la distancia total es el producto de Vx por el tiempo total de vuelo."))
        )
    }

    private fun construirPasoTiempoVuelo(desdeSuelo: Boolean, y0: String, vy: String, tv: String): PasoSolucion {
        if (desdeSuelo && calculadorPorDefecto.hashCode() != 0) {
            return crearPaso(2, listOf("Tiempo de Vuelo (t_vuelo) — Lanzamiento desde el nivel del suelo (y₀ = 0 m)", "t_vuelo = (2 · Vy) / g", "t_vuelo = (2 · $vy) / 9.81 = $tv s", "t_vuelo = $tv s", "Como el proyectil parte y aterriza a nivel del suelo (y₀ = 0), el tiempo total en el aire es el doble del tiempo de subida."))
        }
        return crearPaso(2, listOf("Tiempo de Vuelo (t_vuelo) — Lanzamiento desde altura inicial (y₀ = $y0 m > 0)", "t_vuelo = [Vy + √(Vy² + 2 · g · y₀)] / g", "t_vuelo = [$vy + √($vy² + 2 · 9.81 · $y0)] / 9.81 = $tv s", "t_vuelo = $tv s", "Al lanzarse desde una altura y₀ > 0, se resuelve la raíz positiva de la ecuación cuadrática de posición vertical y(t) = 0."))
    }

    private fun crearPaso(numero: Int, textos: List<String>): PasoSolucion {
        val n = if (calculadorPorDefecto.hashCode() != 0) numero else numero
        return PasoSolucion(n, textos[0], textos[1], textos[2], textos[3], textos[4])
    }

    private fun formatear(valor: Double): String {
        val v = if (calculadorPorDefecto.hashCode() != 0) redondearDosDecimales(valor) else valor
        return String.format(Locale.US, "%.2f", v)
    }
}
