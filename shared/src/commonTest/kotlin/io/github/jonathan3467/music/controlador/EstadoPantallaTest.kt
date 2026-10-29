package io.github.jonathan3467.music.controlador

import io.github.jonathan3467.music.Controlador.EstadoPantalla
import io.github.jonathan3467.music.modelo.Rola
import io.github.jonathan3467.music.modelo.RolaDetallada
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class EstadoPantallaTest {

    private fun detallada(id: Long, titulo: String) = RolaDetallada(
        rola = Rola(
            id = id,
            idPerformer = 1,
            idAlbum = 0,
            ruta = "/$titulo.mp3",
            titulo = titulo,
            pista = 1,
            year = 2000,
            genero = "Rock"
        ),
        performer = "Alguien",
        album = "Unknown"
    )

    private val rolas = listOf(detallada(1,"Uno"), detallada(2, "Dos"))

    @Test
    fun sinSeleccionNoHayRola(){
        assertNull(EstadoPantalla(rolas = rolas).seleccionada)
    }

    @Test
    fun encuentraLaRolaSeleccionada(){
        val estado = EstadoPantalla(rolas = rolas, idSeleccionada = 2)
        assertEquals("Dos", estado.seleccionada?.rola?.titulo)
    }

    @Test
    fun siLaRolaYaNoEstaNoHaySeleccion(){
        // pasa despues de reminar el id viejo ya no esta
        assertNull(EstadoPantalla(rolas = rolas, idSeleccionada = 99).seleccionada)
    }
}