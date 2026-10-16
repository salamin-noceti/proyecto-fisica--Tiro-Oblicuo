package org.example

import org.example.model.Dificultad
import org.example.service.GeneradorEjerciciosBancoAdapter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GeneradorEjerciciosBancoAdapterTest {

    @Test
    fun `obtener ejercicio por cada dificultad disponible`() {
        val adapter = GeneradorEjerciciosBancoAdapter()

        val facil = adapter.obtenerEjercicioPorDificultad(Dificultad.FACIL)
        val medio = adapter.obtenerEjercicioPorDificultad(Dificultad.MEDIO)
        val dificil = adapter.obtenerEjercicioPorDificultad(Dificultad.DIFICIL)

        assertEquals(Dificultad.FACIL, facil.dificultad)
        assertEquals(Dificultad.MEDIO, medio.dificultad)
        assertEquals(Dificultad.DIFICIL, dificil.dificultad)

        assertNotNull(facil.enunciado)
        assertNotNull(medio.enunciado)
        assertNotNull(dificil.enunciado)
    }

    @Test
    fun `rotacion ciclica de ejercicios por dificultad`() {
        val adapter = GeneradorEjerciciosBancoAdapter()

        val ej1 = adapter.obtenerEjercicioPorDificultad(Dificultad.FACIL)
        val ej2 = adapter.obtenerEjercicioPorDificultad(Dificultad.FACIL)
        val ej3 = adapter.obtenerEjercicioPorDificultad(Dificultad.FACIL)
        val ej4 = adapter.obtenerEjercicioPorDificultad(Dificultad.FACIL)

        assertEquals(1, ej1.id)
        assertEquals(2, ej2.id)
        assertEquals(3, ej3.id)
        assertEquals(1, ej4.id) // Vuelve a empezar cíclicamente
    }
}
