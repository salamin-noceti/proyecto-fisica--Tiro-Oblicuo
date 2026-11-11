package org.example.ui.simulacion

import kotlin.math.min

/**
 * Responsable de la transformación geométrica entre el espacio métrico físico
 * (donde x crece hacia la derecha e y crece hacia arriba, en metros) y el espacio de coordenadas
 * del lienzo gráfico 2D de Swing (donde x crece a la derecha e y crece hacia abajo, en píxeles).
 *
 * Cumple con el Principio de Responsabilidad Única (SRP) aislando toda la matemática
 * de escala, márgenes y proyecciones de coordenadas de la vista de dibujo.
 *
 * Resuelve la separación entre el eje Y y los elementos en X=0:
 * - [margenEjeY]: Margen interno reservado exclusivamente para las etiquetas numéricas ("5m", "10m", etc.)
 *   y la línea vertical del eje Y.
 * - [espacioLanzadorX]: Separación horizontal de seguridad entre el eje Y y el origen de disparo X=0.
 * - [margenIzquierdo]: Posición del origen físico X=0, desplazado a la derecha de la escala del eje Y
 *   para garantizar que edificios, cañones o personajes jamás tapen las marcas numéricas.
 *
 * @param margenEjeY Espacio en píxeles reservado exclusivamente a la línea del eje Y y a sus
 *   etiquetas numéricas ("5m", "10m", "15m", "20m"...). Ningún sprite debe dibujarse aquí.
 * @param espacioLanzadorX Distancia de separación entre la escala del eje Y y el punto de disparo X=0.
 * @param margenIzquierdo Margen total hasta el origen X=0. Por defecto se deriva de
 *   `margenEjeY + espacioLanzadorX`, garantizando que el origen físico quede siempre justo a la
 *   derecha de la zona reservada del eje Y.
 * @param margenDerecho Margen en píxeles hacia el extremo derecho del lienzo.
 * @param margenSuperior Margen en píxeles superior para evitar que la altura máxima toque el borde.
 * @param margenInferior Margen en píxeles para el suelo y la escala horizontal.
 */
class ConversorCoordenadas(
    val margenEjeY: Double = 60.0,
    val espacioLanzadorX: Double = 40.0,
    val margenIzquierdo: Double = margenEjeY + espacioLanzadorX,
    val margenDerecho: Double = 50.0,
    val margenSuperior: Double = 50.0,
    val margenInferior: Double = 60.0
) {

    /**
     * Calcula el factor de escala isotrópico (píxeles por metro) para que toda la trayectoria
     * física quepa proporcionalmente dentro del lienzo visible sin deformación angular.
     *
     * @param anchoCanvas Ancho actual en píxeles del lienzo.
     * @param altoCanvas Alto actual en píxeles del lienzo.
     * @param xMaxFisico Alcance horizontal máximo en metros esperado.
     * @param yMaxFisico Altura máxima en metros esperada.
     * @return Factor de conversión en píxeles por metro.
     */
    fun calcularEscala(
        anchoCanvas: Int,
        altoCanvas: Int,
        xMaxFisico: Double,
        yMaxFisico: Double
    ): Double {
        val anchoUtil = (anchoCanvas - margenIzquierdo - margenDerecho).coerceAtLeast(20.0)
        val altoUtil = (altoCanvas - margenSuperior - margenInferior).coerceAtLeast(20.0)

        // Se aplica un margen de seguridad porcentual para que el proyectil no toque los bordes
        val xDominio = (xMaxFisico * 1.15).coerceAtLeast(10.0)
        val yDominio = (yMaxFisico * 1.20).coerceAtLeast(5.0)

        val escalaX = anchoUtil / xDominio
        val escalaY = altoUtil / yDominio

        return min(escalaX, escalaY).coerceAtLeast(0.01)
    }

    /**
     * Convierte una coordenada horizontal física en metros a su posición en píxeles en pantalla.
     */
    fun metroAPixelX(
        xMetros: Double,
        anchoCanvas: Int,
        altoCanvas: Int,
        xMaxFisico: Double,
        yMaxFisico: Double
    ): Double {
        val escala = calcularEscala(anchoCanvas, altoCanvas, xMaxFisico, yMaxFisico)
        return margenIzquierdo + (xMetros * escala)
    }

    /**
     * Convierte una coordenada vertical física en metros a su posición en píxeles en pantalla.
     * Invierte el eje vertical debido al sistema de coordenadas de pantalla (y hacia abajo).
     */
    fun metroAPixelY(
        yMetros: Double,
        anchoCanvas: Int,
        altoCanvas: Int,
        xMaxFisico: Double,
        yMaxFisico: Double
    ): Double {
        val escala = calcularEscala(anchoCanvas, altoCanvas, xMaxFisico, yMaxFisico)
        val suelo = sueloPixelY(altoCanvas)
        return suelo - (yMetros * escala)
    }

    /**
     * Retorna la posición vertical en píxeles correspondiente a la línea del suelo (y = 0 m).
     */
    fun sueloPixelY(altoCanvas: Int): Double {
        return (altoCanvas - margenInferior).coerceAtLeast(0.0)
    }

    /**
     * Retorna la posición horizontal en píxeles correspondiente a la línea y marcas del eje Y.
     */
    fun ejeYPixelX(): Double {
        return min(margenEjeY, (margenIzquierdo - 30.0).coerceAtLeast(10.0))
    }

    /**
     * Retorna la posición horizontal en píxeles correspondiente al origen físico de disparo (x = 0 m).
     */
    fun origenPixelX(): Double {
        return margenIzquierdo
    }
}
