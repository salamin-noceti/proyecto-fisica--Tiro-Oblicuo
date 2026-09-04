// Interfaz que define QUÉ puede calcular un servicio de física.
// Según el principio DIP (Dependency Inversion) de SOLID:
//   El código que usa esta interfaz no depende de una implementación concreta,
//   sino de una abstracción. Así podemos cambiar el cálculo sin romper el resto.
package org.example.service

import org.example.model.IngresoDatos
import org.example.model.ResultadoTiroOblicuo

//si queres calcular fisica, estas obligado a tener una funcion llamada calcular
interface CalculadorFisico {
    fun calcular(datos: IngresoDatos): ResultadoTiroOblicuo
}
