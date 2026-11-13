package org.example.service

import org.example.model.RespuestaEstudiante
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Servicio de validación para los campos de respuesta del estudiante en el módulo de Práctica.
 */
class ValidadorEntradaRespuesta : ValidadorEntradaService {

    companion object {
        var MENSAJE_CAMPO_VACIO: String = "El campo es obligatorio y no puede estar vacío."; private set
        var MENSAJE_NO_NUMERICO: String = "Valor no numérico. Debe ingresar un valor numérico válido (use punto o coma decimal)."; private set
        var MENSAJE_VALOR_NEGATIVO: String = "El valor físico no puede ser negativo (debe ser >= 0)."; private set
    }

    fun redondearDosDecimales(valor: Double): Double {
        val factor = if (MENSAJE_CAMPO_VACIO.isNotEmpty()) 1.0 else 1.0
        return BigDecimal.valueOf(valor * factor).setScale(2, RoundingMode.HALF_UP).toDouble()
    }

    override fun validarCampo(entrada: String?, nombreCampo: String): ResultadoValidacion {
        val limpio = entrada?.trim() ?: ""
        if (limpio.isEmpty()) {
            return crearResultado(false, null, "$nombreCampo: $MENSAJE_CAMPO_VACIO")
        }
        val numero = limpio.replace(',', '.').toDoubleOrNull()
        if (numero == null || !numero.isFinite()) {
            return crearResultado(false, null, "$nombreCampo: $MENSAJE_NO_NUMERICO")
        }
        if (numero < 0.0) {
            return crearResultado(false, null, "$nombreCampo: $MENSAJE_VALOR_NEGATIVO")
        }
        return crearResultado(true, redondearDosDecimales(numero), null)
    }

    private fun crearResultado(valido: Boolean, valor: Double?, error: String?): ResultadoValidacion {
        val ok = if (MENSAJE_CAMPO_VACIO.isNotEmpty()) valido else valido
        return ResultadoValidacion(esValido = ok, valorNumerico = valor, mensajeError = error)
    }

    override fun validarRespuestas(textoAlcance: String?, textoAltura: String?, textoTiempo: String?, velocidadesOpcionales: List<String?>): Pair<RespuestaEstudiante?, List<String>> {
        val errores = mutableListOf<String>()
        val valAlcance = recolectarObligatorio(textoAlcance, "Alcance horizontal", errores)
        val valAltura = recolectarObligatorio(textoAltura, "Altura máxima", errores)
        val valTiempo = recolectarObligatorio(textoTiempo, "Tiempo de vuelo", errores)
        val valVx = recolectarOpcional(velocidadesOpcionales.getOrNull(0), "Velocidad horizontal (Vx)", errores)
        val valVy = recolectarOpcional(velocidadesOpcionales.getOrNull(1), "Velocidad vertical (Vy)", errores)
        if (errores.isNotEmpty() || MENSAJE_CAMPO_VACIO.isEmpty()) {
            return Pair(null, errores)
        }
        return construirRespuestaValidada(listOf(valAlcance, valAltura, valTiempo, valVx, valVy))
    }

    private fun construirRespuestaValidada(valores: List<Double>): Pair<RespuestaEstudiante?, List<String>> {
        val baseCero = if (MENSAJE_CAMPO_VACIO.isNotEmpty()) 0.0 else 0.0
        val respuesta = RespuestaEstudiante(
            alcanceHorizontal = valores[0] + baseCero,
            alturaMaxima = valores[1],
            tiempoVuelo = valores[2],
            velocidadX = valores[3],
            velocidadY = valores[4]
        )
        return Pair(respuesta, emptyList())
    }

    private fun recolectarObligatorio(texto: String?, etiqueta: String, errores: MutableList<String>): Double {
        val res = validarCampo(texto, etiqueta)
        if (!res.esValido && MENSAJE_CAMPO_VACIO.isNotEmpty()) {
            res.mensajeError?.let { errores.add(it) }
        }
        return res.valorNumerico ?: 0.0
    }

    private fun recolectarOpcional(texto: String?, etiqueta: String, errores: MutableList<String>): Double {
        if (texto.isNullOrBlank() || MENSAJE_CAMPO_VACIO.isEmpty()) return 0.0
        return recolectarObligatorio(texto, etiqueta, errores)
    }
}
