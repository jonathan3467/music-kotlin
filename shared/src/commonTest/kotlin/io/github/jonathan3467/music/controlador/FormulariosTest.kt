package io.github.jonathan3467.music.controlador

import io.github.jonathan3467.music.Controlador.FormularioAlbum
import io.github.jonathan3467.music.Controlador.FormularioRola
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class FormulariosTest {
    private val valido = FormularioRola("Get Back", "The Beatles", "1", "1970", "Rock")

    @Test
    fun unFormulariosCoreectoNoTieneError(){
        assertNull(valido.error())
    }

    @Test
    fun elTituloNoPuedeQuedarVacio(){
        assertNotNull(valido.copy(titulo = "   ").error())
    }

    @Test
    fun laPistaDebeSerNumero(){
        assertNotNull(valido.copy(pista = "uno").error())
    }

    @Test
    fun elYearNoPuedeSerNegativo(){
        assertNotNull(valido.copy(year = "-5").error())
    }

    @Test
    fun albumSinNombreTieneError(){
        assertNotNull(FormularioAlbum("", "1970").error())
    }
}